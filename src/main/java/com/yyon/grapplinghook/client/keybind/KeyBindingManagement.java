package com.yyon.grapplinghook.client.keybind;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;

public class KeyBindingManagement {

    public static ArrayList<KeyMapping> keyBindings = new ArrayList<>();

    public static KeyMapping createKeyBinding(KeyMapping k) {
        keyBindings.add(k);
        return k;
    }

    public static KeyMapping key_boththrow = KeyBindingManagement.createKeyBinding(new KeyMapping("key.boththrow.desc", InputConstants.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_2, "key.grapplemod.category"));
    public static KeyMapping key_leftthrow = KeyBindingManagement.createKeyBinding(new KeyMapping("key.leftthrow.desc", InputConstants.UNKNOWN.getValue(), "key.grapplemod.category"));
    public static KeyMapping key_rightthrow = KeyBindingManagement.createKeyBinding(new KeyMapping("key.rightthrow.desc", InputConstants.UNKNOWN.getValue(), "key.grapplemod.category"));
    public static KeyMapping key_motoronoff = KeyBindingManagement.createKeyBinding(new KeyMapping("key.motoronoff.desc", GLFW.GLFW_KEY_LEFT_SHIFT, "key.grapplemod.category"));
    public static KeyMapping key_jumpanddetach = KeyBindingManagement.createKeyBinding(new KeyMapping("key.jumpanddetach.desc", GLFW.GLFW_KEY_SPACE, "key.grapplemod.category"));
    public static KeyMapping key_slow = KeyBindingManagement.createKeyBinding(new KeyMapping("key.slow.desc", GLFW.GLFW_KEY_LEFT_SHIFT, "key.grapplemod.category"));
    public static KeyMapping key_climb = KeyBindingManagement.createKeyBinding(new KeyMapping("key.climb.desc", GLFW.GLFW_KEY_LEFT_SHIFT, "key.grapplemod.category"));
    public static KeyMapping key_climbup = KeyBindingManagement.createKeyBinding(new KeyMapping("key.climbup.desc", InputConstants.UNKNOWN.getValue(), "key.grapplemod.category"));
    public static KeyMapping key_climbdown = KeyBindingManagement.createKeyBinding(new KeyMapping("key.climbdown.desc", InputConstants.UNKNOWN.getValue(), "key.grapplemod.category"));
    public static KeyMapping key_enderlaunch = KeyBindingManagement.createKeyBinding(new KeyMapping("key.enderlaunch.desc", InputConstants.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_1, "key.grapplemod.category"));
    public static KeyMapping key_rocket = KeyBindingManagement.createKeyBinding(new KeyMapping("key.rocket.desc", InputConstants.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_1, "key.grapplemod.category"));
    public static KeyMapping key_slide = KeyBindingManagement.createKeyBinding(new KeyMapping("key.slide.desc", GLFW.GLFW_KEY_LEFT_SHIFT, "key.grapplemod.category"));


    public static void registerAll() {
        for(KeyMapping mapping: KeyBindingManagement.keyBindings) {
            KeyBindingHelper.registerKeyBinding(mapping);
        }
    }

}
