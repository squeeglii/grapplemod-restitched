package com.yyon.grapplinghook.client.physics.context;

import com.yyon.grapplinghook.client.GrappleModClient;
import com.yyon.grapplinghook.config.GrappleModLegacyConfig;
import com.yyon.grapplinghook.customization.CustomizationVolume;
import com.yyon.grapplinghook.util.GrappleModUtils;
import com.yyon.grapplinghook.util.Vec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import static com.yyon.grapplinghook.content.registry.GrappleModCustomizationProperties.ROCKET_ATTACHED;

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
	
	private int ignoreGroundCounter = 0;
	private boolean wasSliding = false;
	private boolean wasWallrunning = false;
	private boolean wasRocket = false;
	private boolean firstTickSinceCreated = true;


	public AirFrictionPhysicsController(int grapplehookEntityId, int entityId, Level world, int id, CustomizationVolume custom) {
		super(grapplehookEntityId, entityId, world, id, custom);
	}


	@Override
	public void updatePlayerPos() {
		Entity entity = this.entity;

		if (entity == null) return;

		if (entity.getVehicle() != null) {
			this.disable();
			this.updateServerPos();
			return;
		}

		if (entity instanceof LivingEntity e && e.onClimbable()) {
			this.disable();
		}

		boolean shouldCancel = GrappleModUtils.and(
				() -> GrappleModLegacyConfig.getConf().other.dont_override_movement_in_air,
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

		if (this.entity.isInWater() || this.entity.isInLava()) {
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

				Vec new_movement = this.playerMovement.withMagnitude(GrappleModLegacyConfig.getConf().enchantments.wallrun.wallrun_speed*1.5);
				if (this.getWallDirection() != null) {
					new_movement = new_movement.removeAlong(this.getWallDirection());
				}
				if (new_movement.length() > GrappleModLegacyConfig.getConf().enchantments.wallrun.wallrun_speed) {
					new_movement.mutableSetMagnitude(GrappleModLegacyConfig.getConf().enchantments.wallrun.wallrun_speed);
				}
				Vec current_motion_along = this.motion.removeAlong(new Vec(0,1,0));
				Vec new_motion_along = this.motion.add(new_movement).removeAlong(new Vec(0,1,0));

				if (this.getWallDirection() != null) {
					current_motion_along = current_motion_along.removeAlong(this.getWallDirection());
					new_motion_along = new_motion_along.removeAlong(this.getWallDirection());
				}

				if (current_motion_along.length() <= GrappleModLegacyConfig.getConf().enchantments.wallrun.wallrun_max_speed || current_motion_along.dot(new_movement) < 0) {
					motion.mutableAdd(new_movement);
					if (new_motion_along.length() > GrappleModLegacyConfig.getConf().enchantments.wallrun.wallrun_max_speed) {
						this.motion.mutableSetMagnitude(GrappleModLegacyConfig.getConf().enchantments.wallrun.wallrun_max_speed);
					}
				}
				additionalMotion.mutableAdd(wallrunPressAgainstWall());
			} else {
				double max_motion = GrappleModLegacyConfig.getConf().other.airstrafe_max_speed;
				double accel = GrappleModLegacyConfig.getConf().other.airstrafe_acceleration;
				Vec motion_horizontal = motion.removeAlong(new Vec(0,1,0));
				double prev_motion = motion_horizontal.length();
				Vec new_motion_horizontal = motion_horizontal.add(this.playerMovement.withMagnitude(accel));
				double angle = motion_horizontal.angle(new_motion_horizontal);
				if (new_motion_horizontal.length() > max_motion && new_motion_horizontal.length() > prev_motion) {
					double ninety_deg = Math.PI / 2;
					double new_max_motion = max_motion;
					if (angle < ninety_deg && prev_motion > max_motion) {
						new_max_motion = prev_motion + ((max_motion - prev_motion) * (angle / (Math.PI / 2)));
					}
					new_motion_horizontal.mutableSetMagnitude(new_max_motion);
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
