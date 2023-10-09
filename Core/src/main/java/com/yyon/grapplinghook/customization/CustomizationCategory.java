package com.yyon.grapplinghook.customization;

import com.yyon.grapplinghook.content.item.upgrade.BaseUpgradeItem;
import com.yyon.grapplinghook.content.registry.GrappleModMetaRegistry;
import com.yyon.grapplinghook.customization.type.CustomizationProperty;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class CustomizationCategory {

    private final BaseUpgradeItem upgradeItem;
    private final List<CustomizationProperty<?>> linkedProperties;

    public CustomizationCategory(BaseUpgradeItem upgradeItem, CustomizationProperty<?>... unlocks) {
        this.upgradeItem = upgradeItem;
        this.linkedProperties = List.of(unlocks);
    }


    public String getLocalization() {
        return this.getLocalization(null);
    }

    public String getLocalization(String suffix) {
        String path = this.getIdentifier().toLanguageKey();

        boolean includeConnectingDot = suffix != null && !suffix.isEmpty() && !suffix.startsWith(".");

        return "grapple_category.%s%s%s".formatted(
                path,
                includeConnectingDot ? "." : "",
                suffix == null ? "" : suffix
        );
    }

    public ResourceLocation getIdentifier() {
        return GrappleModMetaRegistry.CUSTOMIZATION_CATEGORIES.getKey(this);
    }

    public BaseUpgradeItem getUpgradeItem() {
        return this.upgradeItem;
    }

    public MutableComponent getName() {
        return Component.translatable(this.getLocalization());
    }

    public MutableComponent getDescription() {
        return Component.translatable(this.getLocalization("desc"));
    }

    public MutableComponent getEmbedContent() {
        return Component.empty()
                .append(this.getName().withStyle(ChatFormatting.AQUA).withStyle(ChatFormatting.UNDERLINE))
                .append("\n\n")
                .append(this.getDescription().withStyle(ChatFormatting.GRAY));
    }

    public MutableComponent getEmbed() {
        Component hoverText = this.getEmbedContent();

        HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText);

        MutableComponent base = this.getName().copy();
        Style style = Style.EMPTY
                .withUnderlined(true)
                .withItalic(true)
                .withColor(ChatFormatting.AQUA)
                .withHoverEvent(hoverEvent);

        base.setStyle(style);
        return base;
    }

    public Set<CustomizationProperty<?>> getLinkedProperties() {
        return new LinkedHashSet<>(this.linkedProperties);
    }

    public boolean shouldRender() {
        return !this.linkedProperties.isEmpty();
    }
}
