package com.yyon.grapplinghook.content.advancement;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.yyon.grapplinghook.physics.PlayerPhysicsFrame;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.GsonHelper;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public record PhysicsFramePredicate(Optional<List<ResourceLocation>> controllerTypes, Optional<MinMaxBounds.Doubles> speed, Optional<Boolean> isUsingRocket) {

    public static final Codec<PhysicsFramePredicate> CODEC = RecordCodecBuilder.create((instance) ->
            instance
                .group(
                        ExtraCodecs.strictOptionalField(Codec.list(ResourceLocation.CODEC), "controller_types").forGetter(PhysicsFramePredicate::controllerTypes),
                        ExtraCodecs.strictOptionalField(MinMaxBounds.Doubles.CODEC, "speed").forGetter(PhysicsFramePredicate::speed),
                        ExtraCodecs.strictOptionalField(Codec.BOOL, "is_using_rocket").forGetter(PhysicsFramePredicate::isUsingRocket)

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
