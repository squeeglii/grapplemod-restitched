package com.yyon.grapplinghook.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.function.Function;

public class ClientKey {

    public static ArrayList<KeyMapping> keyBindings = new ArrayList<>();

    public static KeyMapping createKeyBinding(KeyMapping k) {
        keyBindings.add(k);
        return k;
    }

    public static WithFallback createKeyBindingWithFallback(KeyMapping k, Function<Options, KeyMapping> vanillaFallback) {
        keyBindings.add(k);
        return new WithFallback(k, vanillaFallback);
    }

    public static final WithFallback THROW_HOOKS = ClientKey.createKeyBindingWithFallback(new KeyMapping("key.boththrow.desc", GLFW.GLFW_KEY_UNKNOWN, "key.grapplemod.category"), options -> options.keyUse);
    public static final WithFallback TOGGLE_MOTOR = ClientKey.createKeyBindingWithFallback(new KeyMapping("key.motoronoff.desc", GLFW.GLFW_KEY_UNKNOWN, "key.grapplemod.category"), options -> options.keyShift);
    public static final WithFallback DETACH = ClientKey.createKeyBindingWithFallback(new KeyMapping("key.jumpanddetach.desc", GLFW.GLFW_KEY_UNKNOWN, "key.grapplemod.category"), options -> options.keyJump);
    public static final WithFallback DAMPEN_SWING = ClientKey.createKeyBindingWithFallback(new KeyMapping("key.slow.desc", GLFW.GLFW_KEY_UNKNOWN, "key.grapplemod.category"), options -> options.keyShift);
    public static final WithFallback CLIMB = ClientKey.createKeyBindingWithFallback(new KeyMapping("key.climb.desc", GLFW.GLFW_KEY_UNKNOWN, "key.grapplemod.category"), options -> options.keyShift);
    public static final WithFallback HOOK_ENDER_LAUNCH = ClientKey.createKeyBindingWithFallback(new KeyMapping("key.enderlaunch.desc", GLFW.GLFW_KEY_UNKNOWN, "key.grapplemod.category"), options -> options.keyAttack);
    public static final WithFallback ROCKET = ClientKey.createKeyBindingWithFallback(new KeyMapping("key.rocket.desc", GLFW.GLFW_KEY_UNKNOWN, "key.grapplemod.category"), options -> options.keyAttack);
    public static final WithFallback SLIDE = ClientKey.createKeyBindingWithFallback(new KeyMapping("key.slide.desc", GLFW.GLFW_KEY_UNKNOWN, "key.grapplemod.category"), options -> options.keyShift);

    public static final KeyMapping THROW_OFF_HOOK = ClientKey.createKeyBinding(new KeyMapping("key.off_throw.desc", InputConstants.UNKNOWN.getValue(), "key.grapplemod.category"));
    public static final KeyMapping THROW_MAIN_HOOK = ClientKey.createKeyBinding(new KeyMapping("key.main_throw.desc", InputConstants.UNKNOWN.getValue(), "key.grapplemod.category"));
    public static final KeyMapping CLIMB_UP = ClientKey.createKeyBinding(new KeyMapping("key.climbup.desc", InputConstants.UNKNOWN.getValue(), "key.grapplemod.category"));
    public static final KeyMapping CLIMB_DOWN = ClientKey.createKeyBinding(new KeyMapping("key.climbdown.desc", InputConstants.UNKNOWN.getValue(), "key.grapplemod.category"));


    public static void registerAll() {
        for(KeyMapping mapping: ClientKey.keyBindings) {
            KeyBindingHelper.registerKeyBinding(mapping);
        }
    }

    /**
     * A lot of this mod's custom keybinds clash with vanilla. For 1.21.1 - 1.21.8, the mod will use
     * a vanilla keybind for each key unless an alternative is bound.
     */
    public record WithFallback(KeyMapping modMapping, Function<Options, KeyMapping> vanillaFallback) {

        public KeyMapping get() {
            return this.modMapping.isUnbound()
                    ? this.vanillaFallback.apply(Minecraft.getInstance().options)
                    : this.modMapping;
        }

    }

}
