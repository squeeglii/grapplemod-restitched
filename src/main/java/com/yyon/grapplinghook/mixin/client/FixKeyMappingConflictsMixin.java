package com.yyon.grapplinghook.mixin.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.yyon.grapplinghook.client.keybind.NonConflictKeyMapping;
import net.minecraft.client.KeyMapping;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

@Mixin(KeyMapping.class)
public class FixKeyMappingConflictsMixin {

    @Shadow @Final private static Map<InputConstants.Key, KeyMapping> MAP;

    @Shadow @Final private static Map<String, KeyMapping> ALL;

    @Inject(method = "click(Lcom/mojang/blaze3d/platform/InputConstants$Key;)V", at = @At("HEAD"))
    private static void click(InputConstants.Key key, CallbackInfo ci) {
        forNonPrimaryMappingsDo(key, overflowMapping -> ++overflowMapping.clickCount);
    }

    @Inject(method = "set(Lcom/mojang/blaze3d/platform/InputConstants$Key;Z)V", at = @At("HEAD"))
    private static void set(InputConstants.Key key, boolean held, CallbackInfo ci) {
        forNonPrimaryMappingsDo(key, overflowMapping -> overflowMapping.setDown(held));
    }

    @Inject(method = "resetMapping()V", at = @At("HEAD"))
    private static void resetMapping(CallbackInfo ci) {
        NonConflictKeyMapping.resetAllOverflowMappings();

        for (KeyMapping keyMapping : ALL.values()) {
            NonConflictKeyMapping.addToOverflowMappings(keyMapping);
        }
    }


    @Unique
    private static void forNonPrimaryMappingsDo(InputConstants.Key key, Consumer<KeyMapping> action) {
        // At least one key must be mapped in MAP for the overflow to contain a key
        KeyMapping primaryMapping = MAP.get(key);
        if(primaryMapping == null)
            return;

        Set<KeyMapping> associatedOverflows = NonConflictKeyMapping.getExtraKeyMappings().get(key);

        if(associatedOverflows == null)
            return;

        associatedOverflows.stream()
                // Ensure the primary key is not being triggered twice
                .filter(overflowMapping -> !primaryMapping.equals(overflowMapping))
                .forEach(action);
    }

}
