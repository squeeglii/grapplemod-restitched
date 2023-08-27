package com.yyon.grapplinghook.physics;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

import java.util.Set;

public class PhysicsFramePredicate {

    public static final PhysicsFramePredicate ANY = new PhysicsFramePredicate();

    private Set<ResourceLocation> controllerTypes;
    private MinMaxBounds.Doubles speed;

    private PhysicsFramePredicate() {
        this.controllerTypes = null;
        this.speed = MinMaxBounds.Doubles.ANY;
    }


    public boolean matches(PlayerPhysicsFrame frame) {
        if(this.controllerTypes != null) {
            if (!this.controllerTypes.contains(frame.getPhysicsControllerType()))
                return false;
        }

        if(!this.speed.matches(frame.getSpeed()))
            return false;

        return true;
    }

    public static PhysicsFramePredicate fromJson(JsonElement json) {
        if (json == null || json.isJsonNull())
            return ANY;

        PhysicsFramePredicate predicate = new PhysicsFramePredicate();
        JsonObject root = GsonHelper.convertToJsonObject(json, "physics");

        predicate.speed = MinMaxBounds.Doubles.fromJson(root.get("speed"));

        JsonArray jsonArray = GsonHelper.getAsJsonArray(root, "controller_types", null);
        if (jsonArray != null) {
            ImmutableSet.Builder<ResourceLocation> builder = ImmutableSet.builder();

            for (JsonElement jsonElement : jsonArray) {
                String entry = GsonHelper.convertToString(jsonElement, "controller_type");
                ResourceLocation controllerTypeId = new ResourceLocation(entry);
                builder.add(controllerTypeId);
            }

            predicate.controllerTypes = builder.build();
        }

        return predicate;
    }

    public JsonElement toJson() {
        if(this == ANY)
            return JsonNull.INSTANCE;

        JsonObject root = new JsonObject();

        root.add("speed", this.speed.serializeToJson());

        if (this.controllerTypes != null) {
            JsonArray controllerTypesJson = new JsonArray();

            this.controllerTypes.stream()
                    .map(ResourceLocation::toString)
                    .forEach(controllerTypesJson::add);

            root.add("controller_types", controllerTypesJson);
        }

        return root;
    }

}
