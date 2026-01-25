package com.yyon.grapplinghook.content.advancement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.yyon.grapplinghook.physics.PlayerPhysicsFrame;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;

import java.util.List;
import java.util.Optional;

public record PhysicsFramePredicate(Optional<List<ResourceLocation>> controllerTypes, Optional<MinMaxBounds.Doubles> speed, Optional<Boolean> isUsingRocket) {

    public static final Codec<PhysicsFramePredicate> CODEC = RecordCodecBuilder.create((instance) ->
            instance
                .group(
                        Codec.list(ResourceLocation.CODEC).optionalFieldOf("controller_types").forGetter(PhysicsFramePredicate::controllerTypes),
                        MinMaxBounds.Doubles.CODEC.optionalFieldOf("speed").forGetter(PhysicsFramePredicate::speed),
                        Codec.BOOL.optionalFieldOf("is_using_rocket").forGetter(PhysicsFramePredicate::isUsingRocket)

                )
                .apply(instance, PhysicsFramePredicate::new)
    );

    public static final PhysicsFramePredicate ANY = new PhysicsFramePredicate(
            Optional.empty(),
            Optional.of(MinMaxBounds.Doubles.ANY),
            Optional.empty()
    );


    public boolean matches(PlayerPhysicsFrame frame) {
        if(this.controllerTypes.isPresent()) {
            ResourceLocation frameType = frame.getPhysicsControllerType();

            if (!this.controllerTypes.get().contains(frameType))
                return false;
        }

        if(this.speed.isPresent() && !this.speed.get().matches(frame.getSpeed()))
            return false;

        if(this.isUsingRocket.isPresent() && this.isUsingRocket.get() != frame.isUsingRocket())
            return false;

        return true;
    }



}
