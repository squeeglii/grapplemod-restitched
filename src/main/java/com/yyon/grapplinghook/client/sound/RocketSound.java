package com.yyon.grapplinghook.client.sound;

import com.yyon.grapplinghook.config.GrappleModLegacyConfig;
import com.yyon.grapplinghook.physics.context.GrapplingHookPhysicsController;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;

public final class RocketSound extends AbstractTickableSoundInstance {
    private final GrapplingHookPhysicsController controller;
    private final float changeSpeed;

    private boolean isStopping = false;


    public RocketSound(GrapplingHookPhysicsController controller, SoundEvent soundEvent, SoundSource soundSource) {
        super(soundEvent, soundSource, RandomSource.create());
        this.looping = true;
        this.controller = controller;

        this.changeSpeed = GrappleModLegacyConfig.getClientConf().sounds.rocket_sound_volume * 0.5F * 0.2F;
        this.volume = this.changeSpeed;
        this.delay = 0;
        this.attenuation = SoundInstance.Attenuation.NONE;
        this.relative = false;
    }

    @Override
    public void tick() {
        if (!controller.rocketKeyDown || !controller.isControllerActive)
            this.isStopping = true;

        float targetVolume = this.isStopping
                ? 0
                : (float) controller.getRocketProgression() * GrappleModLegacyConfig.getClientConf().sounds.rocket_sound_volume * 0.5F;

        float diff = Math.abs(targetVolume - this.volume);
        this.volume = diff > changeSpeed
                ? this.volume + changeSpeed * (this.volume > targetVolume ? -1 : 1)
                : targetVolume;

        if (this.volume == 0 && this.isStopping)
            this.stop();

        this.x = controller.entity.getX();
        this.y = controller.entity.getY();
        this.z = controller.entity.getZ();
    }
}
