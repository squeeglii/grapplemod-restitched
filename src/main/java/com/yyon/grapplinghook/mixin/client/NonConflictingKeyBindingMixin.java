package com.yyon.grapplinghook.mixin.client;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.InputConstants;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.KeyMapping;

// Trying to avoid overwites and collisions by just making a new system
// entirely and forwarding things to that.
@Mixin(KeyMapping.class)
public abstract class NonConflictingKeyBindingMixin {

    // redirect away from the original to this
    private static final Map<InputConstants.Key, Set<KeyMapping>> MAP_GROUPS = Maps.newHashMap();

    @Final
    @Shadow
    private static Map<String, KeyMapping> ALL;

    @Shadow private int clickCount;
    @Shadow private InputConstants.Key key;

    @Shadow public abstract void setDown(boolean value);


    private static Set<KeyMapping> getOrCreateKeybindMapForKey(InputConstants.Key key) {
        if(MAP_GROUPS.containsKey(key) && MAP_GROUPS.get(key) != null)
            return MAP_GROUPS.get(key);

        HashSet<KeyMapping> mappingGroup = Sets.newHashSet();
        MAP_GROUPS.put(key, mappingGroup);
        return mappingGroup;
    }




    @Inject(method = "click(Lcom/mojang/blaze3d/platform/InputConstants$Key;)V", at = @At("TAIL"))
    private static void click(InputConstants.Key key, CallbackInfo ci) {
        Set<KeyMapping> keyMappings = MAP_GROUPS.get(key);
        if (keyMappings != null) {
            keyMappings.forEach(m ->
                ++((NonConflictingKeyBindingMixin) (Object) m).clickCount
            );
        }
    }

    @Inject(method = "set(Lcom/mojang/blaze3d/platform/InputConstants$Key;Z)V", at = @At("TAIL"))
    private static void set(InputConstants.Key key, boolean held, CallbackInfo ci) {
        Set<KeyMapping> keyMappings = MAP_GROUPS.get(key);
        if (keyMappings != null) {
            keyMappings.forEach(m ->
                    ((NonConflictingKeyBindingMixin) (Object) m).setDown(held)
            );
        }
    }

    @Inject(method = "resetMapping()V", at = @At("TAIL"))
    private static void resetMapping(CallbackInfo ci) {
        MAP_GROUPS.clear();

        ALL.values().forEach(keyMapping -> {
            Set<KeyMapping> group = getOrCreateKeybindMapForKey(((NonConflictingKeyBindingMixin) (Object) keyMapping).key);
            group.add(keyMapping);
        });
    }


    @Inject(method = "<init>(Ljava/lang/String;Lcom/mojang/blaze3d/platform/InputConstants$Type;ILjava/lang/String;)V", at = @At("TAIL"))
    private void injectConstructor(String name, InputConstants.Type type, int keyCode, String category, CallbackInfo ci) {
        Set<KeyMapping> group = getOrCreateKeybindMapForKey(type.getOrCreate(keyCode));
        group.add((KeyMapping) (Object) this);
    }

}
