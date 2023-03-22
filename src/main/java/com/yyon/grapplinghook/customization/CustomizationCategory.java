package com.yyon.grapplinghook.customization;

import com.yyon.grapplinghook.content.item.upgrade.BaseUpgradeItem;
import com.yyon.grapplinghook.content.registry.GrappleModMetaRegistry;
import com.yyon.grapplinghook.customization.type.CustomizationProperty;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class CustomizationCategory {

    private final BaseUpgradeItem upgradeItem;
    private final CustomizationProperty<?>[] linkedProperties;

    public CustomizationCategory(BaseUpgradeItem upgradeItem, CustomizationProperty<?>... unlocks) {
        this.upgradeItem = upgradeItem;
        this.linkedProperties = unlocks;
    }


    public String getLocalization(String suffix) {
        String path = this.getIdentifier().toString().replaceAll("[:/\\\\]", ".");
        boolean includeConnectingDot = suffix != null && suffix.length() > 0 && !suffix.startsWith(".");
        return "grapple_category.%s%s".formatted(path, includeConnectingDot ? "." : "");
    }

    public ResourceLocation getIdentifier() {
        return GrappleModMetaRegistry.CUSTOMIZATION_CATEGORIES.getKey(this);
    }

    public BaseUpgradeItem getUpgradeItem() {
        return this.upgradeItem;
    }

    public Component getName() {
        return Component.translatable(this.getLocalization("name"));
    }

    public Component getDescription() {
        return Component.translatable(this.getLocalization("desc"));
    }

    public boolean shouldRender() {
        return this.linkedProperties.length > 0;
    }
}
