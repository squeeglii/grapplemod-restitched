package com.yyon.grapplinghook.client;

import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.yyon.grapplinghook.client.keybind.KeyBinding;
import com.yyon.grapplinghook.config.GrappleConfig;
import com.yyon.grapplinghook.controller.AirfrictionController;
import com.yyon.grapplinghook.controller.ForcefieldController;
import com.yyon.grapplinghook.controller.GrappleController;
import com.yyon.grapplinghook.enchantment.DoubleJumpEnchantment;
import com.yyon.grapplinghook.enchantment.SlidingEnchantment;
import com.yyon.grapplinghook.enchantment.WallrunEnchantment;
import com.yyon.grapplinghook.entity.grapplehook.GrapplehookEntity;
import com.yyon.grapplinghook.item.EnderStaffItem;
import com.yyon.grapplinghook.item.GrapplehookItem;
import com.yyon.grapplinghook.util.GrappleCustomization;
import com.yyon.grapplinghook.util.GrappleModUtils;
import com.yyon.grapplinghook.util.Vec;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

public class ClientControllerManager {
	public static ClientControllerManager instance;

	public static HashMap<Integer, GrappleController> controllers = new HashMap<>();
	public static HashMap<BlockPos, GrappleController> controllerPos = new HashMap<>();
	public static long prevRopeJumpTime = 0;

	public HashMap<Integer, Long> enderLaunchTimer = new HashMap<>();

	public double rocketFuel = 1.0;
	public double rocketIncreaseTick = 0.0;
	public double rocketDecreaseTick = 0.0;

	public int ticksWallRunning = 0;

	private boolean prevJumpButton = false;
	private int ticksSinceLastOnGround = 0;
	private boolean alreadyUsedDoubleJump = false;


	public ClientControllerManager() {
		instance = this;
	}


	public void onClientTick(Player player) {
		if (player.isOnGround() || (controllers.containsKey(player.getId()) && controllers.get(player.getId()).controllerId == GrappleModUtils.GRAPPLE_ID)) {
			ticksWallRunning = 0;
		}

		if (this.isWallRunning(player, Vec.motionVec(player))) {
			if (!controllers.containsKey(player.getId())) {
				GrappleController controller = this.createControl(GrappleModUtils.AIR_FRICTION_ID, -1, player.getId(), player.level, null, null);
				if (controller.getWallDirection() == null)
					controller.unattach();
			}
			
			if (controllers.containsKey(player.getId())) {
				ticksSinceLastOnGround = 0;
				alreadyUsedDoubleJump = false;
			}
		}
		
		this.checkDoubleJump();
		
		this.checkSlide(player);
		
		this.rocketFuel += this.rocketIncreaseTick;
		
		try {
			for (GrappleController controller : controllers.values())
				controller.doClientTick();

		} catch (ConcurrentModificationException e) {
			System.out.println("ConcurrentModificationException caught");
		}

		if (this.rocketFuel > 1) {this.rocketFuel = 1;}
		
		if (player.isOnGround()) {
			if (enderLaunchTimer.containsKey(player.getId())) {
				long timer = GrappleModUtils.getTime(player.level) - enderLaunchTimer.get(player.getId());
				if (timer > 10)
					this.resetLauncherTime(player.getId());
			}
		}
	}

	public void checkSlide(Player player) {
		if (KeyBinding.key_slide.isDown() && !controllers.containsKey(player.getId()) && this.isSliding(player, Vec.motionVec(player))) {
			this.createControl(GrappleModUtils.AIR_FRICTION_ID, -1, player.getId(), player.level, null, null);
		}
	}

	public void launchPlayer(Player player) {
		long previousTime = enderLaunchTimer.containsKey(player.getId())
				? enderLaunchTimer.get(player.getId())
				: 0 ;

		long timer = GrappleModUtils.getTime(player.level) - previousTime;

		if (timer > GrappleConfig.getConf().enderstaff.ender_staff_recharge) {
			ItemStack mainHandStack = player.getItemInHand(InteractionHand.MAIN_HAND);
			ItemStack offHandStack = player.getItemInHand(InteractionHand.OFF_HAND);
			Item mainHandItem = mainHandStack.getItem();
			Item offHandItem = offHandStack.getItem();

			boolean isMainHolding = mainHandItem instanceof EnderStaffItem || mainHandItem instanceof GrapplehookItem;
			boolean isOffHolding = offHandItem instanceof EnderStaffItem || offHandItem instanceof GrapplehookItem;

			if(! (isMainHolding || isOffHolding)) return;

			ItemStack usedStack = isMainHolding ? mainHandStack : offHandStack;
			Item usedItem = isMainHolding ? mainHandItem : offHandItem;

			enderLaunchTimer.put(player.getId(), GrappleModUtils.getTime(player.level));

			Vec facing = Vec.lookVec(player);

			GrappleCustomization custom = null;
			if (usedItem instanceof GrapplehookItem grapple)
				custom = grapple.getCustomization(usedStack);

			if (!controllers.containsKey(player.getId())) {
				player.setOnGround(false);
				this.createControl(GrappleModUtils.AIR_FRICTION_ID, -1, player.getId(), player.level, null, custom);
			}

			facing.mult_ip(GrappleConfig.getConf().enderstaff.ender_staff_strength);
			ClientControllerManager.receiveEnderLaunch(player.getId(), facing.x, facing.y, facing.z);
			GrappleModClient.get().playSound(new ResourceLocation("grapplemod", "enderstaff"), GrappleConfig.getClientConf().sounds.enderstaff_sound_volume * 0.5F);
		}
	}
	
	public void resetLauncherTime(int playerId) {
		if (enderLaunchTimer.containsKey(playerId))
			enderLaunchTimer.put(playerId, (long) 0);
	}

	public void updateRocketRegen(double rocketActiveTime, double rocketRefuelRatio) {
		this.rocketDecreaseTick = 0.05 / 2.0 / rocketActiveTime;
		this.rocketIncreaseTick = 0.05 / 2.0 / rocketActiveTime / rocketRefuelRatio;
	}
	

	public double getRocketFunctioning() {
		this.rocketFuel -= this.rocketIncreaseTick;
		this.rocketFuel -= this.rocketDecreaseTick;
		
		if (this.rocketFuel >= 0) {
			return 1;
		} else {
			this.rocketFuel = 0;
			return this.rocketIncreaseTick / this.rocketDecreaseTick / 2.0;
		}
	}
	
	public boolean isWallRunning(Entity entity, Vec motion) {
		if (!(entity.horizontalCollision && !entity.isOnGround() && !entity.isCrouching())) return false;
		if (entity instanceof LivingEntity && ((LivingEntity) entity).onClimbable()) return false;

		for (ItemStack stack : entity.getArmorSlots()) {
			if (stack != null) {
				Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
				for (Enchantment enchant : enchantments.keySet()) {
					if (!(enchant instanceof WallrunEnchantment)) continue;
					if (enchantments.get(enchant) < 1) continue;
					if (KeyBinding.key_jumpanddetach.isDown() || Minecraft.getInstance().options.keyJump.isDown())  continue;

					BlockHitResult rayTraceResult = GrappleModUtils.rayTraceBlocks(entity.level, Vec.positionVec(entity), Vec.positionVec(entity).add(new Vec(0, -1, 0)));
					if (rayTraceResult == null) {
						double currentSpeed = Math.sqrt(Math.pow(motion.x, 2) + Math.pow(motion.z,  2));
						if (currentSpeed >= GrappleConfig.getConf().enchantments.wallrun.wallrun_min_speed) {
							return true;
						}
					}

					break;
				}
			}
		}

		return false;
	}
	
	public void checkDoubleJump() {
		Player player = Minecraft.getInstance().player;
		if(player == null) return;
		
		if (player.isOnGround()) {
			this.ticksSinceLastOnGround = 0;
			this.alreadyUsedDoubleJump = false;
		} else {
			this.ticksSinceLastOnGround++;
		}
		
		boolean isJumpButtonDown = Minecraft.getInstance().options.keyJump.isDown();

		Boolean[] conditions = new Boolean[] {
				isJumpButtonDown,
				!prevJumpButton,
				!player.isInWater(),
				!player.isInLava(),
				ticksSinceLastOnGround > 3,
				this.wearingDoubleJumpEnchant(player),
				!alreadyUsedDoubleJump
		};

		boolean allConditionsMet = GrappleModUtils.and(conditions);

		if(allConditionsMet && !controllers.containsKey(player.getId())) {
			this.createControl(GrappleModUtils.AIR_FRICTION_ID, -1, player.getId(), player.level, null, null);
			GrappleModClient.get().playDoubleJumpSound();
		}

		if(allConditionsMet && controllers.get(player.getId()) instanceof AirfrictionController ctrl) {
			this.alreadyUsedDoubleJump = true;
			ctrl.doubleJump();
			GrappleModClient.get().playDoubleJumpSound();
		}
		
		this.prevJumpButton = isJumpButtonDown;
	}

	public boolean wearingDoubleJumpEnchant(Entity entity) {
		if (entity instanceof Player player && player.getAbilities().flying)
			return false;

		for (ItemStack stack : entity.getArmorSlots()) {
			if (stack == null) continue;

			Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);

			for (Enchantment enchant : enchantments.keySet()) {
				if (!(enchant instanceof DoubleJumpEnchantment)) continue;
				if (enchantments.get(enchant) >= 1) continue;
				return true;
			}
		}

		return false;
	}
	
	public static boolean isWearingSlidingEnchant(Entity entity) {
		for (ItemStack stack : entity.getArmorSlots()) {
			if (stack != null) {
				Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);

				for (Enchantment enchant : enchantments.keySet()) {
					if (!(enchant instanceof SlidingEnchantment)) continue;
					if (enchantments.get(enchant) >= 1) continue;
					return true;
				}
			}
		}

		return false;
	}

	public boolean isSliding(Entity entity, Vec motion) {
		if (entity.isInWater() || entity.isInLava()) return false;
		
		if (entity.isOnGround() && KeyBinding.key_slide.isDown()) {
			if (!ClientControllerManager.isWearingSlidingEnchant(entity)) return false;
			boolean wasSliding = false;
			int id = entity.getId();

			GrappleController controller = controllers.get(id);
			if (controller instanceof AirfrictionController afc && afc.wasSliding) {
					wasSliding = true;
			}

			double speed = motion.removeAlong(new Vec (0,1,0)).length();
			return speed > GrappleConfig.getConf().enchantments.slide.sliding_end_min_speed && (wasSliding || speed > GrappleConfig.getConf().enchantments.slide.sliding_min_speed);

		}
		
		return false;
	}


	public GrappleController createControl(int controllerId, int grapplehookEntityId, int playerId, Level world, BlockPos blockPos, GrappleCustomization custom) {
		GrapplehookEntity grapplehookEntity = null;
		if (world.getEntity(grapplehookEntityId) instanceof GrapplehookEntity g)
			grapplehookEntity = g;
		
		boolean multi = custom != null && custom.doublehook;
		
		GrappleController currentController = controllers.get(playerId);
		if (currentController != null && !(multi && currentController.custom != null && currentController.custom.doublehook))
			currentController.unattach();
		
		GrappleController control;
		if (controllerId == GrappleModUtils.GRAPPLE_ID) {
			if (!multi) {
				control = new GrappleController(grapplehookEntityId, playerId, world, controllerId, custom);

			} else {
				control = controllers.get(playerId);

				Boolean[] conditions = new Boolean[] {
						control != null,
						control.getClass().equals(GrappleController.class),
						control.custom.doublehook,
						grapplehookEntity != null
				};

				if(GrappleModUtils.and(conditions)) {
					control.addHookEntity(grapplehookEntity);
					return control;
				}

				control = new GrappleController(grapplehookEntityId, playerId, world, controllerId, custom);
			}

		} else if (controllerId == GrappleModUtils.REPEL_ID) {
			control = new ForcefieldController(grapplehookEntityId, playerId, world, controllerId);

		} else if (controllerId == GrappleModUtils.AIR_FRICTION_ID) {
			control = new AirfrictionController(grapplehookEntityId, playerId, world, controllerId, custom);

		} else return null;

		if (blockPos != null)
			ClientControllerManager.controllerPos.put(blockPos, control);

		ClientControllerManager.registerController(playerId, control);
		
		Entity e = world.getEntity(playerId);
		if (e instanceof LocalPlayer p)
			control.receivePlayerMovementMessage(p.input.leftImpulse, p.input.forwardImpulse, p.input.jumping, p.input.shiftKeyDown);

		
		return control;
	}

	public static void registerController(int entityId, GrappleController controller) {
		if (controllers.containsKey(entityId))
			controllers.get(entityId).unattach();
		
		controllers.put(entityId, controller);
	}

	public static GrappleController unregisterController(int entityId) {
		if (!controllers.containsKey(entityId)) return null;
		GrappleController controller = controllers.get(entityId);
		controllers.remove(entityId);

		BlockPos pos = null;
		for (BlockPos blockpos : controllerPos.keySet()) {
			GrappleController otherController = controllerPos.get(blockpos);
			if (otherController == controller)
				pos = blockpos;
		}

		if (pos != null)
			controllerPos.remove(pos);

		return controller;
	}

	public static void receiveGrappleDetach(int id) {
		GrappleController controller = controllers.get(id);
		if (controller != null)
			controller.receiveGrappleDetach();
	}
	
	public static void receiveGrappleDetachHook(int id, int hookId) {
		GrappleController controller = controllers.get(id);
		if (controller != null)
			controller.receiveGrappleDetachHook(hookId);
	}

	public static void receiveEnderLaunch(int id, double x, double y, double z) {
		GrappleController controller = controllers.get(id);

		if (controller == null) {
			Logger.getGlobal().warning("Couldn't find controller");
			return;
		}

		controller.receiveEnderLaunch(x, y, z);
	}


	public static class RocketSound extends AbstractTickableSoundInstance {
		GrappleController controller;
		boolean stopping = false;
		public float changeSpeed;

		protected RocketSound(GrappleController controller, SoundEvent soundEvent, SoundSource soundSource) {
			super(soundEvent, soundSource, RandomSource.create());
			this.looping = true;
			this.controller = controller;
			controller.rocket_key = true;
			controller.rocket_on = 1.0F;

			this.changeSpeed = GrappleConfig.getClientConf().sounds.rocket_sound_volume * 0.5F * 0.2F;
			this.volume = this.changeSpeed;
			this.delay = 0;
			this.attenuation = SoundInstance.Attenuation.NONE;
			this.relative = false;
		}

		@Override
		public void tick() {
			if (!controller.rocket_key || !controller.attached)
				this.stopping = true;

			float targetvolume = (float) controller.rocket_on * GrappleConfig.getClientConf().sounds.rocket_sound_volume * 0.5F;
			if (this.stopping) targetvolume = 0;

			float diff = Math.abs(targetvolume - this.volume);
			this.volume = diff > changeSpeed
					? this.volume + changeSpeed * (this.volume > targetvolume ? -1 : 1)
					: targetvolume;

			if (this.volume == 0 && this.stopping)
				this.stop();

			this.x = controller.entity.getX();
			this.y = controller.entity.getY();
			this.z = controller.entity.getZ();
		}
	}

	public void startRocket(Player player, GrappleCustomization custom) {
		if (!custom.rocket) return;
		
		GrappleController controller;
		if (controllers.containsKey(player.getId())) {
			controller = controllers.get(player.getId());
			if (controller.custom == null || !controller.custom.rocket) {
				if (controller.custom == null)
					controller.custom = custom;
				controller.custom.rocket = true;
				controller.custom.rocket_active_time = custom.rocket_active_time;
				controller.custom.rocket_force = custom.rocket_force;
				controller.custom.rocket_refuel_ratio = custom.rocket_refuel_ratio;
				this.updateRocketRegen(custom.rocket_active_time, custom.rocket_refuel_ratio);
			}

		} else {
			controller = this.createControl(GrappleModUtils.AIR_FRICTION_ID, -1, player.getId(), player.level, null, custom);
		}
		
		RocketSound sound = new RocketSound(controller, SoundEvent.createVariableRangeEvent(new ResourceLocation("grapplemod", "rocket")), SoundSource.PLAYERS);
		Minecraft.getInstance().getSoundManager().play(sound);
	}
}
