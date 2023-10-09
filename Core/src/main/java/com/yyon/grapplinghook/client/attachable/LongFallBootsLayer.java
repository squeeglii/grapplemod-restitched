package com.yyon.grapplinghook.client.attachable;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.client.attachable.model.LongFallBootsModel;
import com.yyon.grapplinghook.content.registry.GrappleModEntityRenderLayers;
import com.yyon.grapplinghook.content.registry.GrappleModItems;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class LongFallBootsLayer<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {

    public static final ResourceLocation BOOTS_TEXTURE = GrappleMod.id("textures/armor/long_fall_boots.png");

    private final LongFallBootsModel<T> model;

    public LongFallBootsLayer(RenderLayerParent<T, M> renderLayerParent, EntityModelSet modelSet) {
        super(renderLayerParent);
        this.model = new LongFallBootsModel<>(modelSet.bakeLayer(GrappleModEntityRenderLayers.LONG_FALL_BOOTS.getLocation()));
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T livingEntity, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        ItemStack bootsStack = livingEntity.getItemBySlot(EquipmentSlot.FEET);

        if (bootsStack.is(GrappleModItems.LONG_FALL_BOOTS.get())) {
            poseStack.pushPose();
            //poseStack.translate(0.0F, 0.0F, 0.0F);
            this.getParentModel().copyPropertiesTo(this.model);
            this.model.setupAnim(livingEntity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
            VertexConsumer vertexConsumer = ItemRenderer.getArmorFoilBuffer(buffer, RenderType.armorCutoutNoCull(BOOTS_TEXTURE), false, bootsStack.hasFoil());
            this.model.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            poseStack.popPose();
        }
    }

}
