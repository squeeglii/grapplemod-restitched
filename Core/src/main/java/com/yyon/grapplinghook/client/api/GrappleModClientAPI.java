package com.yyon.grapplinghook.client.api;

import com.yyon.grapplinghook.client.GrappleModClient;
import com.yyon.grapplinghook.client.physics.context.GrapplingHookPhysicsController;
import com.yyon.grapplinghook.content.physics.PhysicsControllers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public class GrappleModClientAPI {

    public static ResourceLocation getPhysicsTypeFor(LivingEntity entity) {
        if(entity == null)
            return PhysicsControllers.NONE;

        GrapplingHookPhysicsController controller = GrappleModClient.get()
                                                                    .getClientControllerManager()
                                                                    .getController(entity.getId());

        return controller == null
                ? PhysicsControllers.NONE
                : controller.getType();
    }


    public static void abortAllPhysicsOverrides(LivingEntity entity) {
        if(entity == null) return;

        GrapplingHookPhysicsController controller = GrappleModClient.get()
                .getClientControllerManager()
                .getController(entity.getId());

        if(controller == null)
            return;

        controller.disable(true);
    }

}
