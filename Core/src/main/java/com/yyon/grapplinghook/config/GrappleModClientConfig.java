package com.yyon.grapplinghook.config;

import com.yyon.grapplinghook.config.helper.impl.DefaultValueTracker;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

public class GrappleModClientConfig extends DefaultValueTracker {

    @ConfigEntry.Gui.CollapsibleObject
    @ConfigEntry.Gui.Tooltip
    public Camera camera = new Camera();
    public static class Camera {
        @ConfigEntry.Gui.Tooltip
        public float wallrun_camera_tilt_degrees = 10;
        @ConfigEntry.Gui.Tooltip
        public float wallrun_camera_animation_s = 0.5f;
    }

    @ConfigEntry.Gui.CollapsibleObject
    @ConfigEntry.Gui.Tooltip
    public Sounds sounds = new Sounds();
    public static class Sounds {
        @ConfigEntry.Gui.Tooltip
        public double wallrun_sound_effect_time_s = 0.35;
        @ConfigEntry.Gui.Tooltip
        public float wallrun_sound_volume = 1.0F;
        @ConfigEntry.Gui.Tooltip
        public float doublejump_sound_volume = 1.0F;
        @ConfigEntry.Gui.Tooltip
        public float slide_sound_volume = 1.0F;
        @ConfigEntry.Gui.Tooltip
        public float wallrunjump_sound_volume = 1.0F;
        @ConfigEntry.Gui.Tooltip
        public float rocket_sound_volume = 1.0F;
        @ConfigEntry.Gui.Tooltip
        public float enderstaff_sound_volume = 1.0F;
    }

}
