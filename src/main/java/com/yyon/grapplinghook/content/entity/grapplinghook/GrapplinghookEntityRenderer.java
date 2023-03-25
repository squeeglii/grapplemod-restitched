package com.yyon.grapplinghook.content.entity.grapplinghook;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.util.Vec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.joml.*;

import java.lang.Math;


/** This file is part of GrappleMod.

    GrappleMod is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    GrappleMod is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with GrappleMod.  If not, see <http://www.gnu.org/licenses/>.
 */

@Environment(EnvType.CLIENT)
public class GrapplinghookEntityRenderer<T extends GrapplinghookEntity> extends EntityRenderer<T> {

	public static final Vector3f X_AXIS = new Vector3f(1, 0, 0);
	public static final Vector3f Y_AXIS = new Vector3f(0, 1, 0);
	public static final Vector3f Z_AXIS = new Vector3f(0, 0, 1);

    private static final ResourceLocation HOOK_TEXTURES = new ResourceLocation("grapplemod", "textures/entity/hook.png");
    private static final ResourceLocation ROPE_TEXTURES = new ResourceLocation("grapplemod", "textures/entity/rope.png");
    private static final RenderType ROPE_RENDER = RenderType.entitySolid(ROPE_TEXTURES);

	private final EntityRendererProvider.Context context;
	private final Item item;

	public GrapplinghookEntityRenderer(EntityRendererProvider.Context context, Item itemIn) {
		super(context);
		this.item = itemIn;
		this.context = context;
	}

    /**
     * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
     * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
     * (Render<T extends Entity>) and this method has signature public void func_76986_a(T entity, double d, double d1,
     * double d2, float f, float f1). But JAD is pre 1.5 so doe
     */
    @Override
    public void render(T hookEntity, float entityYaw, float partialTicks, PoseStack matrix, MultiBufferSource rendertype, int packedLight) {
		if (hookEntity == null || !hookEntity.isAlive()) return;
		
		RopeSegmentHandler ropeHandler = hookEntity.getSegmentHandler();

		if(!(hookEntity.shootingEntity instanceof LivingEntity holder)) return;
		if (!holder.isAlive()) return;
		
		// is right hand?
		int handDirection = (holder.getMainArm() == HumanoidArm.RIGHT ? 1 : -1) * (hookEntity.rightHand ? 1 : -1);
		
		// attack/swing progress
		float completion = holder.getAttackAnim(partialTicks);
		float swingPosition = Mth.sin(Mth.sqrt(completion) * (float) Math.PI);
		
		// get the offset from the center of the head to the hand
		boolean isFirstPerson = this.entityRenderDispatcher.options.getCameraType().isFirstPerson() && holder == Minecraft.getInstance().player;
		Vec handOffset = isFirstPerson
				? this.getFirstPersonHandOffset(holder, handDirection, swingPosition, partialTicks)
				: this.getThirdPersonHandOffset(holder, handDirection, swingPosition, partialTicks);

		// get the hand position
		handOffset.y += holder.getEyeHeight();
		Vec handPosition = handOffset.add(Vec.partialPositionVec(holder, partialTicks));

		this.drawHook(matrix, rendertype, hookEntity, ropeHandler, handPosition, handDirection, packedLight, partialTicks);
		this.drawRope(matrix, rendertype, hookEntity, ropeHandler, handPosition, packedLight, partialTicks);
		super.render(hookEntity, entityYaw, partialTicks, matrix, rendertype, packedLight);
    }

	protected Vec getFirstPersonHandOffset(LivingEntity grappleHookHolder, int handDirection, float swingPos, float partialTicks) {
		// base hand offset (no swing, when facing +Z)
		double d7 = this.entityRenderDispatcher.options.fov().get();
		d7 = d7 / 100.0D;

		Vec handOffset = new Vec(
				(double) handDirection * -0.46D * d7,
				-0.18D * d7,
				0.38D
		);

		// apply swing
		handOffset = handOffset.rotatePitch(-swingPos * 0.7F);
		handOffset = handOffset.rotateYaw(-swingPos * 0.5F);

		// apply looking direction
		handOffset = handOffset.rotatePitch(-Vec.lerp(partialTicks, grappleHookHolder.xRotO, grappleHookHolder.getXRot()) * ((float)Math.PI / 180F));
		return handOffset.rotateYaw(Vec.lerp(partialTicks, grappleHookHolder.yRotO, grappleHookHolder.getYRot()) * ((float)Math.PI / 180F));
	}

	protected Vec getThirdPersonHandOffset(LivingEntity grappleHookHolder, int handDirection, float swingPos, float partialTicks) {
		// base hand offset (no swing, when facing +Z)
		Vec handOffset = new Vec(
				(double) handDirection * -0.36D,
				-0.65D + (grappleHookHolder.isCrouching() ? -0.1875F : 0.0F),
				0.6D
		);

		// apply swing
		handOffset = handOffset.rotatePitch(swingPos * 0.7F);

		// apply body rotation
		return handOffset.rotateYaw(Vec.lerp(partialTicks, grappleHookHolder.yBodyRotO, grappleHookHolder.yBodyRot) * ((float)Math.PI / 180F));
	}

    private Vec getRelativeToEntity(GrapplinghookEntity hookEntity, Vec inVec, float partialTicks) {
    	return inVec.sub(Vec.partialPositionVec(hookEntity, partialTicks));
    }

	public void drawHook(PoseStack matrix, MultiBufferSource renderType, GrapplinghookEntity hookEntity, RopeSegmentHandler ropeHandler, Vec handPosition, int handDirection, int packedLight, float partialTicks) {
		Vec attachDirection = Vec.motionVec(hookEntity).scale(-1);

		if (attachDirection.length() == 0) {

			if (hookEntity.attachDirection != null) {
				attachDirection = hookEntity.attachDirection;

			} else {

				if (ropeHandler == null || ropeHandler.segments.size() <= 2) {
					attachDirection = this.getRelativeToEntity(hookEntity, new Vec(handPosition), partialTicks);

				} else {
					Vec from = ropeHandler.segments.get(1);
					Vec to = Vec.partialPositionVec(hookEntity, partialTicks);
					attachDirection = from.sub(to);
				}
			}
		}

		attachDirection.mutableNormalize();

		if (hookEntity.attached && hookEntity.attachDirection != null)
			attachDirection = hookEntity.attachDirection;

		hookEntity.attachDirection = attachDirection;

		// transformation so hook texture is facing the correct way
		matrix.pushPose();
		matrix.scale(0.5F, 0.5F, 0.5F);

		matrix.mulPose(rotateAxis(-attachDirection.getYaw(), Y_AXIS));
		matrix.mulPose(rotateAxis(attachDirection.getPitch() - 90.0f, X_AXIS));
		matrix.mulPose(rotateAxis(45.0f * handDirection, Y_AXIS));
		matrix.mulPose(rotateAxis(-45.0f, Z_AXIS));

		// draw hook
		ItemStack stack = this.getStackToRender();
		BakedModel bakedModel = context.getItemRenderer().getModel(stack, hookEntity.level(), null, hookEntity.getId());

		context.getItemRenderer().render(stack, ItemDisplayContext.NONE, false, matrix, renderType, packedLight, OverlayTexture.NO_OVERLAY, bakedModel);

		// revert transformation
		matrix.popPose();
	}

	public static Quaternionf rotateAxis(double angleDegrees, Vector3f axis) {
		return new Quaternionf().rotateAxis((float) Math.toRadians(angleDegrees), axis);
	}

	public void drawRope(PoseStack matrix, MultiBufferSource renderType, GrapplinghookEntity hookEntity, RopeSegmentHandler ropeHandler, Vec handPosition, int packedLight, float partialTicks) {
		matrix.pushPose();
		PoseStack.Pose poseEntry = matrix.last();
		Matrix4f poseMatrix = poseEntry.pose();
		Matrix3f normalMatrix = poseEntry.normal();

		// initialize vertexBuffer (used for drawing)
		VertexConsumer vertexBuffer = renderType.getBuffer(ROPE_RENDER);

		// draw rope
		if (ropeHandler == null) {
			// if no segmenthandler, straight line from hand to hook
			Vec finishRelative = this.getRelativeToEntity(hookEntity, new Vec(handPosition), partialTicks);
			this.drawSegment(new Vec(0,0,0), finishRelative, 1.0F, vertexBuffer, poseMatrix, normalMatrix, packedLight);

		} else {
			for (int i = 0; i < ropeHandler.segments.size() - 1; i++) {
				Vec from = ropeHandler.segments.get(i);
				Vec to = ropeHandler.segments.get(i+1);

				if (i == 0)
					from = Vec.partialPositionVec(hookEntity, partialTicks);

				if (i + 2 == ropeHandler.segments.size())
					to = handPosition;

				from = this.getRelativeToEntity(hookEntity, from, partialTicks);
				to = this.getRelativeToEntity(hookEntity, to, partialTicks);

				double taut = 1;
				if (i == ropeHandler.segments.size() - 2) {
					taut = hookEntity.taut;
				}

				this.drawSegment(from, to, taut, vertexBuffer, poseMatrix, normalMatrix, packedLight);
			}
		}

		// draw tip of rope closest to hand
		Vec hook_pos = Vec.partialPositionVec(hookEntity, partialTicks);
		Vec hand_closest = ropeHandler == null || ropeHandler.segments.size() <= 2
				? hook_pos
				: ropeHandler.segments.get(ropeHandler.segments.size() - 2);

		Vec diff = hand_closest.sub(handPosition);
		Vec forward = diff.withMagnitude(1);
		Vec up = forward.cross(new Vec(1, 0, 0));

		if (up.length() == 0)
			up = forward.cross(new Vec(0, 0, 1));

		up.mutableSetMagnitude(0.025);

		Vec side = forward.cross(up);
		side.mutableSetMagnitude(0.025);

		Vec[] corners = new Vec[] {up.scale(-1).add(side.scale(-1)), up.scale(-1).add(side), up.add(side), up.add(side.scale(-1))};
		float[][] uvs = new float[][] {{0, 0.99F}, {0, 1}, {1, 1}, {1, 0.99F}};

		for (int size = 0; size < 4; size++) {
			Vec corner = corners[size];
			Vec normal = corner.normalize(); //.add(forward.normalize().mult(-1)).normalize();
			Vec cornerPos = this.getRelativeToEntity(hookEntity, handPosition, partialTicks).add(corner);
			vertexBuffer.vertex(poseMatrix, (float) cornerPos.x, (float) cornerPos.y, (float) cornerPos.z).color(255, 255, 255, 255).uv(uvs[size][0], uvs[size][1]).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normalMatrix, (float) normal.x, (float) normal.y, (float) normal.z).endVertex();
		}

		matrix.popPose();
	}

    // draw a segment of the rope
    public void drawSegment(Vec start, Vec finish, double taut, VertexConsumer vertexBuffer, Matrix4f poseMatrix, Matrix3f normalMatrix, int packedLight) {
    	if (start.sub(finish).length() < 0.05) return;

        int number_squares = 16;
        if (taut == 1.0F)
			number_squares = 1;

    	Vec diff = finish.sub(start);
        
        Vec forward = diff.withMagnitude(1);
        Vec up = forward.cross(new Vec(1, 0, 0));

        if (up.length() == 0)
			up = forward.cross(new Vec(0, 0, 1));

        up.mutableSetMagnitude(0.025);
        Vec side = forward.cross(up);
        side.mutableSetMagnitude(0.025);
        
        Vec[] corners = new Vec[] {
				up.scale(-1).add(side.scale(-1)),
				up.add(side.scale(-1)),
				up.add(side),
				up.scale(-1).add(side)
		};

        for (int size = 0; size < 4; size++) {
            Vec corner1 = corners[size];
            Vec corner2 = corners[(size + 1) % 4];

        	Vec normal1 = corner1.normalize();
        	Vec normal2 = corner2.normalize();
            
            for (int square_num = 0; square_num < number_squares; square_num++) {
                float squarefrac1 = (float)square_num / (float) number_squares;
                Vec pos1 = start.add(diff.scale(squarefrac1));
                pos1.y += - (1 - taut) * (0.25 - Math.pow((squarefrac1 - 0.5), 2)) * 1.5;
                float squarefrac2 = ((float) square_num+1) / (float) number_squares;
                Vec pos2 = start.add(diff.scale(squarefrac2));
                pos2.y += - (1 - taut) * (0.25 - Math.pow((squarefrac2 - 0.5), 2)) * 1.5;
                
                Vec corner1pos1 = pos1.add(corner1);
                Vec corner2pos1 = pos1.add(corner2);
                Vec corner1pos2 = pos2.add(corner1);
                Vec corner2pos2 = pos2.add(corner2);
            	
                vertexBuffer.vertex(poseMatrix, (float) corner1pos1.x, (float) corner1pos1.y, (float) corner1pos1.z).color(255, 255, 255, 255).uv(0, squarefrac1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normalMatrix, (float) normal1.x, (float) normal1.y, (float) normal1.z).endVertex();
                vertexBuffer.vertex(poseMatrix, (float) corner2pos1.x, (float) corner2pos1.y, (float) corner2pos1.z).color(255, 255, 255, 255).uv(1, squarefrac1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normalMatrix, (float) normal2.x, (float) normal2.y, (float) normal2.z).endVertex();
                vertexBuffer.vertex(poseMatrix, (float) corner2pos2.x, (float) corner2pos2.y, (float) corner2pos2.z).color(255, 255, 255, 255).uv(1, squarefrac2).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normalMatrix, (float) normal2.x, (float) normal2.y, (float) normal2.z).endVertex();
                vertexBuffer.vertex(poseMatrix, (float) corner1pos2.x, (float) corner1pos2.y, (float) corner1pos2.z).color(255, 255, 255, 255).uv(0, squarefrac2).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normalMatrix, (float) normal1.x, (float) normal1.y, (float) normal1.z).endVertex();
            }
        }
        
    }

    @Override
    public boolean shouldRender(T entity, Frustum frustum, double camX, double camY, double camZ) {
		return true;
	}

	public ItemStack getStackToRender() {
		ItemStack stack = new ItemStack(this.item);
		CompoundTag tag = stack.getOrCreateTag();
		tag.putBoolean("hook", true);
		stack.setTag(tag);
        return stack;
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
	@Override
	@NotNull
	public ResourceLocation getTextureLocation(T entity) {
        return HOOK_TEXTURES;
	}
}
