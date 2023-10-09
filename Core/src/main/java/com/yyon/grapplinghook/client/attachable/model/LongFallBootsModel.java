package com.yyon.grapplinghook.client.attachable.model;

import com.google.common.collect.ImmutableList;
import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.util.model.ModelPath;
import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.function.Supplier;

public class LongFallBootsModel<T extends LivingEntity> extends AgeableListModel<T> {

    public static final Supplier<Iterator<String>> LEFT_BOOT_PATH = ModelPath.combine(ModelPath.ROOT_TO_LEFT_LEG, "left_boot");
    public static final Supplier<Iterator<String>> RIGHT_BOOT_PATH = ModelPath.combine(ModelPath.ROOT_TO_RIGHT_LEG, "right_boot");


    protected ModelPart parent;

    protected ModelPart leftBoot;
    protected ModelPart rightBoot;


    public LongFallBootsModel(ModelPart root) {
        this.parent = root;
        this.leftBoot = ModelPath.goTo(root, LEFT_BOOT_PATH.get());
        this.rightBoot = ModelPath.goTo(root, RIGHT_BOOT_PATH.get());
    }

    public static LayerDefinition generateLayer() {
        MeshDefinition mesh = new MeshDefinition();
        CubeDeformation expand = new CubeDeformation(1.0F);

        mesh.getRoot().addOrReplaceChild("left_boot", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, 0.0F, 0.0F, 4.0F, 4.0F, 4.0F, expand), PartPose.ZERO);
        mesh.getRoot().addOrReplaceChild("right_boot", CubeListBuilder.create().texOffs(0, 0).mirror().addBox(0.0F, 0.0F, 0.0F, 4.0F, 4.0F, 4.0F, expand), PartPose.ZERO);

        GrappleMod.LOGGER.info("Generated!");

        return LayerDefinition.create(mesh, 64, 32);
    }

    @NotNull
    @Override
    protected Iterable<ModelPart> headParts() {
        return ImmutableList.of();
    }

    @NotNull
    @Override
    protected Iterable<ModelPart> bodyParts() {
        return ImmutableList.of(this.leftBoot, this.rightBoot);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

    }
}
