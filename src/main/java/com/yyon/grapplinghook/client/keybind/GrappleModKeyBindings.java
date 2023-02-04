package com.yyon.grapplinghook.client.keybind;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;
import com.mojang.blaze3d.platform.InputConstants;
import java.util.ArrayList;

public class GrappleModKeyBindings {

    public static ArrayList<KeyMapping> keyBindings = new ArrayList<>();

    public static KeyMapping createKeyBinding(KeyMapping k) {
        keyBindings.add(k);
        return k;
    }

    public static KeyMapping key_boththrow = GrappleModKeyBindings.createKeyBinding(new KeyMapping("key.boththrow.desc", InputConstants.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_2, "key.grapplemod.category"));
    public static KeyMapping key_leftthrow = GrappleModKeyBindings.createKeyBinding(new KeyMapping("key.leftthrow.desc", InputConstants.UNKNOWN.getValue(), "key.grapplemod.category"));
    public static KeyMapping key_rightthrow = GrappleModKeyBindings.createKeyBinding(new KeyMapping("key.rightthrow.desc", InputConstants.UNKNOWN.getValue(), "key.grapplemod.category"));
    public static KeyMapping key_motoronoff = GrappleModKeyBindings.createKeyBinding(new KeyMapping("key.motoronoff.desc", GLFW.GLFW_KEY_LEFT_SHIFT, "key.grapplemod.category"));
    public static KeyMapping key_jumpanddetach = GrappleModKeyBindings.createKeyBinding(new KeyMapping("key.jumpanddetach.desc", GLFW.GLFW_KEY_SPACE, "key.grapplemod.category"));
    public static KeyMapping key_slow = GrappleModKeyBindings.createKeyBinding(new KeyMapping("key.slow.desc", GLFW.GLFW_KEY_LEFT_SHIFT, "key.grapplemod.category"));
    public static KeyMapping key_climb = GrappleModKeyBindings.createKeyBinding(new KeyMapping("key.climb.desc", GLFW.GLFW_KEY_LEFT_SHIFT, "key.grapplemod.category"));
    public static KeyMapping key_climbup = GrappleModKeyBindings.createKeyBinding(new KeyMapping("key.climbup.desc", InputConstants.UNKNOWN.getValue(), "key.grapplemod.category"));
    public static KeyMapping key_climbdown = GrappleModKeyBindings.createKeyBinding(new KeyMapping("key.climbdown.desc", InputConstants.UNKNOWN.getValue(), "key.grapplemod.category"));
    public static KeyMapping key_enderlaunch = GrappleModKeyBindings.createKeyBinding(new KeyMapping("key.enderlaunch.desc", InputConstants.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_1, "key.grapplemod.category"));
    public static KeyMapping key_rocket = GrappleModKeyBindings.createKeyBinding(new KeyMapping("key.rocket.desc", InputConstants.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_1, "key.grapplemod.category"));
    public static KeyMapping key_slide = GrappleModKeyBindings.createKeyBinding(new KeyMapping("key.slide.desc", GLFW.GLFW_KEY_LEFT_SHIFT, "key.grapplemod.category"));


    public static void registerAll() {
        for(KeyMapping mapping: GrappleModKeyBindings.keyBindings) {
            KeyBindingHelper.registerKeyBinding(mapping);
        }
    }

}
