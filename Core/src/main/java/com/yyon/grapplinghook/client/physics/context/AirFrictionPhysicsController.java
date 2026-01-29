package com.yyon.grapplinghook.client.physics.context;

import com.yyon.grapplinghook.client.GrappleModClient;
import com.yyon.grapplinghook.config.GrappleModCommonConfig;
import com.yyon.grapplinghook.content.physics.PhysicsControllers;
import com.yyon.grapplinghook.customization.data.HookCustomization;
import com.yyon.grapplinghook.util.EnchantmentValues;
import com.yyon.grapplinghook.util.GrappleModUtils;
import com.yyon.grapplinghook.util.Vec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import static com.yyon.grapplinghook.content.registry.CustomizationProperties.ROCKET_ATTACHED;

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

public class AirFrictionPhysicsController extends GrapplingHookPhysicsController {

	public static final double DEG_90 = Math.PI / 2;
	
	private int ignoreGroundCounter = 0;
	private boolean wasSliding = false;
	private boolean wasWallrunning = false;
	private boolean wasRocket = false;
	private boolean firstTickSinceCreated = true;


	public AirFrictionPhysicsController(int grapplehookEntityId, int entityId, Level world, HookCustomization custom) {
		super(grapplehookEntityId, entityId, world, custom);
	}

	@Override
	public ResourceLocation getType() {
		return PhysicsControllers.AIR_FRICTION;
	}

	@Override
	public void updatePlayerPos() {
		LivingEntity entity = this.holder;

		if (entity == null) return;

		if (entity.getVehicle() != null) {
			this.disable();
			this.updateServerPos();
			return;
		}

		if(this.motion.y > 0)
			entity.resetFallDistance();

		if (entity instanceof LivingEntity e && e.onClimbable()) {
			this.disable();
		}

		boolean shouldCancel = GrappleModUtils.and(
				() -> !GrappleModCommonConfig.get().shouldOverrideMovementInAir(),
				() -> !entity.onGround(),
				() -> !this.wasSliding,
				() -> !this.wasWallrunning,
				() -> !this.wasRocket,
				() -> !this.firstTickSinceCreated
		);

		if (shouldCancel) {
			this.motion = Vec.motionVec(entity);
			this.disable();
			return;
		}

		if (!this.isControllerActive())
			return;

		Vec additionalMotion = new Vec(0,0,0);

		boolean isSliding = GrappleModClient.get().isSliding(entity, motion);

		if (isSliding && !this.wasSliding) {
			this.playSlideSound();
		}

		if (this.ignoreGroundCounter <= 0) {
			this.normalGround(isSliding);
			this.normalCollisions(isSliding);
		}

		this.applyAirFriction();

		if (this.holder.isInWater() || this.holder.isInLava()) {
			this.disable();
			return;
		}

		boolean doesrocket = false;
		if (this.getCurrentCustomizations() != null) {
			if (this.getCurrentCustomizations().get(ROCKET_ATTACHED.get())) {
				Vec rocket = this.rocket(entity);
				this.motion.mutableAdd(rocket);
				if (rocket.length() > 0) {
					doesrocket = true;
				}
			}
		}

		if (isSliding) {
			this.applySlidingFriction();
		}

		boolean wallrun = this.applyWallRun();

		if (!isSliding && !this.wasSliding) {

			if (wallrun) {
				motion = motion.removeAlong(new Vec(0,1,0));

				if (this.getWallDirection() != null)
					motion = motion.removeAlong(this.getWallDirection());

				Vec newMovement = this.playerMovement.withMagnitude(EnchantmentValues.BASE_WALLRUN_SPEED *1.5);
				if (this.getWallDirection() != null) {
					newMovement = newMovement.removeAlong(this.getWallDirection());
				}
				if (newMovement.length() > EnchantmentValues.BASE_WALLRUN_SPEED) {
					newMovement.mutableSetMagnitude(EnchantmentValues.BASE_WALLRUN_SPEED);
				}

				Vec current_motion_along = this.motion.removeAlong(new Vec(0,1,0));
				Vec new_motion_along = this.motion.add(newMovement).removeAlong(new Vec(0,1,0));

				if (this.getWallDirection() != null) {
					current_motion_along = current_motion_along.removeAlong(this.getWallDirection());
					new_motion_along = new_motion_along.removeAlong(this.getWallDirection());
				}

				if (current_motion_along.length() <= EnchantmentValues.MAX_WALLRUN_SPEED || current_motion_along.dot(newMovement) < 0) {
					motion.mutableAdd(newMovement);
					if (new_motion_along.length() > EnchantmentValues.MAX_WALLRUN_SPEED) {
						this.motion.mutableSetMagnitude(EnchantmentValues.MAX_WALLRUN_SPEED);
					}
				}
				additionalMotion.mutableAdd(this.wallrunPressAgainstWall());

			} else {
				double max_motion = GrappleModCommonConfig.get().getMaxStrafeSpeedInAir();
				double accel = GrappleModCommonConfig.get().getStrafeAcceleration();
				Vec motion_horizontal = motion.removeAlong(new Vec(0,1,0));
				double prev_motion = motion_horizontal.length();
				Vec new_motion_horizontal = motion_horizontal.add(this.playerMovement.withMagnitude(accel));
				double angle = motion_horizontal.angle(new_motion_horizontal);

				if (new_motion_horizontal.length() > max_motion && new_motion_horizontal.length() > prev_motion) {
					double newMaxMotion = max_motion;

					if (angle < DEG_90 && prev_motion > max_motion)
						newMaxMotion = prev_motion + ((max_motion - prev_motion) * (angle / (DEG_90)));

					new_motion_horizontal.mutableSetMagnitude(newMaxMotion);
				}

				motion.x = new_motion_horizontal.x;
				motion.z = new_motion_horizontal.z;
			}
		}

		if (entity instanceof LivingEntity entityLiving && entityLiving.isFallFlying()) {
			this.disable();
		}

		Vec gravity = new Vec(0, -0.10, 0);

		if (!wallrun)
			this.motion.mutableAdd(gravity);


		// All changes to motion should happen BEFORE this point -- !!
		Vec newMotion = this.motion.add(additionalMotion);
		newMotion.applyAsMotionTo(entity);

		this.updateServerPos();

		if (entity.onGround() && !isSliding && !wallrun) {
			if (!doesrocket) {
				if (this.ignoreGroundCounter <= 0)
					this.disable();

			} else {
				this.motion = Vec.motionVec(entity);
			}
		}

		if (this.ignoreGroundCounter > 0)
			this.ignoreGroundCounter--;

		this.wasSliding = isSliding;
		this.wasWallrunning = wallrun;
		this.wasRocket = doesrocket;
		this.firstTickSinceCreated = false;
	}

	public void receiveEnderLaunch(double x, double y, double z) {
		super.receiveEnderLaunch(x, y, z);
		this.ignoreGroundCounter = 2;
	}
	
	public void doSlidingJump() {
		super.doSlidingJump();
		this.ignoreGroundCounter = 2;
	}
	
	public void playSlideSound() {
		GrappleModClient.get().playSlideSound();
	}

	public boolean wasSliding() {
		return this.wasSliding;
	}

	public boolean wasWallRunning() {
		return this.wasWallrunning;
	}
}
