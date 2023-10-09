package com.yyon.grapplinghook.mixin;

import com.yyon.grapplinghook.physics.ServerHookEntityTracker;
import com.yyon.grapplinghook.physics.io.IHookStateHolder;
import com.yyon.grapplinghook.physics.io.SerializableHookState;
import com.yyon.grapplinghook.util.SharedDamageHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin implements IHookStateHolder {

    @Unique
    private SerializableHookState lastHookState = null;


    @Inject(method = "die(Lnet/minecraft/world/damagesource/DamageSource;)V",
            at = @At("HEAD"))
    public void handleDeath(DamageSource source, CallbackInfo ci){
        SharedDamageHandler.handleDeath((Entity) (Object) this);
    }


    @Inject(method = "addAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V",
            at = @At("TAIL"))
    public void saveLastGrappleState(CompoundTag compound, CallbackInfo ci) {
        this.grapplemod$resetLastHookState(); // Not necessary but peace of mind.
        ServerHookEntityTracker.savePlayerHookState((ServerPlayer) (Object) this, compound);
    }

    @Inject(method = "readAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V",
            at = @At("TAIL"))
    public void readLastGrappleState(CompoundTag compound, CallbackInfo ci) {
        CompoundTag hookState = compound.getCompound(ServerHookEntityTracker.NBT_HOOK_STATE);

        if(!SerializableHookState.isValidNBT(hookState)) {
            this.grapplemod$resetLastHookState();
            return;
        }

        this.lastHookState = SerializableHookState.serializeFrom(hookState);
    }


    @Unique
    @Override
    public void grapplemod$resetLastHookState() {
        this.lastHookState = null;
    }

    @Unique
    @Override
    public void grapplemod$overwriteLastHookState(SerializableHookState hookState) {
        this.lastHookState = hookState;
    }

    @Unique
    @Override
    public Optional<SerializableHookState> grapplemod$getLastHookState() {
        return Optional.ofNullable(this.lastHookState);
    }

}
