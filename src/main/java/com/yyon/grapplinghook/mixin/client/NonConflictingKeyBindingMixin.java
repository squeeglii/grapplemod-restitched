package com.yyon.grapplinghook.mixin.client;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

// Trying to avoid overwites and collisions by just making a new system
// entirely and forwarding things to that.
@Mixin(KeyMapping.class)
public abstract class NonConflictingKeyBindingMixin {

    // redirect away from the original to this
    private static final Map<InputConstants.Key, HashMap<String, KeyMapping>> CLASH_AVOIDANCE_CLUSTERS = Maps.newHashMap();

    @Final
    @Shadow
    private static Map<String, KeyMapping> ALL;

    @Final
    @Shadow
    private static Map<InputConstants.Key, KeyMapping> MAP;

    @Shadow private int clickCount;
    @Shadow private InputConstants.Key key;

    @Shadow public abstract void setDown(boolean value);

    private static HashMap<String, KeyMapping> getOrCreateKeybindMapForKey(InputConstants.Key key) {
        if(CLASH_AVOIDANCE_CLUSTERS.containsKey(key) && CLASH_AVOIDANCE_CLUSTERS.get(key) != null)
            return CLASH_AVOIDANCE_CLUSTERS.get(key);

        HashMap<String, KeyMapping> mappingGroup = Maps.newHashMap();
        CLASH_AVOIDANCE_CLUSTERS.put(key, mappingGroup);
        return mappingGroup;
    }




    @Inject(method = "click(Lcom/mojang/blaze3d/platform/InputConstants$Key;)V", at = @At("TAIL"))
    private static void click(InputConstants.Key key, CallbackInfo ci) {
        HashMap<String, KeyMapping> keyMappings = CLASH_AVOIDANCE_CLUSTERS.get(key);
        if (keyMappings != null) {
            keyMappings.forEach((k, m) ->
                ++((NonConflictingKeyBindingMixin) (Object) m).clickCount
            );
        }
    }

    @Inject(method = "set(Lcom/mojang/blaze3d/platform/InputConstants$Key;Z)V", at = @At("TAIL"))
    private static void set(InputConstants.Key key, boolean held, CallbackInfo ci) {
        HashMap<String, KeyMapping> keyMappings = CLASH_AVOIDANCE_CLUSTERS.get(key);
        if (keyMappings != null) {
            keyMappings.forEach((k, m) ->
                    ((NonConflictingKeyBindingMixin) (Object) m).setDown(held)
            );
        }
    }

    @Inject(method = "resetMapping()V", at = @At("TAIL"))
    private static void resetMapping(CallbackInfo ci) {
        CLASH_AVOIDANCE_CLUSTERS.clear();

        ALL.values().forEach(keyMapping -> {
            if(MAP.get(keyForMapping(keyMapping)).getName().equals(keyMapping.getName()))
                return; // already assigned.

            HashMap<String, KeyMapping> group = getOrCreateKeybindMapForKey(keyForMapping(keyMapping));
            group.put(keyMapping.getName(), keyMapping);
        });
    }


    @Inject(method = "<init>(Ljava/lang/String;Lcom/mojang/blaze3d/platform/InputConstants$Type;ILjava/lang/String;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;",
                    ordinal = 0,
                    shift = At.Shift.BEFORE
            ))
    private void injectConstructor(String name, InputConstants.Type type, int keyCode, String category, CallbackInfo ci) {
        InputConstants.Key key = type.getOrCreate(keyCode);
        KeyMapping olderMappingSharedKey = MAP.get(key); // Key to replace
        KeyMapping olderMappingSharedName = ALL.get(name); // Older mapping for this mapping name

        // If a keybinding for this name already exists, remove it.
        if(olderMappingSharedName != null) {
            InputConstants.Key olderMappingKey = keyForMapping(olderMappingSharedName);
            MAP.remove(olderMappingKey);

            if(CLASH_AVOIDANCE_CLUSTERS.containsKey(olderMappingKey)) {
                CLASH_AVOIDANCE_CLUSTERS.get(olderMappingKey)
                        .remove(olderMappingSharedName.getName());
            }
        }

        // Remap the old keybinding for this key to the non-conflict pool
        if (olderMappingSharedKey != null) {

            // Remapped this keybind name to the same key? Ignore and let
            // vanilla behaviour replace it with itself.
            if(olderMappingSharedKey.getName().equals(name))
                return;

            // Key has a different name to this one. Move it to the non-conflict pool.
            HashMap<String, KeyMapping> group = getOrCreateKeybindMapForKey(key);
            group.put(olderMappingSharedKey.getName(), olderMappingSharedKey);
        }
    }

    protected InputConstants.Key getBoundKey() {
        return this.key;
    }

    private static InputConstants.Key keyForMapping(KeyMapping mapping) {
        return ((NonConflictingKeyBindingMixin) (Object) mapping).getBoundKey();
    }
}
