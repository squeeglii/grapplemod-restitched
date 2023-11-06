package com.yyon.grapplinghook.client.attachable.model;

import com.yyon.grapplinghook.GrappleMod;
import net.minecraft.client.model.HumanoidArmorModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.LivingEntity;

public class LongFallBootsModel<T extends LivingEntity> extends HumanoidModel<T> {

    public LongFallBootsModel(ModelPart root) {
        super(root);
    }


    public static MeshDefinition createBodyLayer() {
        CubeDeformation outerArmourDeform = new CubeDeformation(0.5f);
        MeshDefinition meshDefinition = HumanoidModel.createMesh(outerArmourDeform, 0.0f);
        PartDefinition partDefinition = meshDefinition.getRoot();

        partDefinition.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.ZERO);
        partDefinition.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);
        partDefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.ZERO);
        partDefinition.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.ZERO);
        partDefinition.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.ZERO);

        PartDefinition rightLeg = partDefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, outerArmourDeform.extend(-0.1f)), PartPose.offset(-1.9f, 12.0f, 0.0f));
        PartDefinition leftLeg = partDefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 0).mirror().addBox(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, outerArmourDeform.extend(-0.1f)), PartPose.offset(1.9f, 12.0f, 0.0f));

        CubeDeformation bigHeel = new CubeDeformation(0.3f);

        rightLeg.addOrReplaceChild(
                "right_leg_spike",
                CubeListBuilder.create()
                        .texOffs(0, 16)
                        .addBox(0, -1.0f, 3, 1, 7, 3, bigHeel),
                PartPose.offsetAndRotation(-0.7f, 7.0f, -2.3f, 0.50f, 0, 0)
        );
        leftLeg.addOrReplaceChild(
                "left_leg_spike",
                CubeListBuilder.create()
                        .texOffs(0, 16)
                        .addBox(0, -1.0f, 3, 1, 7, 3, bigHeel),
                PartPose.offsetAndRotation(-0.3f, 7.0f, -2.3f, 0.50f, 0f, 0f)
        );

        return meshDefinition;
    }

}
