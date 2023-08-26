package com.yyon.grapplinghook.client.physics.context;

import com.yyon.grapplinghook.util.Vec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class ForcefieldPhysicsController extends GrapplingHookPhysicsController {

	public ForcefieldPhysicsController(int grapplehookEntityId, int entityId, Level world, int id) {
		super(grapplehookEntityId, entityId, world, id, null);
		
		this.playerMovementMult = 1f;
	}

	@Override
	public void updatePlayerPos() {
		Entity entity = this.entity;
		
		if (!this.isControllerActive()) return;
		if(entity == null) return;

		this.normalGround(false);
		this.normalCollisions(false);

		Vec playerPos = Vec.positionVec(entity);

		if (this.playerSneak)
			this.motion.mutableScale(0.95);

		this.applyPlayerMovement();

		Vec blockPush = this.checkRepel(playerPos, entity.level())
				            .mutableScale(0.5D)
				            .multiply(0.5D, 2.0D, 0.5D);
		this.motion.mutableAdd(blockPush);

		if (!entity.onGround())
			this.motion.mutableAdd(0, -0.05D, 0);

		this.motion.applyAsMotionTo(this.entity);
		this.updateServerPos();
	}
}
