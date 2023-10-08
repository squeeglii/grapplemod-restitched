package com.yyon.grapplinghook.content.entity.grapplinghook;

import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.client.GrappleModClient;
import com.yyon.grapplinghook.config.GrappleModLegacyConfig;
import com.yyon.grapplinghook.config.ConfigUtility;
import com.yyon.grapplinghook.network.NetworkManager;
import com.yyon.grapplinghook.network.clientbound.GrappleAttachMessage;
import com.yyon.grapplinghook.network.clientbound.GrappleAttachPosMessage;
import com.yyon.grapplinghook.content.registry.GrappleModEntities;
import com.yyon.grapplinghook.content.registry.GrappleModItems;
import com.yyon.grapplinghook.customization.CustomizationVolume;
import com.yyon.grapplinghook.physics.io.HookSnapshot;
import com.yyon.grapplinghook.physics.io.RopeSnapshot;
import com.yyon.grapplinghook.util.GrappleModUtils;
import com.yyon.grapplinghook.util.Vec;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;

import java.util.HashMap;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import static com.yyon.grapplinghook.content.registry.GrappleModCustomizationProperties.*;

/*
 * This file is part of GrappleMod.

    GrappleMod is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    GrappleMod is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with GrappleMod.  If not, see <http://www.gnu.org/licenses/>.
 */

public class GrapplinghookEntity extends ThrowableItemProjectile implements IExtendedSpawnPacketEntity {

	public Entity shootingEntity = null;
	public int shootingEntityID;

	public Vec thisPos;

	private boolean isAttachedToMainHand = true;
	private boolean isFirstAttach = false;
	private boolean isAttachedToSurface;
	public Vec attachDirection = null;

	private BlockPos lastBlockCollision = null;
	private Direction lastBlockCollisionSide = null;
	private Vec lastSubCollisionPos = null;


	public double pull;

	public double taut = 1;

	public boolean isInDoublePair = false;

	public double ropeLength;

	private final RopeSegmentHandler segmentHandler;

	private CustomizationVolume customization;

	// magnet attract
	public Vec prevPos = null;
	public boolean foundBlock = false;
	public boolean wasInAir = false;
	public BlockPos magnetBlock = null;


	public GrapplinghookEntity(EntityType<? extends GrapplinghookEntity> type, Level world) {
		super(type, world);

		this.segmentHandler = new RopeSegmentHandler(this, Vec.positionVec(this), Vec.positionVec(this));
		this.customization = new CustomizationVolume();

		this.isAttachedToMainHand = true;
		this.isAttachedToSurface = false;
	}

	public GrapplinghookEntity(Level world, LivingEntity shooter, boolean isAttachedToMainHand, CustomizationVolume customization, boolean isInDoublePair) {
		super(GrappleModEntities.GRAPPLE_HOOK.get(), shooter.position().x, shooter.position().y + shooter.getEyeHeight(), shooter.position().z, world);
		
		this.shootingEntity = shooter;
		this.shootingEntityID = this.shootingEntity.getId();
		
		this.isInDoublePair = isInDoublePair;
		
		Vec pos = Vec.positionVec(this.shootingEntity).add(new Vec(0, this.shootingEntity.getEyeHeight(), 0));

		this.segmentHandler = new RopeSegmentHandler(this, new Vec(pos), new Vec(pos));

		this.customization = customization;
		this.ropeLength = customization.get(MAX_ROPE_LENGTH.get());
		
		this.isAttachedToMainHand = isAttachedToMainHand;
		this.isAttachedToSurface = false;
	}

	public GrapplinghookEntity(HookSnapshot snapshot, CustomizationVolume volume, Entity shootingEntity, boolean isMainHook, boolean isInPair) {
		super(GrappleModEntities.GRAPPLE_HOOK.get(), snapshot.getX(), snapshot.getY(), snapshot.getZ(), shootingEntity.level());

		RopeSnapshot rope = snapshot.getRopeSnapshot();

		this.shootingEntity = shootingEntity;
		this.shootingEntityID = shootingEntity.getId();

		this.segmentHandler = new RopeSegmentHandler(this, shootingEntity, rope);

		this.customization = volume;
		this.ropeLength = rope.getRopeLength();
		this.isAttachedToMainHand = isMainHook;
		this.isInDoublePair = isInPair;

		//this.isAttachedToSurface = snapshot.isAttached();
	}



	@Override
    public void writeSpawnData(FriendlyByteBuf data) {
	    data.writeInt(this.shootingEntity != null ? this.shootingEntity.getId() : 0);
	    data.writeBoolean(this.isAttachedToMainHand);
	    data.writeBoolean(this.isInDoublePair);
		data.writeBoolean(this.isAttachedToSurface);
	    if (this.customization == null) {
	    	GrappleMod.LOGGER.warn("error: customization null");
	    }
	    this.customization.writeToBuf(data);
    }
	
	@Override
    public void readSpawnData(FriendlyByteBuf data) {
    	this.shootingEntityID = data.readInt();
	    this.shootingEntity = this.level().getEntity(this.shootingEntityID);
	    this.isAttachedToMainHand = data.readBoolean();
	    this.isInDoublePair = data.readBoolean();
		this.isAttachedToSurface = data.readBoolean();
	    this.customization = new CustomizationVolume();
	    this.customization.readFromBuf(data);
    }

	@Override
	@NotNull
	public ItemStack getItem() {
		return new ItemStack(this.getDefaultItem());
	}

	@Override
	@NotNull
	public Packet<ClientGamePacketListener> getAddEntityPacket() {
		return new ClientboundAddEntityPacket(this);
	}

	@Override
	public boolean shouldRenderAtSqrDistance(double p_70112_1_) {
		return true;
	}

	@Override
	public boolean shouldRender(double p_145770_1_, double p_145770_3_, double p_145770_5_) {
		return true;
	}

	@Override
	protected float getGravity() {
		if (this.isAttachedToSurface)
			return 0.0F;

		return this.customization.get(HOOK_GRAVITY_MULTIPLIER.get()).floatValue() * 0.1F;
	}

	@Override
	public AABB getBoundingBoxForCulling() {
		if (this.shootingEntity == null) {
			return super.getBoundingBoxForCulling();
		}
		return this.segmentHandler.getBoundingBox(Vec.positionVec(this), Vec.positionVec(this.shootingEntity).add(new Vec(0, this.shootingEntity.getEyeHeight(), 0)));
	}

	@NotNull
	@Override
	protected Item getDefaultItem() {
		return GrappleModItems.GRAPPLING_HOOK.get();
	}

	@Override
	public void tick() {
		if (this.shootingEntityID == 0 || this.shootingEntity == null) { // removes ghost grappling hooks
			this.remove(RemovalReason.DISCARDED);
		}

		if (this.isFirstAttach) {
			this.setDeltaMovement(0, 0, 0);
			this.isFirstAttach = false;
			super.setPos(this.thisPos.x, this.thisPos.y, this.thisPos.z);
		}

		if (this.isAttachedToSurface) {
			this.setDeltaMovement(0, 0, 0);
		}

		super.tick();

		boolean hookIsDetached = !this.level().isClientSide &&
				                  this.shootingEntity != null &&
				                 !this.isAttachedToSurface;

		if(!hookIsDetached) return;

		if (this.segmentHandler.hookPastBend(this.ropeLength)) {
			Vec farthest = this.segmentHandler.getFarthest();
			this.serverAttach(this.segmentHandler.getBendBlock(1), farthest, null);
		}

		if (!this.customization.get(BLOCK_PHASE_ROPE.get())) {
			this.segmentHandler.update(Vec.positionVec(this), Vec.positionVec(this.shootingEntity).add(new Vec(0, this.shootingEntity.getEyeHeight(), 0)), this.ropeLength, true);

			if (this.customization.get(STICKY_ROPE.get())) {
				List<Vec> segments = this.segmentHandler.getSegments();

				if (segments.size() > 2) {
					int bendnumber = segments.size() - 2;
					Vec closest = segments.get(bendnumber);
					BlockPos blockpos = this.segmentHandler.getBendBlock(bendnumber);

					for (int i = 1; i <= bendnumber; i++)
						this.segmentHandler.removeSegment(1);

					this.serverAttach(blockpos, closest, null);
				}
			}

		} else {
			this.segmentHandler.updatePos(Vec.positionVec(this), Vec.positionVec(this.shootingEntity).add(new Vec(0, this.shootingEntity.getEyeHeight(), 0)), this.ropeLength);
		}

		Vec farthest = this.segmentHandler.getFarthest();
		double distToFarthest = this.segmentHandler.getDistToFarthest();

		Vec ropevec = Vec.positionVec(this).sub(farthest);
		double d = ropevec.length();

		if (this.customization.get(HOOK_REEL_IN_ON_SNEAK.get()) && this.shootingEntity.isCrouching()) {
			double newdist = d + distToFarthest - 0.4;
			if (newdist > 1 && newdist <= this.customization.get(MAX_ROPE_LENGTH.get())) {
				this.ropeLength = newdist;
			}
		}


		if (d + distToFarthest > this.ropeLength) {
			Vec motion = Vec.motionVec(this);

			if (motion.dot(ropevec) > 0) {
				motion = motion.removeAlong(ropevec);
			}

			this.setVelocityActually(motion.x, motion.y, motion.z);

			ropevec.mutableSetMagnitude(this.ropeLength - distToFarthest);
			Vec newpos = ropevec.add(farthest);

			this.setPos(newpos.x, newpos.y, newpos.z);
		}

		// magnet attraction

		boolean shouldAttactMagnet = this.customization.get(MAGNET_ATTACHED.get()) &&
				Vec.positionVec(this).sub(Vec.positionVec(this.shootingEntity)).length() >
						this.customization.get(MAGNET_RADIUS.get());

		if (shouldAttactMagnet) handleMagnetAttraction();
	}

	private void handleMagnetAttraction() {
		if (this.foundBlock) return;

		Vec playerpos = Vec.positionVec(this.shootingEntity);
		Vec pos = Vec.positionVec(this);
		if (magnetBlock == null) {
			if (prevPos != null) {
				HashMap<BlockPos, Boolean> checkedset = new HashMap<>();
				Vec vector = pos.sub(prevPos);
				if (vector.length() > 0) {
					Vec normvector = vector.normalize();
					for (int i = 0; i < vector.length(); i++) {
						double dist = prevPos.sub(playerpos).length();
						int radius = (int) dist / 4;
						BlockPos found = this.check(prevPos, checkedset);
						if (found != null) {
							//if (wasinair) {
							Vec distvec = new Vec(found.getX(), found.getY(), found.getZ());
							distvec.mutableSub(prevPos);
							if (distvec.length() < radius) {
								this.setPosRaw(prevPos.x, prevPos.y, prevPos.z);
								pos = prevPos;

								magnetBlock = found;

								break;
							}
							//}
						} else {
							wasInAir = true;
						}

						prevPos.mutableAdd(normvector);
					}
				}
			}
		}

		if (magnetBlock != null) {
			BlockState blockstate = this.level().getBlockState(magnetBlock);
			VoxelShape BB = blockstate.getCollisionShape(this.level(), magnetBlock);

			Vec blockvec = new Vec(magnetBlock.getX() + (BB.max(Axis.X) + BB.min(Axis.X)) / 2, magnetBlock.getY() + (BB.max(Axis.Y) + BB.min(Axis.Y)) / 2, magnetBlock.getZ() + (BB.max(Axis.Z) + BB.min(Axis.Z)) / 2);
			Vec newvel = blockvec.sub(pos);

			double l = newvel.length();

			newvel.withMagnitude(this.getSpeed());

			this.setDeltaMovement(newvel.x, newvel.y, newvel.z);

			if (l < 0.2) {
				this.serverAttach(magnetBlock, blockvec, Direction.UP);
			}
		}

		prevPos = pos;
	}

	@Override
	protected void onHit(HitResult movingobjectposition) {
		if (!this.level().isClientSide) {
			if (this.isAttachedToSurface) {
				return;
			}
			if (this.shootingEntity == null || this.shootingEntityID == 0) {
				return;
			}
			if (movingobjectposition == null) {
				return;
			}

			Vec vec3d = Vec.positionVec(this);
			Vec vec3d1 = vec3d.add(Vec.motionVec(this));

			if (movingobjectposition instanceof EntityHitResult && !GrappleModLegacyConfig.getConf().grapplinghook.other.hookaffectsentities) {
				onHit(GrappleModUtils.rayTraceBlocks(this, this.level(), vec3d, vec3d1));
				return;
			}

			BlockHitResult blockhit = null;
			if (movingobjectposition instanceof BlockHitResult) {
				blockhit = (BlockHitResult) movingobjectposition;
			}

			if (blockhit != null) {
				BlockPos blockpos = blockhit.getBlockPos();
				Block block = this.level().getBlockState(blockpos).getBlock();
				if (ConfigUtility.breaksBlock(block)) {
					this.level().destroyBlock(blockpos, true);
					onHit(GrappleModUtils.rayTraceBlocks(this, this.level(), vec3d, vec3d1));
					return;
				}
			}

			if (movingobjectposition instanceof EntityHitResult entityHit) {
				// hit entity
				Entity entity = entityHit.getEntity();
				if (entity == this.shootingEntity) {
					return;
				}

				Vec playerpos = Vec.positionVec(this.shootingEntity);
				Vec entitypos = Vec.positionVec(entity);
				Vec yank = playerpos.sub(entitypos).scale(0.4);
				yank.y = Math.min(yank.y, 2);
				Vec newmotion = Vec.motionVec(entity).add(yank);
				entity.setDeltaMovement(newmotion.toVec3d());

				this.removeServer();

			} else if (blockhit != null) {
				BlockPos blockpos = blockhit.getBlockPos();

				Vec vec3 = new Vec(movingobjectposition.getLocation());

				this.serverAttach(blockpos, vec3, blockhit.getDirection());
			} else {
				GrappleMod.LOGGER.warn("unknown impact?");
			}
		}
	}


	public void removeServer() {
		this.remove(RemovalReason.DISCARDED);
		this.shootingEntityID = 0;
	}

	public void shoot(Vec direction, double speed, float inaccuracy) {
		this.shoot(direction.getX(), direction.getY(), direction.getZ(), (float) speed, inaccuracy);
	}

	public void setVelocityActually(double x, double y, double z) {
		this.setDeltaMovement(x, y, z);

        if (this.xRotO == 0.0F && this.yRotO == 0.0F) {
            double f = Math.sqrt(x * x + z * z);
            this.setYRot((float)(Mth.atan2(x, z) * (180D / Math.PI)));
            this.setXRot((float)(Mth.atan2(y, f) * (180D / Math.PI)));
            this.yRotO = this.getYRot();
            this.xRotO = this.getXRot();
        }
	}

	public void serverAttach(BlockPos blockpos, Vec pos, Direction sideHit) {
		if (this.isAttachedToSurface)
			return;

		if (this.shootingEntity == null || this.shootingEntityID == 0)
			return;

		this.isAttachedToSurface = true;
		this.lastBlockCollision = blockpos;
		this.lastSubCollisionPos = pos;
		this.lastBlockCollisionSide = sideHit;

		if (blockpos != null) {
			Block block = this.level().getBlockState(blockpos).getBlock();

			if (!ConfigUtility.attachesBlock(block)) {
				this.removeServer();
				return;
			}
		}

		Vec vec3 = Vec.positionVec(this);
		vec3.mutableAdd(Vec.motionVec(this));

		if (pos != null) {
            vec3 = pos;
            this.setPosRaw(vec3.x, vec3.y, vec3.z);
		}

		//west -x
		//north -z
		Vec curpos = Vec.positionVec(this);
		switch (sideHit) {
			case DOWN  -> curpos.y -= 0.3f;
			case WEST  -> curpos.x -= 0.05f;
			case NORTH -> curpos.z -= 0.05f;
			case SOUTH -> curpos.z += 0.05f;
			case EAST  -> curpos.x += 0.05f;
			case UP    -> curpos.y += 0.05f;
		}
		curpos.applyAsPositionTo(this);

		this.setDeltaMovement(0, 0, 0);

        this.thisPos = Vec.positionVec(this);
		this.isFirstAttach = true;

		GrappleModUtils.sendToCorrectClient(new GrappleAttachMessage(this.getId(), this.position().x, this.position().y, this.position().z, this.shootingEntityID, blockpos, this.segmentHandler.segments, this.segmentHandler.segmentTopSides, this.segmentHandler.segmentBottomSides, this.customization), this.shootingEntityID, this.level());

		GrappleAttachPosMessage msg = new GrappleAttachPosMessage(this.getId(), this.position().x, this.position().y, this.position().z);
		NetworkManager.packetToClient(msg, GrappleModUtils.getPlayersThatCanSeeChunkAt((ServerLevel) this.level(), new Vec(this.position())));
	}

	public void clientAttach(double x, double y, double z) {
		this.setAttachPos(x, y, z);

		if (this.shootingEntity instanceof Player) {
			GrappleModClient.get().resetLauncherTime(this.shootingEntityID);
		}
	}

	public void setAttachPos(double x, double y, double z) {
		this.setPosRaw(x, y, z);

		this.setDeltaMovement(0, 0, 0);
		this.isFirstAttach = true;
		this.isAttachedToSurface = true;
        this.thisPos = new Vec(x, y, z);
	}

	// used for magnet attraction

	public BlockPos check(Vec p, HashMap<BlockPos, Boolean> checkedset) {
    	int radius = (int) Math.floor(this.customization.get(MAGNET_RADIUS.get()));
    	BlockPos closestpos = null;
    	double closestdist = 0;
    	for (int x = (int)p.x - radius; x <= (int)p.x + radius; x++) {
        	for (int y = (int)p.y - radius; y <= (int)p.y + radius; y++) {
            	for (int z = (int)p.z - radius; z <= (int)p.z + radius; z++) {
			    	BlockPos pos = new BlockPos(x, y, z);
					if (hasBlock(pos, checkedset)) {
						Vec distvec = new Vec(pos.getX(), pos.getY(), pos.getZ());
						distvec.mutableSub(p);
						double dist = distvec.length();
						if (closestpos == null || dist < closestdist) {
							closestpos = pos;
							closestdist = dist;
						}
					}
				}
	    	}
    	}
		return closestpos;
	}
	// used for magnet attraction

	public boolean hasBlock(BlockPos pos, HashMap<BlockPos, Boolean> checkedset) {
    	if (!checkedset.containsKey(pos)) {
    		boolean isblock = false;
	    	BlockState blockstate = this.level().getBlockState(pos);
	    	Block b = blockstate.getBlock();
			if (ConfigUtility.attachesBlock(b)) {
		    	if (!(blockstate.isAir())) {
			    	VoxelShape BB = blockstate.getCollisionShape(this.level(), pos);
			    	if (!BB.isEmpty()) {
			    		isblock = true;
			    	}
		    	}
			}

	    	checkedset.put(pos, isblock);
	    	return isblock;
    	} else {
    		return checkedset.get(pos);
    	}
	}

	public double getSpeed() {
		return this.customization.get(HOOK_THROW_SPEED.get());
	}

	public CustomizationVolume getCurrentCustomizations() {
		return this.customization;
	}

	public RopeSegmentHandler getSegmentHandler() {
		return this.segmentHandler;
	}

	public boolean isAttachedToSurface() {
		return this.isAttachedToSurface;
	}

	public boolean isHeldInMainHand() {
		return this.isAttachedToMainHand;
	}

	public Vec getSurfaceAttachmentDirection() {
		return this.attachDirection;
	}

	public double getCurrentRopeLength() {
		return this.ropeLength;
	}

	public BlockPos getLastBlockCollision() {
		return this.lastBlockCollision;
	}

	public Vec getLastSubCollisionPos() {
		return this.lastSubCollisionPos;
	}

	public Direction getLastBlockCollisionSide() {
		return this.lastBlockCollisionSide;
	}
}
