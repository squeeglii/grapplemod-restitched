package me.cg360.mod.grapplemod.compat.gliders.mixin;

import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.content.physics.PhysicsControllers;
import com.yyon.grapplinghook.physics.PlayerPhysicsFrame;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.venturecraft.gliders.util.GliderUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(GliderUtil.class)
public class GliderUtilMixin {

    @Inject(method = "canDeployHere(Lnet/minecraft/world/entity/LivingEntity;)Z",
            at = @At("HEAD"),
            cancellable = true)
    private static void handleGrapplingDeployCondition(LivingEntity livingEntity, CallbackInfoReturnable<Boolean> cir) {
        if(!(livingEntity instanceof Player player)) return;


        Optional<PlayerPhysicsFrame> mostRecentFrame = GrappleMod.get().getServerPhysicsObserver()
                                                                       .getMostRecentFrame(player);

        // No record of physics so there's nothing to check. Use vanilla checks only.
        if(mostRecentFrame.isEmpty())
            return;

        ResourceLocation controllerType = mostRecentFrame.get().getPhysicsControllerType();

        // Sanity
        if(controllerType == null) return;

        boolean usingValidController = controllerType.equals(PhysicsControllers.AIR_FRICTION) ||
                                       controllerType.equals(PhysicsControllers.GRAPPLING_HOOK);

        // Can't be deployed with forcefield either as that just feels jank.
        if(usingValidController) {
            cir.setReturnValue(true);
            cir.cancel();
        }
    }

}
