package com.yyon.grapplinghook.util;

import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class TextUtils {

    /**
     * Used to generate keybinding heads-up tooltips for items.
     * @param label a translation key for this action.
     * @param mappings the keys bound to this action.
     * @return a formatted entry for a keybinding tooltip.
     */
    public static Component keybinding(String label, KeyMapping... mappings) {
        MutableComponent base = Component.empty().withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC);

        if(mappings == null)
            throw new IllegalArgumentException("At least one KeyMapping must be provided.");

        // Add commas to the first mappings
        for(int i = 0; i < mappings.length - 1; i++) {
            KeyMapping mapping = mappings[i];
            base.append(mapping.getTranslatedKeyMessage())
                .append(", ");
        }

        // And handle the last case separately
        KeyMapping finalMapping = mappings[mappings.length - 1];
        base.append(finalMapping.getTranslatedKeyMessage())
            .append(" - ")
            .append(Component.translatable(label));

        return base;
    }

}
