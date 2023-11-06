package com.yyon.grapplinghook.util.model;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.LivingEntity;

public interface LayerBuilder<T extends LivingEntity, M extends HumanoidModel<T>, A extends M> {

     RenderLayer<T, M> build(
            RenderLayerParent<T, M> parent,
            A model,
            EntityRendererProvider.Context context
     );

}
