package com.yyon.grapplinghook.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;

public class ClientKey {

    public static ArrayList<KeyMapping> keyBindings = new ArrayList<>();

    public static KeyMapping createKeyBinding(KeyMapping k) {
        keyBindings.add(k);
        return k;
    }

    public static final KeyMapping THROW_HOOKS = ClientKey.createKeyBinding(new KeyMapping("key.boththrow.desc", InputConstants.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_2, "key.grapplemod.category"));
    public static final KeyMapping THROW_LEFT_HOOK = ClientKey.createKeyBinding(new KeyMapping("key.leftthrow.desc", InputConstants.UNKNOWN.getValue(), "key.grapplemod.category"));
    public static final KeyMapping THROW_RIGHT_HOOK = ClientKey.createKeyBinding(new KeyMapping("key.rightthrow.desc", InputConstants.UNKNOWN.getValue(), "key.grapplemod.category"));
    public static final KeyMapping TOGGLE_MOTOR = ClientKey.createKeyBinding(new KeyMapping("key.motoronoff.desc", GLFW.GLFW_KEY_LEFT_SHIFT, "key.grapplemod.category"));
    public static final KeyMapping DETACH = ClientKey.createKeyBinding(new KeyMapping("key.jumpanddetach.desc", GLFW.GLFW_KEY_SPACE, "key.grapplemod.category"));
    public static final KeyMapping DAMPEN_SWING = ClientKey.createKeyBinding(new KeyMapping("key.slow.desc", GLFW.GLFW_KEY_LEFT_SHIFT, "key.grapplemod.category"));
    public static final KeyMapping CLIMB = ClientKey.createKeyBinding(new KeyMapping("key.climb.desc", GLFW.GLFW_KEY_LEFT_SHIFT, "key.grapplemod.category"));
    public static final KeyMapping CLIMB_UP = ClientKey.createKeyBinding(new KeyMapping("key.climbup.desc", InputConstants.UNKNOWN.getValue(), "key.grapplemod.category"));
    public static final KeyMapping CLIMB_DOWN = ClientKey.createKeyBinding(new KeyMapping("key.climbdown.desc", InputConstants.UNKNOWN.getValue(), "key.grapplemod.category"));
    public static final KeyMapping HOOK_ENDER_LAUNCH = ClientKey.createKeyBinding(new KeyMapping("key.enderlaunch.desc", InputConstants.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_1, "key.grapplemod.category"));
    public static final KeyMapping ROCKET = ClientKey.createKeyBinding(new KeyMapping("key.rocket.desc", InputConstants.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_1, "key.grapplemod.category"));
    public static final KeyMapping SLIDE = ClientKey.createKeyBinding(new KeyMapping("key.slide.desc", GLFW.GLFW_KEY_LEFT_SHIFT, "key.grapplemod.category"));


    public static void registerAll() {
        for(KeyMapping mapping: ClientKey.keyBindings) {
            KeyBindingHelper.registerKeyBinding(mapping);
        }
    }

}
