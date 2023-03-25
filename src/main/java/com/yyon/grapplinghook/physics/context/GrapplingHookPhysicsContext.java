package com.yyon.grapplinghook.physics.context;

import com.yyon.grapplinghook.client.GrappleModClient;
import com.yyon.grapplinghook.client.keybind.GrappleModKey;
import com.yyon.grapplinghook.client.keybind.MinecraftKey;
import com.yyon.grapplinghook.config.GrappleModConfig;
import com.yyon.grapplinghook.content.entity.grapplinghook.GrapplinghookEntity;
import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.content.entity.grapplinghook.RopeSegmentHandler;
import com.yyon.grapplinghook.network.NetworkManager;
import com.yyon.grapplinghook.network.serverbound.GrappleEndMessage;
import com.yyon.grapplinghook.network.serverbound.PlayerMovementMessage;
import com.yyon.grapplinghook.customization.CustomizationVolume;
import com.yyon.grapplinghook.util.GrappleModUtils;
import com.yyon.grapplinghook.util.Vec;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.HashSet;

import static com.yyon.grapplinghook.content.registry.GrappleModCustomizationProperties.*;


public class GrapplingHookPhysicsContext {
	public int entityId;
	public Level world;
	public Entity entity;
	
	public HashSet<GrapplinghookEntity> grapplehookEntities = new HashSet<>();
	public HashSet<Integer> grapplehookEntityIds = new HashSet<>();
	
	public boolean attached = true;
	
	public Vec motion;
	
	public double playerForward = 0;
	public double playerStrafe = 0;
	public boolean playerJump = false;
	public boolean playerSneak = false;
	public Vec playerMovementUnrotated = new Vec(0,0,0);
	public Vec playerMovement = new Vec(0,0,0);

	private boolean prevOnGround = false;

	public int onGroundTimer;
	public int maxOnGroundTimer = 3;
	
	public double maxLen;
	
	public double playerMovementMult = 0;

	private double repelMaxPush = 0.3;

	public int controllerId;

	public boolean rocketKeyDown = false;
	private double rocketProgression;

	private int ticksSinceLastWallrunSoundEffect = 0;

	private boolean isOnWall = false;
	private Vec wallDirection = null;
	private BlockHitResult wallrunRaytraceResult = null;

	private final CustomizationVolume custom;
	
	public GrapplingHookPhysicsContext(int grapplehookEntityId, int entityId, Level world, int controllerId, CustomizationVolume custom) {
		this.entityId = entityId;
		this.world = world;
		this.custom = custom;
		
		if (this.custom != null) {
			this.playerMovementMult = this.custom.get(MOVE_SPEED_MULTIPLIER.get());
			this.maxLen = custom.get(MAX_ROPE_LENGTH.get());
		}
		
		this.controllerId = controllerId;
		
		this.entity = world.getEntity(entityId);
		this.motion = Vec.motionVec(entity);
		
		// undo friction
		Vec newmotion = new Vec(entity.position().x - entity.xOld, entity.position().y - entity.yOld, entity.position().z - entity.zOld);
		if (newmotion.x/motion.x < 2 && motion.x/newmotion.x < 2 && newmotion.y/motion.y < 2 && motion.y/newmotion.y < 2 && newmotion.z/motion.z < 2 && motion.z/newmotion.z < 2) {
			this.motion = newmotion;
		}

		this.onGroundTimer = 0;

		if (grapplehookEntityId != -1) {
			Entity grapplehookEntity = world.getEntity(grapplehookEntityId);
			if (grapplehookEntity != null && grapplehookEntity.isAlive() && grapplehookEntity instanceof GrapplinghookEntity grapple) {
				this.addHookEntity(grapple);

			} else {
				GrappleMod.LOGGER.warn("Grappling Hook Controller without a grappling hook entity!");
				this.unattach();
			}
		}
		
		if (custom != null && custom.get(ROCKET_ATTACHED.get())) {
			GrappleModClient.get().updateRocketRegen(custom.get(ROCKET_FUEL_DEPLETION_RATIO.get()), custom.get(ROCKET_REFUEL_RATIO.get()));
		}
	}
	
	public void unattach() {
		if (GrappleModClient.get().unregisterController(this.entityId) == null) return;

		this.attached = false;

		if (this.controllerId != GrappleModUtils.AIR_FRICTION_ID) {
			NetworkManager.packetToServer(new GrappleEndMessage(this.entityId, this.grapplehookEntityIds));
			GrappleModClient.get().createControl(GrappleModUtils.AIR_FRICTION_ID, -1, this.entityId, this.entity.level(), new Vec(0,0,0), null, this.custom);
		}
	}
	
	
	public void doClientTick() {
		if (!this.attached) return;

		if (this.entity == null || !this.entity.isAlive()) {
			this.unattach();
		} else {
			this.updatePlayerPos();
		}
	}
		
	public void receivePlayerMovementMessage(float strafe, float forward, boolean sneak) {
		this.playerForward = forward;
		this.playerStrafe = strafe;
		this.playerSneak = sneak;
		this.playerMovementUnrotated = new Vec(strafe, 0, forward);
		this.playerMovement = playerMovementUnrotated.rotateYaw((float) (this.entity.getYRot() * (Math.PI / 180.0)));
	}
	
	public void updatePlayerPos() {
		Entity entity = this.entity;
		
		if (!this.attached) return;
		if(entity == null) return;

		if (entity.getVehicle() != null) {
			this.unattach();
			this.updateServerPos();
			return;
		}

		this.normalGround(false);
		this.normalCollisions(false);
		this.applyAirFriction();

		Vec playerPos = Vec.positionVec(entity).add(new Vec(0, entity.getEyeHeight(), 0));
		Vec additionalMotion = new Vec(0,0,0);
		Vec gravity = new Vec(0, -0.05, 0);

		this.motion.mutableAdd(gravity);

		boolean doJump = false;
		double jumpSpeed = 0;
		boolean isClimbing = false;

		// is motor active?
		boolean motor = false;
		if (this.custom.get(MOTOR_ATTACHED.get())) {
			boolean isActive = this.custom.get(MOTOR_ACTIVATION.get()).isActive(GrappleModKey.key_motoronoff);

			if(isActive) motor = true;
		}

		boolean close = false;

		Vec averagemotiontowards = new Vec(0, 0, 0);

		double min_spherevec_dist = 99999;

		for (GrapplinghookEntity hookEntity : this.grapplehookEntities) {
			Vec hookPos = Vec.positionVec(hookEntity);
			RopeSegmentHandler segmentHandler = hookEntity.getSegmentHandler();

			// Update segment handler (handles rope bends)
			if (this.custom.get(BLOCK_PHASE_ROPE.get())) {
				segmentHandler.updatePos(hookPos, playerPos, hookEntity.ropeLength);
			} else {
				segmentHandler.update(hookPos, playerPos, hookEntity.ropeLength, false);
			}

			// vectors along rope
			Vec anchor = segmentHandler.getClosest(hookPos);
			double distToAnchor = segmentHandler.getDistToAnchor();
			double remainingLength = motor
					? Math.max(this.custom.get(MAX_ROPE_LENGTH.get()), hookEntity.ropeLength) - distToAnchor
					: hookEntity.ropeLength - distToAnchor;

			Vec oldspherevec = playerPos.sub(anchor);
			Vec spherevec = oldspherevec.withMagnitude(remainingLength);
			Vec spherechange = spherevec.sub(oldspherevec);

			if (spherevec.length() < min_spherevec_dist) {min_spherevec_dist = spherevec.length();}

			averagemotiontowards.mutableAdd(spherevec.withMagnitude(-1));

			if (motor) {
				hookEntity.ropeLength = distToAnchor + oldspherevec.length();
			}

			// snap to rope length
			if (oldspherevec.length() >= remainingLength) {
				if (oldspherevec.length() - remainingLength > GrappleModConfig.getConf().grapplinghook.other.rope_snap_buffer) {
					// if rope is too long, the rope snaps

					this.unattach();
					this.updateServerPos();
					return;
				} else {
					additionalMotion = spherechange;
				}
			}

			double dist = oldspherevec.length();

			this.applyCalculatedTaut(dist, hookEntity);

			// handle keyboard input (jumping and climbing)
			if (entity instanceof Player player) {
				boolean isJumping = GrappleModClient.get().isKeyDown(GrappleModKey.key_jumpanddetach) && !playerJump;
				playerJump = GrappleModClient.get().isKeyDown(GrappleModKey.key_jumpanddetach);

				if (isJumping && onGroundTimer >= 0) {
					// jumping
					double timer = GrappleModClient.get().getTimeSinceLastRopeJump(this.entity.level());
					if (timer > GrappleModConfig.getConf().grapplinghook.other.rope_jump_cooldown_s * 20.0) {
						doJump = true;
						jumpSpeed = this.getJumpPower(player, spherevec, hookEntity);
					}
				}

				if (GrappleModClient.get().isKeyDown(GrappleModKey.key_slow)) {
					// slow down
					Vec motiontorwards = spherevec.withMagnitude(-0.1);
					motiontorwards = new Vec(motiontorwards.x, 0, motiontorwards.z);
					if (motion.dot(motiontorwards) < 0) {
						motion.mutableAdd(motiontorwards);
					}

					Vec newmotion = dampenMotion(motion, motiontorwards);
					motion = new Vec(newmotion.x, motion.y, newmotion.z);

				}

				if ((GrappleModClient.get().isKeyDown(GrappleModKey.key_climb) || GrappleModClient.get().isKeyDown(GrappleModKey.key_climbup) || GrappleModClient.get().isKeyDown(GrappleModKey.key_climbdown)) && !motor) {
					isClimbing = true;
					if (anchor.y > playerPos.y) {
						// climb up/down rope
						double climbup = 0;
						if (GrappleModClient.get().isKeyDown(GrappleModKey.key_climb)) {
							climbup = playerForward;
							if (GrappleModClient.get().isMovingSlowly(this.entity)) {
								climbup = climbup / 0.3D;
							}
							if (climbup > 1) {climbup = 1;} else if (climbup < -1) {climbup = -1;}
						}
						else if (GrappleModClient.get().isKeyDown(GrappleModKey.key_climbup)) { climbup = 1.0; }
						else if (GrappleModClient.get().isKeyDown(GrappleModKey.key_climbdown)) { climbup = -1.0; }
						if (climbup != 0) {
								if (dist + distToAnchor < maxLen || climbup > 0 || maxLen == 0) {
									hookEntity.ropeLength = dist + distToAnchor;
									hookEntity.ropeLength -= climbup* GrappleModConfig.getConf().grapplinghook.other.climb_speed;
									if (hookEntity.ropeLength < distToAnchor) {
										hookEntity.ropeLength = dist + distToAnchor;
									}

									Vec additionalmovementdown = spherevec.withMagnitude(-climbup * GrappleModConfig.getConf().grapplinghook.other.climb_speed).proj(new Vec(0,1,0));
									if (additionalmovementdown.y < 0) {
										additionalMotion.mutableAdd(additionalmovementdown);
									}
								}
						}
					}
				}
			}
			if (dist + distToAnchor < 2) {
				close = true;
			}

			// swing along max rope length
			if (anchor.sub(playerPos.add(motion)).length() > remainingLength) { // moving away
				motion = motion.removeAlong(spherevec);
			}
		}
		averagemotiontowards.mutableSetMagnitude(1);

		Vec facing = new Vec(entity.getLookAngle()).normalize();

		// Motor
		if (motor) {
			boolean dopull = true;

			// if only one rope is pulling and not oneropepull, disable motor
			if (this.custom.get(DOUBLE_HOOK_ATTACHED.get()) && this.grapplehookEntities.size() == 1) {
				boolean isdouble = true;
				for (GrapplinghookEntity hookEntity : this.grapplehookEntities) {
					if (!hookEntity.isInDoublePair) {
						isdouble = false;
						break;
					}
				}

				if (isdouble && !this.custom.get(SINGLE_ROPE_PULL.get())) {
					dopull = false;
				}
			}

			Vec totalPull = new Vec(0, 0, 0);

			double accel = this.custom.get(MOTOR_ACCELERATION.get()) / this.grapplehookEntities.size();

			double minabssidewayspull = 999;

			boolean firstpull = true;
			boolean pullispositive = true;
			boolean pullissameway = true;

			// set all motors to maximum pull and precalculate some stuff for smart motor / smart double motor
			for (GrapplinghookEntity hookEntity : this.grapplehookEntities) {
				Vec hookPos = Vec.positionVec(hookEntity);//this.getPositionVector();
				Vec anchor = hookEntity.getSegmentHandler().getClosest(hookPos);
				Vec spherevec = playerPos.sub(anchor);
				Vec pull = spherevec.scale(-1);

				hookEntity.pull = accel;

				totalPull.mutableAdd(pull.withMagnitude(accel));

				pull.mutableSetMagnitude(hookEntity.pull);

				// precalculate some stuff for smart double motor
				// For smart double motor: the motors should pull left and right equally
				// one side will be less able to pull to its side due to the angle
				// therefore the other side should slow down in order to match and have both sides pull left/right equally
				// the amount each should pull (the lesser of the two) is minabssidewayspull
				if (pull.dot(facing) > 0 || this.custom.get(MOTOR_WORKS_BACKWARDS.get())) {
					if (this.custom.get(SMART_MOTOR.get()) && this.grapplehookEntities.size() > 1) {
						Vec facingxy = new Vec(facing.x, 0, facing.z);
						Vec facingside = facingxy.cross(new Vec(0, 1, 0)).normalize();
						Vec sideways = pull.proj(facingside);
						Vec currentsideways = motion.proj(facingside);
						sideways.mutableAdd(currentsideways);
						double sidewayspull = sideways.dot(facingside);

						if (Math.abs(sidewayspull) < minabssidewayspull) {
							minabssidewayspull = Math.abs(sidewayspull);
						}

						if (firstpull) {
							firstpull = false;
							pullispositive = (sidewayspull >= 0);
						} else {
							if (pullispositive != (sidewayspull >= 0)) {
								pullissameway = false;
							}
						}
					}

				}
			}

			// Smart double motor - calculate the speed each motor should pull at
			if (this.custom.get(DOUBLE_SMART_MOTOR.get()) && this.grapplehookEntities.size() > 1) {
				totalPull = new Vec(0, 0, 0);

				for (GrapplinghookEntity hookEntity : this.grapplehookEntities) {
					Vec hookPos = Vec.positionVec(hookEntity);
					Vec anchor = hookEntity.getSegmentHandler().getClosest(hookPos);
					Vec spherevec = playerPos.sub(anchor);
					Vec pull = spherevec.scale(-1);
					pull.mutableSetMagnitude(hookEntity.pull);

					if (pull.dot(facing) > 0 || this.custom.get(MOTOR_WORKS_BACKWARDS.get())) {
						Vec facingxy = new Vec(facing.x, 0, facing.z);
						Vec facingside = facingxy.cross(new Vec(0, 1, 0)).normalize();
						Vec sideways = pull.proj(facingside);
						Vec currentsideways = motion.proj(facingside);
						sideways.mutableAdd(currentsideways);
						double sidewayspull = sideways.dot(facingside);

						if (pullissameway) {
							// only 1 rope pulls
							if (Math.abs(sidewayspull) > minabssidewayspull+0.05) {
								hookEntity.pull = 0;
							}
						} else {
							hookEntity.pull = hookEntity.pull * minabssidewayspull / Math.abs(sidewayspull);
						}
						totalPull.mutableAdd(pull.withMagnitude(hookEntity.pull));
					} else {
						if (hookEntity.isInDoublePair) {
							if (!this.custom.get(SINGLE_ROPE_PULL.get())) {
								dopull = false;
							}
						}
					}
				}
			}

			// smart motor - angle of motion = angle facing
			// match angle (the ratio of pulling upwards to pulling sideways)
			// between the motion (after pulling and gravity) vector and the facing vector
			// if double hooks, all hooks are scaled by the same amount (to prevent pulling to the left/right)
			double pullmult = 1;
			if (this.custom.get(SMART_MOTOR.get()) && totalPull.y > 0 && !(this.onGroundTimer > 0 || entity.onGround())) {
				Vec pullxzvector = new Vec(totalPull.x, 0, totalPull.z);
				double pullxz = pullxzvector.length();
				double motionxz = motion.proj(pullxzvector).dot(pullxzvector.normalize());
				double facingxz = facing.proj(pullxzvector).dot(pullxzvector.normalize());

				pullmult = (facingxz * (motion.y + gravity.y) - motionxz * facing.y)/(facing.y * pullxz - facingxz * totalPull.y); // (gravity.y * facingxz) / (facing.y * pullxz - facingxz * totalpull.y);

				if ((facing.y * pullxz - facingxz * totalPull.y) == 0) {
					// division by zero
					pullmult = 9999;
				}

				double pulll = pullmult * totalPull.length();

				if (pulll > this.custom.get(MOTOR_ACCELERATION.get())) {
					pulll = this.custom.get(MOTOR_ACCELERATION.get());
				}

				if (pulll < 0) {
					pulll = 0;
				}

				pullmult = pulll / totalPull.length();
			}

			// Prevent motor from moving too fast (motormaxspeed)
			if (this.motion.dot(totalPull) > 0) {
				if (this.motion.proj(totalPull).length() + totalPull.scale(pullmult).length() > this.custom.get(MAX_MOTOR_SPEED.get())) {
					pullmult = Math.max(0, (this.custom.get(MAX_MOTOR_SPEED.get()) - this.motion.proj(totalPull).length()) / totalPull.length());
				}
			}

			// sideways dampener
			if (this.custom.get(MOTOR_DAMPENER.get()) && totalPull.length() != 0) {
				motion = this.dampenMotion(motion, totalPull);
			}

			// actually pull with the motor
			if (dopull) {
				for (GrapplinghookEntity hookEntity : this.grapplehookEntities) {
					Vec hookPos = Vec.positionVec(hookEntity);
					Vec anchor = hookEntity.getSegmentHandler().getClosest(hookPos);
					Vec spherevec = playerPos.sub(anchor);
					Vec pull = spherevec.scale(-1);
					pull.mutableSetMagnitude(hookEntity.pull * pullmult);

					if (pull.dot(facing) > 0 || this.custom.get(MOTOR_WORKS_BACKWARDS.get())) {
						if (hookEntity.pull > 0) {
							motion.mutableAdd(pull);
						}
					}
				}
			}

			// if player is at the destination, slow down
			if (close && !(this.grapplehookEntities.size() > 1)) {
				if (entity.horizontalCollision || entity.verticalCollision || entity.onGround()) {
					motion.mutableScale(0.6);
				}
			}
		}

		// forcefield
		if (this.custom.get(FORCEFIELD_ATTACHED.get())) {
			Vec blockPush = checkRepel(playerPos, entity.level());
			blockPush.mutableScale(this.custom.get(FORCEFIELD_FORCE.get()) * 0.5f)
					 .mutableMultiply(0.5D, 2.0D, 0.5D);
			this.motion.mutableAdd(blockPush);
		}

		// rocket
		if (this.custom.get(ROCKET_ATTACHED.get())) {
			this.motion.mutableAdd(this.rocket(entity));
		}

		// WASD movement
		if (!doJump && !isClimbing) {
			applyPlayerMovement();
		}

		// jump
		if (doJump) {
			if (jumpSpeed <= 0) {
				jumpSpeed = 0;
			}

			if (jumpSpeed > GrappleModConfig.getConf().grapplinghook.other.rope_jump_power) {
				jumpSpeed = GrappleModConfig.getConf().grapplinghook.other.rope_jump_power;
			}

			this.doJump(entity, jumpSpeed, averagemotiontowards, min_spherevec_dist);
			GrappleModClient.get().resetRopeJumpTime(this.entity.level());
			return;
		}

		// now to actually apply everything to the player
		Vec newmotion = motion.add(additionalMotion);

		if (Double.isNaN(newmotion.x) || Double.isNaN(newmotion.y) || Double.isNaN(newmotion.z)) {
			newmotion = new Vec(0, 0, 0);
			motion = new Vec(0, 0, 0);
			GrappleMod.LOGGER.warn("error: motion is NaN");
		}

		entity.setDeltaMovement(newmotion.x, newmotion.y, newmotion.z);

		this.updateServerPos();
	}
	
	public void applyCalculatedTaut(double dist, GrapplinghookEntity hookEntity) {
		if (hookEntity == null) return;

		hookEntity.taut = dist < hookEntity.ropeLength
				? Math.max(0, 1 - ((hookEntity.ropeLength - dist) / 5))
				: 1.0d;
	}

	public void normalCollisions(boolean sliding) {

		// stop if collided with object
		if (this.entity.horizontalCollision) {
			if (this.entity.getDeltaMovement().x == 0) {
				if (!sliding || this.tryStepUp(new Vec(this.motion.x, 0, 0))) {
					this.motion.x = 0;
				}
			}

			if (this.entity.getDeltaMovement().z == 0) {
				if (!sliding || this.tryStepUp(new Vec(0, 0, this.motion.z))) {
					this.motion.z = 0;
				}
			}
		}
		
		if (sliding && !this.entity.horizontalCollision) {
			if (entity.position().x - entity.xOld == 0) {
				this.motion.x = 0;
			}
			if (entity.position().z - entity.zOld == 0) {
				this.motion.z = 0;
			}
		}
		
		if (this.entity.verticalCollision) {
			if (this.entity.onGround()) {
				if (!sliding && Minecraft.getInstance().options.keyJump.isDown()) {
					this.motion.y = entity.getDeltaMovement().y;
				} else {
					if (this.motion.y < 0) {
						this.motion.y = 0;
					}
				}

			} else {
				if (this.motion.y > 0 && entity.yOld == entity.position().y) {
					this.motion.y = 0;
				}
			}
		}
	}

	public boolean tryStepUp(Vec collisionMotion) {
		if (collisionMotion.length() == 0)
			return false;

		Vec moveOffset = collisionMotion.withMagnitude(0.05).add(0, entity.maxUpStep() + 0.01, 0);
		Iterable<VoxelShape> collisions = this.entity.level().getCollisions(this.entity, this.entity.getBoundingBox().move(moveOffset.x, moveOffset.y, moveOffset.z));

		if (collisions.iterator().hasNext()) return true;

		if (this.entity.onGround()) {
			this.entity.horizontalCollision = false;
			return false;
		}

		Vec pos = Vec.positionVec(entity);
		pos.mutableAdd(moveOffset);
		pos.applyAsPositionTo(entity);
		this.entity.xOld = pos.x;
		this.entity.yOld = pos.y;
		this.entity.zOld = pos.z;

		return false;
	}

	public void normalGround(boolean sliding) {
		if (entity.onGround()) {
			onGroundTimer = maxOnGroundTimer;
		} else {
			if (this.onGroundTimer > 0) {
				onGroundTimer--;
			}
		}
		if (entity.onGround() || onGroundTimer > 0) {
			if (!sliding) {
				this.motion = Vec.motionVec(entity);
				if (GrappleModClient.get().isKeyDown(MinecraftKey.keyBindJump)) {
					this.motion.y += 0.05;
				}
			}
		}
		prevOnGround = entity.onGround();
	}

	private double getJumpPower(Entity player, double jumppower) {
		double maxjump = GrappleModConfig.getConf().grapplinghook.other.rope_jump_power;
		if (onGroundTimer > 0) { // on ground: jump normally
			onGroundTimer = 20;
			return 0;
		}
		if (player.onGround()) {
			jumppower = 0;
		}
		if (player.horizontalCollision || player.verticalCollision) {
			jumppower = maxjump;
		}
		if (jumppower < 0) {
			jumppower = 0;
		}
		
		return jumppower;
	}
	
	public void doJump(Entity player, double jumppower, Vec averagemotiontowards, double min_spherevec_dist) {
		if (jumppower > 0) {
			if (GrappleModConfig.getConf().grapplinghook.other.rope_jump_at_angle && min_spherevec_dist > 1) {
				motion.mutableAdd(averagemotiontowards.withMagnitude(jumppower));
			} else {
				if (jumppower > player.getDeltaMovement().y + jumppower) {
					motion.y = jumppower;
				} else {
					motion.y += jumppower;
				}
			}
			motion.applyAsMotionTo(player);
		}
		
		this.unattach();
		
		this.updateServerPos();
	}
	
	public double getJumpPower(Entity player, Vec spherevec, GrapplinghookEntity hookEntity) {
		double maxjump = GrappleModConfig.getConf().grapplinghook.other.rope_jump_power;
		Vec jump = new Vec(0, maxjump, 0);
		if (spherevec != null && !GrappleModConfig.getConf().grapplinghook.other.rope_jump_at_angle) {
			jump = jump.proj(spherevec);
		}
		double jumppower = jump.y;
		
		if (spherevec != null && spherevec.y > 0) {
			jumppower = 0;
		}
		if ((hookEntity != null) && hookEntity.ropeLength < 1 && (player.position().y < hookEntity.position().y)) {
			jumppower = maxjump;
		}

		jumppower = this.getJumpPower(player, jumppower);
		
		double current_speed = GrappleModConfig.getConf().grapplinghook.other.rope_jump_at_angle ? -motion.distanceAlong(spherevec) : motion.y;
		if (current_speed > 0) {
			jumppower = jumppower - current_speed;
		}

		if (jumppower < 0) {jumppower = 0;}

		return jumppower;
	}

	public Vec dampenMotion(Vec motion, Vec forward) {
		Vec newmotion = motion.proj(forward);
		double dampening = 0.05;
		return newmotion.scale(dampening).add(motion.scale(1-dampening));
	}
	
	public void updateServerPos() {
		NetworkManager.packetToServer(new PlayerMovementMessage(this.entityId, this.entity.position().x, this.entity.position().y, this.entity.position().z, this.entity.getDeltaMovement().x, this.entity.getDeltaMovement().y, this.entity.getDeltaMovement().z));
	}
	
	// Vector stuff:
	
	public void receiveGrappleDetach() {
		this.unattach();
	}

	public void receiveEnderLaunch(double x, double y, double z) {
		this.motion.mutableAdd(x, y, z);
		this.motion.applyAsMotionTo(this.entity);
	}
	
	public void applyAirFriction() {
		double dragforce = 1 / 200F;
		if (this.entity.isInWater() || this.entity.isInLava()) {
			dragforce = 1 / 4F;
		}
		
		double vel = this.motion.length();
		dragforce = vel * dragforce;
		
		Vec airfric = new Vec(this.motion.x, this.motion.y, this.motion.z);
		airfric.mutableSetMagnitude(-dragforce);
		this.motion.mutableAdd(airfric);
	}
	
	public void applyPlayerMovement() {
		motion.mutableAdd(this.playerMovement.withMagnitude(0.015 + motion.length() * 0.01).scale(this.playerMovementMult));//0.02 * playermovementmult));
	}

	public void addHookEntity(GrapplinghookEntity hookEntity) {
		this.grapplehookEntities.add(hookEntity);
		hookEntity.ropeLength = hookEntity.getSegmentHandler().getDist(Vec.positionVec(hookEntity), Vec.positionVec(entity).add(new Vec(0, entity.getEyeHeight(), 0)));
		this.grapplehookEntityIds.add(hookEntity.getId());
	}

	
    // repel stuff
    public Vec checkRepel(Vec p, Level w) {
    	p = p.add(0.0, 0.75, 0.0);
    	Vec v = new Vec(0, 0, 0);
    	
    	double t = (1.0 + Math.sqrt(5.0)) / 2.0;
    	
		BlockPos pos = BlockPos.containing(p.x, p.y, p.z);

		if (hasBlock(pos, w)) {
			v.mutableAdd(0, 1, 0);

		} else {
	    	v.mutableAdd(vecDist(p, new Vec(-1,  t,  0), w));
	    	v.mutableAdd(vecDist(p, new Vec( 1,  t,  0), w));
	    	v.mutableAdd(vecDist(p, new Vec(-1, -t,  0), w));
	    	v.mutableAdd(vecDist(p, new Vec( 1, -t,  0), w));
	    	v.mutableAdd(vecDist(p, new Vec( 0, -1,  t), w));
	    	v.mutableAdd(vecDist(p, new Vec( 0,  1,  t), w));
	    	v.mutableAdd(vecDist(p, new Vec( 0,  1,  t), w));
	    	v.mutableAdd(vecDist(p, new Vec( 0, -1, -t), w));
	    	v.mutableAdd(vecDist(p, new Vec( 0,  1, -t), w));
	    	v.mutableAdd(vecDist(p, new Vec( t,  0, -1), w));
	    	v.mutableAdd(vecDist(p, new Vec( t,  0,  1), w));
	    	v.mutableAdd(vecDist(p, new Vec(-t,  0, -1), w));
	    	v.mutableAdd(vecDist(p, new Vec(-t,  0,  1), w));
		}
    	
    	if (v.length() > repelMaxPush) {
    		v.mutableSetMagnitude(repelMaxPush);
    	}
    	
		return v;
	}
    
    public Vec vecDist(Vec p, Vec v, Level w) {
    	for (double i = 0.5; i < 10; i += 0.5) {
    		Vec v2 = v.withMagnitude(i);
    		BlockPos pos = BlockPos.containing(p.x + v2.x, p.y + v2.y, p.z + v2.z);

    		if (this.hasBlock(pos, w)) {
    			Vec v3 = new Vec(pos.getX() + 0.5 - p.x, pos.getY() + 0.5 - p.y, pos.getZ() + 0.5 - p.z);
    			v3.mutableSetMagnitude(-1 / Math.pow(v3.length(), 2));
    			return v3;
    		}
    	}
    	
    	return new Vec(0, 0, 0);
    }
    
	public boolean hasBlock(BlockPos pos, Level w) {
    	BlockState blockstate = w.getBlockState(pos);
    	return !blockstate.isAir();
	}

	public void receiveGrappleDetachHook(int hookid) {
		if (this.grapplehookEntityIds.contains(hookid)) {
			this.grapplehookEntityIds.remove(hookid);

		} else {
			GrappleMod.LOGGER.warn("Error: controller received hook detach, but hook id not in grapplehookEntityIds");
		}
		
		GrapplinghookEntity hookToRemove = null;
		for (GrapplinghookEntity hookEntity : this.grapplehookEntities) {
			if (hookEntity.getId() == hookid) {
				hookToRemove = hookEntity;
				break;
			}
		}
		
		if (hookToRemove != null) {
			this.grapplehookEntities.remove(hookToRemove);
		} else {
			GrappleMod.LOGGER.warn("Error: controller received hook detach, but hook entity not in grapplehookEntities");
		}
	}

	public Vec rocket(Entity entity) {
		if (!GrappleModClient.get().isKeyDown(GrappleModKey.key_rocket)) {
			this.rocketKeyDown = false;
			this.rocketProgression = 0F;
			return new Vec(0,0,0);
		}

		this.rocketProgression = GrappleModClient.get().getRocketFunctioning();
		double rocket_force = this.custom.get(ROCKET_FORCE.get()) * 0.225 * this.rocketProgression;
		double yaw = entity.getYRot();
		double pitch = this.custom.get(ROCKET_ANGLE.get()) - entity.getXRot();

		Vec force = new Vec(0, 0, rocket_force);
		force = force.rotatePitch(Math.toRadians(pitch));
		force = force.rotateYaw(Math.toRadians(yaw));

		this.rocketKeyDown = true;
		return force;
	}
	
	public Vec getNearbyWall(Vec tryfirst, Vec trysecond, double extra) {
		float entitywidth = this.entity.getBbWidth();
		
		for (Vec direction : new Vec[] {tryfirst, trysecond, tryfirst.scale(-1), trysecond.scale(-1)}) {
			BlockHitResult raytraceresult = GrappleModUtils.rayTraceBlocks(this.entity, this.entity.level(), Vec.positionVec(this.entity), Vec.positionVec(this.entity).add(direction.withMagnitude(entitywidth/2 + extra)));

			if (raytraceresult != null) {
				wallrunRaytraceResult = raytraceresult;
				return direction;
			}
		}
		
		return null;
	}
	
	public Vec getWallDirection() {
		Vec tryfirst = new Vec(0, 0, 0);
		Vec trysecond = new Vec(0, 0, 0);
		
		if (Math.abs(this.motion.x) > Math.abs(this.motion.z)) {
			tryfirst.x = (this.motion.x > 0) ? 1 : -1;
			trysecond.z = (this.motion.z > 0) ? 1 : -1;
		} else {
			tryfirst.z = (this.motion.z > 0) ? 1 : -1;
			trysecond.x = (this.motion.x > 0) ? 1 : -1;
		}
		
		return getNearbyWall(tryfirst, trysecond, 0.05);
	}
	
	public Vec getCorner(int cornernum, Vec facing, Vec sideways) {
		Vec corner = new Vec(0,0,0);
		if (cornernum / 2 == 0) {
			corner.mutableAdd(facing);
		} else {
			corner.mutableAdd(facing.scale(-1));
		}

		if (cornernum % 2 == 0) {
			corner.mutableAdd(sideways);
		} else {
			corner.mutableAdd(sideways.scale(-1));
		}
		return corner;
	}
	
	public boolean wallNearby(double dist) {
		float entitywidth = this.entity.getBbWidth();
		Vec v1 = new Vec(entitywidth/2 + dist, 0, 0);
		Vec v2 = new Vec(0, 0, entitywidth/2 + dist);
		
		for (int i = 0; i < 4; i++) {
			Vec corner1 = getCorner(i, v1, v2);
			Vec corner2 = getCorner((i + 1) % 4, v1, v2);
			
			BlockHitResult raytraceresult = GrappleModUtils.rayTraceBlocks(this.entity, this.entity.level(), Vec.positionVec(this.entity).add(corner1), Vec.positionVec(this.entity).add(corner2));
			if (raytraceresult != null) {
				return true;
			}
		}
		
		return false;
	}

	public boolean isWallRunning() {
		double currentSpeed = Math.sqrt(Math.pow(this.motion.x, 2) + Math.pow(this.motion.z,  2));
		if (currentSpeed < GrappleModConfig.getConf().enchantments.wallrun.wallrun_min_speed) {
			this.isOnWall = false;
			return false;
		}
		
		if (this.isOnWall) {
			GrappleModClient.get().setWallrunTicks(GrappleModClient.get().getWallrunTicks()+1);
		}
		
		if (GrappleModClient.get().getWallrunTicks() < GrappleModConfig.getConf().enchantments.wallrun.max_wallrun_time * 40) {
			if (!(this.playerSneak)) {
				// continue wallrun
				if (this.isOnWall && !this.entity.onGround() && this.entity.horizontalCollision) {
					return !(entity instanceof LivingEntity living && living.onClimbable());
				}
				
				// start wallrun
				if (GrappleModClient.get().isWallRunning(this.entity, this.motion)) {
					this.isOnWall = true;
					return true;
				}
			}

			this.isOnWall = false;
		}
		
		if (GrappleModClient.get().getWallrunTicks() > 0 && (this.entity.onGround() || (!this.entity.horizontalCollision && !wallNearby(0.2)))) {
			this.ticksSinceLastWallrunSoundEffect = 0;
		}
		
		return false;
	}
	
	public boolean applyWallRun() {
		boolean wallrun = this.isWallRunning();
		
		if (this.playerJump) {
			if (wallrun) {
				return false;
			}

			this.playerJump = false;
		}
		
		if (wallrun && !GrappleModClient.get().isKeyDown(GrappleModKey.key_jumpanddetach)) {

			Vec wallSide = this.getWallDirection();
			if (wallSide != null) {
				this.wallDirection = wallSide;
			}
			
			if (this.wallDirection == null) {
				return false;
			}

			if (!this.playerJump) {
				this.motion.y = 0;
			}

			// drag
			double dragforce = GrappleModConfig.getConf().enchantments.wallrun.wallrun_drag;
			double vel = this.motion.length();
			
			if (dragforce > vel)
				dragforce = vel;
			
			Vec wallFriction = new Vec(this.motion);
			if (wallSide != null) {
				wallFriction.removeAlong(wallSide);
			}

			wallFriction.mutableSetMagnitude(-dragforce);
			this.motion.mutableAdd(wallFriction);

			this.ticksSinceLastWallrunSoundEffect++;
			if (this.ticksSinceLastWallrunSoundEffect > GrappleModConfig.getClientConf().sounds.wallrun_sound_effect_time_s * 20 * GrappleModConfig.getConf().enchantments.wallrun.wallrun_max_speed / (vel + 0.00000001)) {
				if (wallrunRaytraceResult != null) {
					BlockPos blockpos = wallrunRaytraceResult.getBlockPos();
					
					BlockState blockState = this.entity.level().getBlockState(blockpos);
					Block blockIn = blockState.getBlock();
					
			        SoundType soundtype = blockIn.getSoundType(blockState);

		            this.entity.playSound(soundtype.getStepSound(), soundtype.getVolume() * 0.30F * GrappleModConfig.getClientConf().sounds.wallrun_sound_volume, soundtype.getPitch());
					this.ticksSinceLastWallrunSoundEffect = 0;
				}
			}
		}
		
		// jump
		boolean isjumping = GrappleModClient.get().isKeyDown(GrappleModKey.key_jumpanddetach) && this.isOnWall;
		isjumping = isjumping && !this.playerJump; // only jump once when key is first pressed
		this.playerJump = GrappleModClient.get().isKeyDown(GrappleModKey.key_jumpanddetach) && isOnWall;

		if (isjumping && wallrun) {
			GrappleModClient.get().setWallrunTicks(0);
			Vec jump = new Vec(0, GrappleModConfig.getConf().enchantments.wallrun.wall_jump_up, 0);

			if (this.wallDirection != null) {
				jump.mutableAdd(this.wallDirection.scale(-GrappleModConfig.getConf().enchantments.wallrun.wall_jump_side));
			}

			this.motion.mutableAdd(jump);
			
			wallrun = false;

			GrappleModClient.get().playWallrunJumpSound();
		}
		
		return wallrun;
	}
	
	public Vec wallrunPressAgainstWall() {
		// press against wall
		if (this.wallDirection != null) {
			return this.wallDirection.withMagnitude(0.05);
		}
		return new Vec(0,0,0);
	}

	public void doDoubleJump() {
		if (-this.motion.y > GrappleModConfig.getConf().enchantments.doublejump.dont_doublejump_if_falling_faster_than) {
			return;
		}

		if (this.motion.y < 0 && !GrappleModConfig.getConf().enchantments.doublejump.doublejump_relative_to_falling) {
			this.motion.y = 0;
		}

		this.motion.y += GrappleModConfig.getConf().enchantments.doublejump.doublejumpforce;
		this.motion.applyAsMotionTo(this.entity);
	}
	
	public void applySlidingFriction() {
		double dragForce = GrappleModConfig.getConf().enchantments.slide.sliding_friction;
		
		if (dragForce > this.motion.length()) {dragForce = this.motion.length(); }
		
		Vec airFriction = new Vec(this.motion.x, this.motion.y, this.motion.z);
		airFriction.mutableSetMagnitude(-dragForce);
		this.motion.mutableAdd(airFriction);
	}

	public void doSlidingJump() {
		this.motion.y = GrappleModConfig.getConf().enchantments.slide.slidingjumpforce;
	}

	public void resetRocketProgression() {
		this.rocketKeyDown = true;
		this.rocketProgression = 1.0F;
	}

	public double getPlayerMovementMultiplier() {
		return this.playerMovementMult;
	}

	public double getRocketProgression() {
		return this.rocketProgression;
	}

	public CustomizationVolume getCurrentCustomizations() {
		return this.custom;
	}
}
