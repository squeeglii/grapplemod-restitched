package com.yyon.grapplinghook.client.keybind;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;

import java.util.*;

public class NonConflictKeyMapping {

    private static final Map<InputConstants.Key, Set<KeyMapping>> EXTRA_KEY_MAPPINGS = new HashMap<>();

    public static void addToOverflowMappings(KeyMapping mapping) {
        Set<KeyMapping> overflowBindings = EXTRA_KEY_MAPPINGS.computeIfAbsent(mapping.key, k -> new LinkedHashSet<>());
        overflowBindings.add(mapping);
    }

    public static void resetAllOverflowMappings() {
        EXTRA_KEY_MAPPINGS.clear();
    }


    public static Map<InputConstants.Key, Set<KeyMapping>> getExtraKeyMappings() {
        return Collections.unmodifiableMap(EXTRA_KEY_MAPPINGS);
    }
}
