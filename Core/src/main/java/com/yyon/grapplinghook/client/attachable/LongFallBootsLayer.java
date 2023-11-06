package com.yyon.grapplinghook.client.attachable;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.content.registry.GrappleModItems;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;

public class LongFallBootsLayer<T extends LivingEntity, M extends HumanoidModel<T>, A extends M> extends RenderLayer<T, M> {

    public static final ResourceLocation BOOTS_TEXTURE = GrappleMod.id("textures/armor/long_fall_boots.png");

    private final A model;
    private final TextureAtlas armorTrimAtlas;

    public LongFallBootsLayer(RenderLayerParent<T, M> renderLayerParent, A model, ModelManager modelManager) {
        super(renderLayerParent);
        this.model = model;
        this.armorTrimAtlas = modelManager.getAtlas(Sheets.ARMOR_TRIMS_SHEET);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T livingEntity, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        ItemStack itemStack = livingEntity.getItemBySlot(EquipmentSlot.FEET);

        if (!itemStack.is(GrappleModItems.LONG_FALL_BOOTS.get())) return;

        this.getParentModel().copyPropertiesTo(this.model);

        this.renderModel(poseStack, buffer, packedLight, model, 1.0f, 1.0f, 1.0f);

        ArmorTrim.getTrim(livingEntity.level().registryAccess(), itemStack, true).ifPresent(armorTrim -> {
            this.renderTrim(poseStack, buffer, packedLight, armorTrim, model);
        });

        if (itemStack.hasFoil()) {
            this.renderGlint(poseStack, buffer, packedLight, model);
        }
    }


    // Borrowed from Minecraft's HumanoidArmorLayer :)
    private void renderModel(PoseStack poseStack, MultiBufferSource buffer, int packedLight, A model, float red, float green, float blue) {
        RenderType armourRenderType = RenderType.armorCutoutNoCull(BOOTS_TEXTURE);
        VertexConsumer vertexConsumer = buffer.getBuffer(armourRenderType);
        model.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, red, green, blue, 1.0f);
    }


    private void renderTrim(PoseStack poseStack, MultiBufferSource buffer, int packedLight, ArmorTrim trim, A model) {
        TextureAtlasSprite bootsSprite = this.armorTrimAtlas.getSprite(BOOTS_TEXTURE);
        boolean isDecal = trim.pattern().value().decal();
        RenderType trimsSheet = Sheets.armorTrimsSheet(isDecal);
        VertexConsumer trimBuffer = buffer.getBuffer(trimsSheet);
        VertexConsumer vertexConsumer = bootsSprite.wrap(trimBuffer);

        model.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, 1.0f, 1.0f, 1.0f, 1.0f);
    }

    private void renderGlint(PoseStack poseStack, MultiBufferSource buffer, int packedLight, A model) {
        VertexConsumer glintBuf = buffer.getBuffer(RenderType.armorEntityGlint());

        model.renderToBuffer(poseStack, glintBuf, packedLight, OverlayTexture.NO_OVERLAY, 1.0f, 1.0f, 1.0f, 1.0f);
    }



}
