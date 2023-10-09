package com.yyon.grapplinghook.content.item.type;

import com.yyon.grapplinghook.customization.CustomizationVolume;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;

public interface ICustomizationApplicable {

    Component getOverwriteMessage();
    SoundEvent getOverwriteSoundEffect();

    void applyCustomizations(ItemStack stack, CustomizationVolume custom);
    CustomizationVolume resetCustomizations(ItemStack stack);

    // Should it be easy to overwrite a given objects customizations (i.e, a grappling hook) or should
    // it be a bit harder (and less prone to accidents) to overwrite it.
    boolean shouldAllowQuickOverwrite();

}
