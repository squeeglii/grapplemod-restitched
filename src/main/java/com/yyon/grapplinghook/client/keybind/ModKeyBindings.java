package com.yyon.grapplinghook.client.keybind;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;

public class ModKeyBindings {

    public static ArrayList<KeyMapping> keyBindings = new ArrayList<>();

    public static KeyMapping createKeyBinding(KeyMapping k) {
        keyBindings.add(k);
        return k;
    }

    public static KeyMapping key_boththrow = ModKeyBindings.createKeyBinding(new KeyMapping("key.boththrow.desc", InputConstants.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_2, "key.grapplemod.category"));
    public static KeyMapping key_leftthrow = ModKeyBindings.createKeyBinding(new KeyMapping("key.leftthrow.desc", InputConstants.UNKNOWN.getValue(), "key.grapplemod.category"));
    public static KeyMapping key_rightthrow = ModKeyBindings.createKeyBinding(new KeyMapping("key.rightthrow.desc", InputConstants.UNKNOWN.getValue(), "key.grapplemod.category"));
    public static KeyMapping key_motoronoff = ModKeyBindings.createKeyBinding(new KeyMapping("key.motoronoff.desc", GLFW.GLFW_KEY_LEFT_SHIFT, "key.grapplemod.category"));
    public static KeyMapping key_jumpanddetach = ModKeyBindings.createKeyBinding(new KeyMapping("key.jumpanddetach.desc", GLFW.GLFW_KEY_SPACE, "key.grapplemod.category"));
    public static KeyMapping key_slow = ModKeyBindings.createKeyBinding(new KeyMapping("key.slow.desc", GLFW.GLFW_KEY_LEFT_SHIFT, "key.grapplemod.category"));
    public static KeyMapping key_climb = ModKeyBindings.createKeyBinding(new KeyMapping("key.climb.desc", GLFW.GLFW_KEY_LEFT_SHIFT, "key.grapplemod.category"));
    public static KeyMapping key_climbup = ModKeyBindings.createKeyBinding(new KeyMapping("key.climbup.desc", InputConstants.UNKNOWN.getValue(), "key.grapplemod.category"));
    public static KeyMapping key_climbdown = ModKeyBindings.createKeyBinding(new KeyMapping("key.climbdown.desc", InputConstants.UNKNOWN.getValue(), "key.grapplemod.category"));
    public static KeyMapping key_enderlaunch = ModKeyBindings.createKeyBinding(new KeyMapping("key.enderlaunch.desc", InputConstants.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_1, "key.grapplemod.category"));
    public static KeyMapping key_rocket = ModKeyBindings.createKeyBinding(new KeyMapping("key.rocket.desc", InputConstants.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_1, "key.grapplemod.category"));
    public static KeyMapping key_slide = ModKeyBindings.createKeyBinding(new KeyMapping("key.slide.desc", GLFW.GLFW_KEY_LEFT_SHIFT, "key.grapplemod.category"));


    public static void registerAll() {
        for(KeyMapping mapping: ModKeyBindings.keyBindings) {
            KeyBindingHelper.registerKeyBinding(mapping);
        }
    }

}
