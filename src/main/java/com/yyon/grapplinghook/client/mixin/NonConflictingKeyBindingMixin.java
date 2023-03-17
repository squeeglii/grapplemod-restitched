package com.yyon.grapplinghook.client.mixin;

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
    private static final Map<InputConstants.Key, HashMap<String, KeyMapping>> FULL_SELECTION = Maps.newHashMap();

    @Final
    @Shadow
    private static Map<String, KeyMapping> ALL;

    @Final
    @Shadow
    private static Map<InputConstants.Key, KeyMapping> MAP;

    @Shadow private int clickCount;
    @Shadow private InputConstants.Key key;

    @Shadow public abstract void setDown(boolean value);

    @Shadow @Final private String name;

    @Shadow public abstract String getName();

    private static HashMap<String, KeyMapping> getOrCreateKeybindMapForKey(InputConstants.Key key) {
        HashMap<String, KeyMapping> map = FULL_SELECTION.get(key);
        if(map != null)
            return FULL_SELECTION.get(key);

        HashMap<String, KeyMapping> mappingGroup = Maps.newHashMap();
        FULL_SELECTION.put(key, mappingGroup);
        return mappingGroup;
    }




    @Inject(method = "click(Lcom/mojang/blaze3d/platform/InputConstants$Key;)V", at = @At("RETURN"))
    private static void click(InputConstants.Key key, CallbackInfo ci) {
        HashMap<String, KeyMapping> keyMappings = FULL_SELECTION.get(key);

        if (keyMappings != null) {
            keyMappings.forEach((n, m) -> {
                KeyMapping primaryMapping = MAP.get(keyForMapping(m));
                if(primaryMapping == null || !primaryMapping.getName().equals(n)) // don't double tap
                    ++((NonConflictingKeyBindingMixin) (Object) m).clickCount;
            });
        }
    }

    @Inject(method = "set(Lcom/mojang/blaze3d/platform/InputConstants$Key;Z)V", at = @At("RETURN"))
    private static void set(InputConstants.Key key, boolean held, CallbackInfo ci) {
        HashMap<String, KeyMapping> keyMappings = FULL_SELECTION.get(key);

        if (keyMappings != null)
            keyMappings.forEach((n, m) -> ((NonConflictingKeyBindingMixin) (Object) m).setDown(held));
    }

    @Inject(method = "resetMapping()V", at = @At("RETURN"))
    private static void resetMapping(CallbackInfo ci) {
        FULL_SELECTION.clear();

        ALL.values().forEach(keyMapping -> {
            HashMap<String, KeyMapping> group = getOrCreateKeybindMapForKey(keyForMapping(keyMapping));
            group.put(keyMapping.getName(), keyMapping);
        });
    }


    @Inject(method = "<init>(Ljava/lang/String;Lcom/mojang/blaze3d/platform/InputConstants$Type;ILjava/lang/String;)V",
            at = @At(value = "RETURN"))
    private void injectConstructor(String name, InputConstants.Type type, int keyCode, String category, CallbackInfo ci) {
        InputConstants.Key key = type.getOrCreate(keyCode);
        KeyMapping olderMappingSharedName = ALL.get(name); // Older mapping for this mapping name

        // If a keybinding for this name already exists, remove it.
        if(olderMappingSharedName != null) {
            InputConstants.Key olderMappingKey = keyForMapping(olderMappingSharedName);

            if(FULL_SELECTION.containsKey(olderMappingKey)) {
                FULL_SELECTION.get(olderMappingKey)
                        .remove(olderMappingSharedName.getName());
            }
        }

        HashMap<String, KeyMapping> group = getOrCreateKeybindMapForKey(key);
        group.put(name, (KeyMapping) (Object) this);
    }

    @Inject(method = "setKey", at = @At("HEAD"))
    public void updateKey(InputConstants.Key key, CallbackInfo ci) {
        InputConstants.Key oldKey = this.key;

        if(FULL_SELECTION.containsKey(oldKey)) {
            FULL_SELECTION.get(oldKey).remove(this.name);
        }

        KeyMapping oldMap = MAP.get(oldKey);

        if(oldMap != null && this.getName().equals(oldMap.getName())) {
            MAP.remove(oldKey);
        }
    }

    private static InputConstants.Key keyForMapping(KeyMapping mapping) {
        return ((NonConflictingKeyBindingMixin) (Object) mapping).key;
    }
}
