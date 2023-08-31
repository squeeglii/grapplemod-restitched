package com.yyon.grapplinghook.content.item;

import com.yyon.grapplinghook.customization.CustomizationVolume;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public interface ICustomizationAppliable {

    Component getOverwriteMessage();
    SoundEvent getOverwriteSoundEffect();

    void applyCustomizations(ItemStack stack, CustomizationVolume custom);
    CustomizationVolume resetCustomizations(ItemStack stack);
}
