package com.yyon.grapplinghook.physics.context;

import com.yyon.grapplinghook.util.Vec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class ForcefieldPhysicsController extends GrapplingHookPhysicsController {

	public ForcefieldPhysicsController(int grapplehookEntityId, int entityId, Level world, int id) {
		super(grapplehookEntityId, entityId, world, id, null);
		
		this.playerMovementMult = 1;
	}

	@Override
	public void updatePlayerPos() {
		Entity entity = this.entity;
		
		if (!this.isControllerActive()) return;
		if(entity == null) return;

		this.normalGround(false);
		this.normalCollisions(false);
//		this.applyAirFriction();

		Vec playerpos = Vec.positionVec(entity);

//		double dist = oldspherevec.length();

		if (this.playerSneak)
			this.motion.mutableScale(0.95);

		this.applyPlayerMovement();

		Vec blockpush = this.checkRepel(playerpos, entity.level())
				            .mutableScale(0.5)
				            .multiply(0.5D, 2.0D, 0.5D);

		this.motion.mutableAdd(blockpush);

		if (!entity.onGround())
			this.motion.mutableAdd(0, -0.05, 0);

		this.motion.applyAsMotionTo(this.entity);
		this.updateServerPos();
	}
}
