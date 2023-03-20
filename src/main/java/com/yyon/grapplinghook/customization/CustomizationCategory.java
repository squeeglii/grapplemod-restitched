package com.yyon.grapplinghook.customization;

import com.yyon.grapplinghook.content.item.upgrade.BaseUpgradeItem;
import com.yyon.grapplinghook.content.registry.GrappleModMetaRegistry;
import com.yyon.grapplinghook.customization.type.CustomizationProperty;
import net.minecraft.resources.ResourceLocation;

public class CustomizationCategory {

    private final BaseUpgradeItem upgradeItem;
    private final CustomizationProperty<?>[] linkedProperties;

    public CustomizationCategory(BaseUpgradeItem upgradeItem, CustomizationProperty<?>... unlocks) {
        this.upgradeItem = upgradeItem;
        this.linkedProperties = unlocks;
    }


    public String getLocalizationString() {
        String path = this.getIdentifier().toString().replaceAll("[:/\\\\]", ".");
        return "grapple_category.%s.name".formatted(path);
    }

    public ResourceLocation getIdentifier() {
        return GrappleModMetaRegistry.CUSTOMIZATION_CATEGORIES.getKey(this);
    }

    public BaseUpgradeItem getUpgradeItem() {
        return this.upgradeItem;
    }

    public boolean shouldRender() {
        return this.linkedProperties.length > 0;
    }
}
