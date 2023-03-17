package com.yyon.grapplinghook.physics.context;

import com.yyon.grapplinghook.util.Vec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class ForcefieldPhysicsContext extends GrapplingHookPhysicsContext {
	public ForcefieldPhysicsContext(int grapplehookEntityId, int entityId, Level world, int id) {
		super(grapplehookEntityId, entityId, world, id, null);
		
		this.playerMovementMult = 1;
	}

	public void updatePlayerPos() {
		Entity entity = this.entity;
		
		if (this.attached) {
			if(entity != null) {
				this.normalGround(false);
				this.normalCollisions(false);
//					this.applyAirFriction();

				Vec playerpos = Vec.positionVec(entity);

//					double dist = oldspherevec.length();

				if (playerSneak) {
					motion.mutableScale(0.95);
				}
				applyPlayerMovement();

				Vec blockpush = checkRepel(playerpos, entity.level());
				blockpush.mutableScale(0.5);
				blockpush = new Vec(blockpush.x*0.5, blockpush.y*2, blockpush.z*0.5);
				this.motion.mutableAdd(blockpush);

				if (!entity.onGround()) {
					motion.mutableAdd(0, -0.05, 0);
				}

				motion.setMotion(this.entity);

				this.updateServerPos();
			}
		}
	}
}
