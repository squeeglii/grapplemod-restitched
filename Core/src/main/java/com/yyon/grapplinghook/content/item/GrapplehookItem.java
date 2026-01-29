package com.yyon.grapplinghook.content.item;

import com.yyon.grapplinghook.api.GrappleModServerEvents;
import com.yyon.grapplinghook.client.GrappleModClient;
import com.yyon.grapplinghook.client.ClientKey;
import com.yyon.grapplinghook.content.entity.grapplinghook.GrapplinghookEntity;
import com.yyon.grapplinghook.content.item.type.ICustomizationApplicable;
import com.yyon.grapplinghook.content.item.type.IDropHandling;
import com.yyon.grapplinghook.content.item.type.IGlobalKeyObserver;
import com.yyon.grapplinghook.content.registry.internal.ModItemComponents;
import com.yyon.grapplinghook.customization.data.HookCustomization;
import com.yyon.grapplinghook.customization.data.TemplateAuthor;
import com.yyon.grapplinghook.customization.type.AttachmentProperty;
import com.yyon.grapplinghook.customization.type.CustomizationProperty;
import com.yyon.grapplinghook.network.NetworkManager;
import com.yyon.grapplinghook.network.clientbound.DetachSingleHookS2CPayload;
import com.yyon.grapplinghook.network.clientbound.GrappleDetachS2CPayload;
import com.yyon.grapplinghook.network.serverbound.KeypressC2SPayload;
import com.yyon.grapplinghook.physics.ServerHookEntityTracker;
import com.yyon.grapplinghook.util.GrappleModUtils;
import com.yyon.grapplinghook.util.TextUtils;
import com.yyon.grapplinghook.util.Vec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;

import static com.yyon.grapplinghook.content.registry.CustomizationProperties.*;


/*
 * This file is part of GrappleMod.

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

public class GrapplehookItem extends Item implements IGlobalKeyObserver, IDropHandling, ICustomizationApplicable {

	public static final int DURABILITY = 500; // as of 1.21.1, this should be changed with data components rather than the config.

	public static HashMap<Entity, GrapplinghookEntity> grapplehookEntitiesLeft = new HashMap<>();
	public static HashMap<Entity, GrapplinghookEntity> grapplehookEntitiesRight = new HashMap<>();

	//todo: integrate deploy state.
	//todo: ensure deploy state & customizations are properly saved & loaded.
	//todo: left/right hand --> main & off hand.

	public GrapplehookItem() {
		super(
				new Item.Properties()
						.stacksTo(1)
						.durability(DURABILITY)
						.component(ModItemComponents.CUSTOMIZABLE, new HookCustomization())
		);
	}


	@Override
	public boolean isValidRepairItem(ItemStack stack, ItemStack repair) {
        if (repair != null && repair.getItem().equals(Items.LEATHER)) return true;
        return super.isValidRepairItem(stack, repair);
	}

	@Override
	public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		return true;
	}

	// previously: onBlockStartBreak
	@Override
	public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity miningEntity) {
		return true;
	}

	@Override
	public boolean canAttackBlock(BlockState p_195938_1_, Level p_195938_2_, BlockPos p_195938_3_, Player p_195938_4_) {
		return false;
	}

	@Override
	public void onCustomKeyDown(ItemStack stack, Player player, IGlobalKeyObserver.Keys key, boolean isMainHand) {
		if (player.level().isClientSide) {
			if (key == IGlobalKeyObserver.Keys.LAUNCHER) {
				if (this.getCustomizationsOrDefault(stack).get(ENDER_STAFF_ATTACHED.get()))
					GrappleModClient.get().launchPlayer(player);

			} else if (key == IGlobalKeyObserver.Keys.THROWLEFT || key == IGlobalKeyObserver.Keys.THROWRIGHT || key == IGlobalKeyObserver.Keys.THROWBOTH) {
				NetworkManager.packetToServer(new KeypressC2SPayload(key, true));

			} else if (key == IGlobalKeyObserver.Keys.ROCKET) {
				HookCustomization custom = this.getCustomizationsOrDefault(stack);
				if (custom.get(ROCKET_ATTACHED.get()))
					GrappleModClient.get().startRocket(player, custom);
			}

			return;
		}

		HookCustomization custom = this.getCustomizationsOrDefault(stack);

		boolean isEitherSingleHandThrowKeyDown = key == IGlobalKeyObserver.Keys.THROWLEFT || key == IGlobalKeyObserver.Keys.THROWRIGHT;

		if (key == IGlobalKeyObserver.Keys.THROWBOTH || (!custom.get(DOUBLE_HOOK_ATTACHED.get()) && isEitherSingleHandThrowKeyDown)) {
			throwBoth(stack, player.level(), player, isMainHand);
			return;
		}

		if(!isEitherSingleHandThrowKeyDown) return;

		boolean isLeft = key == Keys.THROWLEFT;

		GrapplinghookEntity hook = isLeft
				? getHookEntityLeft(player)
				: getHookEntityRight(player);

		if (hook != null) {
			if(isLeft) detachLeft(player);
			else detachRight(player);
			return;
		}

		stack.hurtAndBreak(1, player, GrappleModUtils.currentHand(isMainHand));
		if (stack.getCount() <= 0) return;

		boolean threw = isLeft
				? throwLeft(stack, player.level(), player)
				: throwRight(stack, player.level(), player, isMainHand);

		if (!threw) return;

		player.level().playSound(null, player.position().x, player.position().y, player.position().z, SoundEvents.ARROW_SHOOT, SoundSource.NEUTRAL, 1.0F, 1.0F / (player.getRandom().nextFloat() * 0.4F + 1.2F) + 2.0F * 0.5F);
	}
	
	@Override
	public void onCustomKeyUp(ItemStack stack, Player player, IGlobalKeyObserver.Keys key, boolean ismainhand) {
		if (player.level().isClientSide) {
			if (key == IGlobalKeyObserver.Keys.THROWLEFT || key == IGlobalKeyObserver.Keys.THROWRIGHT || key == IGlobalKeyObserver.Keys.THROWBOTH) {
				NetworkManager.packetToServer(new KeypressC2SPayload(key, false));
			}

			return;
		}

		HookCustomization custom = this.getCustomizationsOrDefault(stack);

		if (custom.get(DETACH_HOOK_ON_KEY_UP.get())) {
			GrapplinghookEntity hookLeft = getHookEntityLeft(player);
			GrapplinghookEntity hookRight = getHookEntityRight(player);

			if (key == IGlobalKeyObserver.Keys.THROWBOTH) {
				detachBoth(player);
			} else if (key == IGlobalKeyObserver.Keys.THROWLEFT) {
				if (hookLeft != null) detachLeft(player);
			} else if (key == IGlobalKeyObserver.Keys.THROWRIGHT) {
				if (hookRight != null) detachRight(player);
			}
		}
	}

	@Override
	public void onDroppedByPlayer(ItemStack stack, Player player) {
		if(!stack.has(ModItemComponents.FORCE_HOOK_DISPLAY))
			return;

		int id = player.getId();
		GrappleModUtils.sendToCorrectClient(new GrappleDetachS2CPayload(id), id, player.level());

		if (grapplehookEntitiesLeft.containsKey(player)) {
			GrapplinghookEntity hookLeft = grapplehookEntitiesLeft.get(player);
			setHookEntityLeft(player, null);
			if (hookLeft != null) {
				hookLeft.removeServer();
			}
		}

		if (grapplehookEntitiesRight.containsKey(player)) {
			GrapplinghookEntity hookRight = grapplehookEntitiesRight.get(player);
			setHookEntityLeft(player, null);
			if (hookRight != null) {
				hookRight.removeServer();
			}
		}
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
		HookCustomization custom = this.getCustomizationsOrDefault(stack);
		Options options = Minecraft.getInstance().options;

		if(stack.has(ModItemComponents.AUTHORED)) {
			TemplateAuthor metadata = stack.get(ModItemComponents.AUTHORED);
			Component author = metadata.author()
					.copy()
					.withStyle(ChatFormatting.GRAY, ChatFormatting.UNDERLINE);

			tooltipComponents.add(Component.empty()
					.withStyle(ChatFormatting.DARK_GRAY)
					.append(Component.translatable("grapple_tooltip.template.author"))
					.append(Component.literal(" "))
					.append(author)
			);

			tooltipComponents.add(Component.literal(" "));
		}

		if (Screen.hasShiftDown()) {
			tooltipComponents.add(Component.literal(""));
			tooltipComponents.add(Component.translatable("grappletooltip.controls.title").withStyle(
					ChatFormatting.GRAY, ChatFormatting.BOLD, ChatFormatting.UNDERLINE
			));

			if (custom.get(DOUBLE_HOOK_ATTACHED.get())) {
				if (!custom.get(DETACH_HOOK_ON_KEY_UP.get())) {
					tooltipComponents.add(TextUtils.keybinding("grappletooltip.throw_double_both.desc", ClientKey.THROW_HOOKS));
					tooltipComponents.add(TextUtils.keybinding("grappletooltip.throw_double_left.desc", ClientKey.THROW_LEFT_HOOK));
					tooltipComponents.add(TextUtils.keybinding("grappletooltip.throw_double_right.desc", ClientKey.THROW_RIGHT_HOOK));
				} else {
					tooltipComponents.add(TextUtils.keybinding("grappletooltip.throw_double_both_hold.desc", ClientKey.THROW_HOOKS));
					tooltipComponents.add(TextUtils.keybinding("grappletooltip.throw_double_left_hold.desc", ClientKey.THROW_LEFT_HOOK));
					tooltipComponents.add(TextUtils.keybinding("grappletooltip.throw_double_right_hold.desc", ClientKey.THROW_RIGHT_HOOK));
				}

			} else {
				if (!custom.get(DETACH_HOOK_ON_KEY_UP.get())) {
					tooltipComponents.add(TextUtils.keybinding("grappletooltip.throw.desc", ClientKey.THROW_HOOKS));
					tooltipComponents.add(TextUtils.keybinding("grappletooltip.release.desc", ClientKey.THROW_HOOKS));
				} else {
					tooltipComponents.add(TextUtils.keybinding("grappletooltip.throw_hold.desc", ClientKey.THROW_HOOKS));
				}
			}



			tooltipComponents.add(TextUtils.keybinding("grappletooltip.swing.desc",
					options.keyUp, options.keyLeft, options.keyDown, options.keyRight
			));

			tooltipComponents.add(TextUtils.keybinding("grappletooltip.jump.desc", ClientKey.DETACH));
			tooltipComponents.add(TextUtils.keybinding("grappletooltip.slow.desc", ClientKey.DAMPEN_SWING));

			tooltipComponents.add(Component.empty().withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC)
					.append(ClientKey.CLIMB.getTranslatedKeyMessage()).append("+")
					.append(options.keyUp.getTranslatedKeyMessage())
					.append(" / ")
					.append(ClientKey.CLIMB_UP.getTranslatedKeyMessage())
					.append(" - ").append(Component.translatable("grappletooltip.climbup.desc"))
			);

			tooltipComponents.add(Component.empty().withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC)
					.append(ClientKey.CLIMB.getTranslatedKeyMessage()).append("+")
					.append(options.keyDown.getTranslatedKeyMessage())
					.append(" / ")
					.append(ClientKey.CLIMB_DOWN.getTranslatedKeyMessage())
					.append(" - ").append(Component.translatable("grappletooltip.climbdown.desc"))
			);

			if (custom.get(ENDER_STAFF_ATTACHED.get())) {
				tooltipComponents.add(TextUtils.keybinding("grappletooltip.enderlaunch.desc", ClientKey.HOOK_ENDER_LAUNCH));
			}

			if (custom.get(ROCKET_ATTACHED.get())) {
				tooltipComponents.add(TextUtils.keybinding("grappletooltip.rocket.desc", ClientKey.ROCKET));
			}

			if (custom.get(MOTOR_ATTACHED.get())) {
				Component text = switch (custom.get(MOTOR_ACTIVATION.get())) {
					case WHEN_CROUCHING -> TextUtils.keybinding("grappletooltip.motoron.desc", ClientKey.TOGGLE_MOTOR);
					case WHEN_NOT_CROUCHING -> TextUtils.keybinding("grappletooltip.motoroff.desc", ClientKey.TOGGLE_MOTOR);
					default -> null;
				};

				if(text != null)
					tooltipComponents.add(text.copy().withStyle(ChatFormatting.DARK_GRAY));
			}

			if (custom.get(HOOK_REEL_IN_ON_SNEAK.get())) {
				tooltipComponents.add(TextUtils.keybinding("grappletooltip.reelin.desc", options.keyShift));
			}

			return;
		}

		if (Screen.hasControlDown()) {
			tooltipComponents.add(Component.translatable("grappletooltip.properties.title").withStyle(
					ChatFormatting.GRAY, ChatFormatting.BOLD, ChatFormatting.UNDERLINE
			));
			tooltipComponents.add(Component.literal(""));

			for(CustomizationProperty<?> property: custom.getPropertiesPresent()) {
				Component hintText = property.getDisplay().getModificationHint(custom);
				if(hintText == null) continue;

				Component formatted = hintText.copy().withStyle(ChatFormatting.DARK_GRAY);

				tooltipComponents.add(formatted);
			}

			return;
		}

		HashMap<ResourceLocation, Component> attachmentTexts = new HashMap<>();

		custom.getPropertiesPresent().stream()
				.filter(p -> p instanceof AttachmentProperty)
				.map(p -> (AttachmentProperty) p)
				.forEach(attachment -> {
					boolean isAttachmentShadowed = AttachmentProperty.isShadowed(custom, attachment);

					// Some attachments are hidden by others (i.e, smart motor hides motor)
					// Skip any names where its shadower is present and enabled.
					if(isAttachmentShadowed) return;

					Component formattedName = attachment.getDisplayName()
							.copy()
							.withStyle(ChatFormatting.DARK_GRAY);

					attachmentTexts.put(attachment.getIdentifier(), formattedName);
				});

		if(!attachmentTexts.isEmpty()) {
			tooltipComponents.add(Component.translatable("grappletooltip.attachments.title").withStyle(
					ChatFormatting.GRAY, ChatFormatting.BOLD, ChatFormatting.UNDERLINE
			));

			tooltipComponents.add(Component.literal(""));
			tooltipComponents.addAll(attachmentTexts.values());
			tooltipComponents.add(Component.literal(""));
		}

		tooltipComponents.add(Component.translatable("grapple_tooltip.controls.hint").withStyle(
				ChatFormatting.ITALIC, ChatFormatting.GRAY
		));
		tooltipComponents.add(Component.translatable("grapple_tooltip.configuration.hint").withStyle(
				ChatFormatting.ITALIC, ChatFormatting.GRAY
		));
	}

	@NotNull
	@Override
	public Component getName(ItemStack stack) {
		if(!stack.has(ModItemComponents.AUTHORED))
			return super.getName(stack);

		TemplateAuthor metadata = stack.get(ModItemComponents.AUTHORED);

		return metadata.isNameEmpty()
				? super.getName(stack)
				: metadata.templateDisplayName();
	}

	@Override
	public boolean shouldAllowQuickOverwrite() {
		return true;
	}

	@Override
	public Component getOverwriteMessage() {
		return Component.translatable("feedback.grapplemod.modifier.applied_configuration");
	}

	@Override
	public SoundEvent getOverwriteSoundEffect() {
		return SoundEvents.VILLAGER_WORK_TOOLSMITH;
	}

	/**
	 * Applies customizations and removes the template metadata.
	 * To retain the metadata, call #applyTemplateMetadata(...) after calling this.
	 */
	@Override
	public void applyCustomizations(ItemStack stack, HookCustomization custom) {
		stack.remove(ModItemComponents.AUTHORED);
		stack.set(ModItemComponents.CUSTOMIZABLE, custom);
	}

	public Vec calculateThrowDirectionVector(Vec angleVec) {
		float velx = -Mth.sin((float) angleVec.getYaw() * 0.017453292F) * Mth.cos((float) angleVec.getPitch() * 0.017453292F);
		float vely = -Mth.sin((float) angleVec.getPitch() * 0.017453292F);
		float velz = Mth.cos((float) angleVec.getYaw() * 0.017453292F) * Mth.cos((float) angleVec.getPitch() * 0.017453292F);

		return new Vec(velx, vely, velz);
	}

	public double calculateExtraSpeedFromAngles(LivingEntity holder, Vec directionVec) {
		return Math.max(0.0D, Vec.motionVec(holder).distanceAlong(directionVec));
	}

	public void throwBoth(ItemStack stack, Level worldIn, LivingEntity entityLiving, boolean isMainHand) {
		if (this.hasHookEntity(entityLiving)) {
			this.detachBoth(entityLiving);
			return;
		}

		stack.hurtAndBreak(1, (ServerPlayer) entityLiving, GrappleModUtils.currentHand(isMainHand));
		if (stack.getCount() <= 0)
			return;

		HookCustomization custom = this.getCustomizationsOrDefault(stack);
		double angle = this.getSingleHookAngle(entityLiving, custom);
		boolean shouldThrowBothHands = custom.get(DOUBLE_HOOK_ATTACHED.get()) && angle != 0;

		if (shouldThrowBothHands)
            this.throwLeft(stack, worldIn, entityLiving);

		this.throwRight(stack, worldIn, entityLiving, isMainHand);

		entityLiving.level().playSound(null, entityLiving.position().x, entityLiving.position().y, entityLiving.position().z, SoundEvents.ARROW_SHOOT, SoundSource.NEUTRAL, 1.0F, 1.0F / (worldIn.random.nextFloat() * 0.4F + 1.2F) + 2.0F * 0.5F);
	}

	public boolean throwLeft(ItemStack stack, Level worldIn, LivingEntity entityLiving) {
    	HookCustomization custom = this.getCustomizationsOrDefault(stack);

		double angle = this.getDoubleHookAngle(entityLiving, custom);
		double verticalAngle = this.getSingleHookAngle(entityLiving, custom);

		Vec initialAngle = Vec.fromAngles(Math.toRadians(-angle), Math.toRadians(verticalAngle));
		Vec anglevec = applyHolderRotation(initialAngle, entityLiving);
	  	Vec direction = this.calculateThrowDirectionVector(anglevec);
	  	double extraSpeed = this.calculateExtraSpeedFromAngles(entityLiving, direction);

		GrapplinghookEntity hookEntity = this.createGrapplehookEntity(stack, worldIn, entityLiving, false, true);
        hookEntity.shoot(direction, hookEntity.getSpeed() + extraSpeed, 0.0F);
        
		worldIn.addFreshEntity(hookEntity);
		setHookEntityLeft(entityLiving, hookEntity);
		return true;
	}
	
	public boolean throwRight(ItemStack stack, Level worldIn, LivingEntity entityLiving, boolean righthand) {
	    HookCustomization custom = this.getCustomizationsOrDefault(stack);
		double angle = this.getDoubleHookAngle(entityLiving, custom);
  		double verticalAngle = this.getSingleHookAngle(entityLiving, custom);

		boolean isNotDouble = !custom.get(DOUBLE_HOOK_ATTACHED.get()) || angle == 0;

		Vec initialAngle = isNotDouble
				? new Vec(0,0,1).rotatePitch(Math.toRadians(verticalAngle))
				: Vec.fromAngles(Math.toRadians(angle), Math.toRadians(verticalAngle));

		Vec anglevec = applyHolderRotation(initialAngle, entityLiving);
		Vec direction = this.calculateThrowDirectionVector(anglevec);
		double extraSpeed = this.calculateExtraSpeedFromAngles(entityLiving, direction);

		GrapplinghookEntity hookEntity = isNotDouble
				? this.createGrapplehookEntity(stack, worldIn, entityLiving, righthand, false)
				: this.createGrapplehookEntity(stack, worldIn, entityLiving, true, true);

		hookEntity.shoot(direction, hookEntity.getSpeed() + extraSpeed, 0.0F);

		worldIn.addFreshEntity(hookEntity);
		setHookEntityRight(entityLiving, hookEntity);

		return true;
	}
	
	public void detachBoth(LivingEntity thrower) {
		GrapplinghookEntity hookLeft = getHookEntityLeft(thrower);
		GrapplinghookEntity hookRight = getHookEntityRight(thrower);

		setHookEntityLeft(thrower, null);
		setHookEntityRight(thrower, null);
		
		if (hookLeft != null) hookLeft.removeServer();
		if (hookRight != null) hookRight.removeServer();

		int id = thrower.getId();
		GrappleModServerEvents.HOOK_RETRACT.invoker().onHookRetracted(thrower);
		GrappleModUtils.sendToCorrectClient(new GrappleDetachS2CPayload(id), thrower.getId(), thrower.level());
	}
	
	public void detachLeft(LivingEntity thrower) {

		GrapplinghookEntity hookLeft = getHookEntityLeft(thrower);
		setHookEntityLeft(thrower, null);
		
		if (hookLeft != null) hookLeft.removeServer();

		int id = thrower.getId();
		GrappleModServerEvents.HOOK_RETRACT.invoker().onHookRetracted(thrower);
		
		// remove controller if hook is attached
		if (getHookEntityRight(thrower) == null) {
			GrappleModUtils.sendToCorrectClient(new GrappleDetachS2CPayload(id), id, thrower.level());
		} else {
			GrappleModUtils.sendToCorrectClient(new DetachSingleHookS2CPayload(id, hookLeft.getId()), id, thrower.level());
		}
	}
	
	public void detachRight(LivingEntity thrower) {
		GrapplinghookEntity hookRight = getHookEntityRight(thrower);
		setHookEntityRight(thrower, null);
		
		if (hookRight != null) hookRight.removeServer();
		
		int id = thrower.getId();

		GrappleModServerEvents.HOOK_RETRACT.invoker().onHookRetracted(thrower);
		// remove controller if hook is attached
		if (getHookEntityLeft(thrower) == null) {
			GrappleModUtils.sendToCorrectClient(new GrappleDetachS2CPayload(id), id, thrower.level());
		} else {
			GrappleModUtils.sendToCorrectClient(new DetachSingleHookS2CPayload(id, hookRight.getId()), id, thrower.level());
		}
	}
	
	public GrapplinghookEntity createGrapplehookEntity(ItemStack stack, Level worldIn, LivingEntity entityLiving, boolean righthand, boolean isdouble) {
		GrapplinghookEntity hookEntity = new GrapplinghookEntity(worldIn, entityLiving, righthand, this.getCustomizationsOrDefault(stack), isdouble);
		ServerHookEntityTracker.addGrappleEntity(entityLiving, hookEntity);
		return hookEntity;
	}

	public void applyTemplateMetadata(ItemStack stack, TemplateAuthor template) {
		if(template != null) {
			stack.set(ModItemComponents.AUTHORED, template);
		} else {
			stack.remove(ModItemComponents.AUTHORED);
		}
	}

	public static Vec applyHolderRotation(Vec angleVec, LivingEntity holder) {
		Vec newVec = angleVec.rotatePitch(Math.toRadians(-holder.getViewXRot(1.0F)));
		return newVec.rotateYaw(Math.toRadians(holder.getViewYRot(1.0F)));
	}


	public void setHookEntityLeft(Entity entity, GrapplinghookEntity hookEntity) {
		GrapplehookItem.grapplehookEntitiesLeft.put(entity, hookEntity);
	}
	public void setHookEntityRight(Entity entity, GrapplinghookEntity hookEntity) {
		GrapplehookItem.grapplehookEntitiesRight.put(entity, hookEntity);
	}


	public boolean hasHookEntity(Entity entity) {
		GrapplinghookEntity hookLeft = getHookEntityLeft(entity);
		GrapplinghookEntity hookRight = getHookEntityRight(entity);
		return (hookLeft != null) || (hookRight != null);
	}

	public GrapplinghookEntity getHookEntityLeft(Entity entity) {
		if (!GrapplehookItem.grapplehookEntitiesLeft.containsKey(entity)) return null;

		GrapplinghookEntity hookEntity = GrapplehookItem.grapplehookEntitiesLeft.get(entity);
		if (hookEntity != null && hookEntity.isAlive())
			return hookEntity;

		return null;
	}

	public GrapplinghookEntity getHookEntityRight(Entity entity) {
		if (!GrapplehookItem.grapplehookEntitiesRight.containsKey(entity)) return null;

		GrapplinghookEntity hookEntity = GrapplehookItem.grapplehookEntitiesRight.get(entity);
		if (hookEntity != null && hookEntity.isAlive())
			return hookEntity;

		return null;
	}

	public double getSingleHookAngle(LivingEntity entity, HookCustomization custom) {
		return entity.isCrouching()
				? custom.get(HOOK_THROW_ANGLE_ON_SNEAK.get())
				: custom.get(HOOK_THROW_ANGLE.get());
	}

	public double getDoubleHookAngle(LivingEntity entity, HookCustomization custom) {
		return entity.isCrouching()
				? custom.get(DOUBLE_HOOK_ANGLE_ON_SNEAK.get())
				: custom.get(DOUBLE_HOOK_ANGLE.get());
	}

	/**
	 * If a hook doesn't have customizations, it should just use the default set without
	 * saving it to the item itself.
	 */
	public HookCustomization getCustomizationsOrDefault(ItemStack stack) {
		return stack.getOrDefault(ModItemComponents.CUSTOMIZABLE, new HookCustomization());
	}

	public boolean shouldDisplayAsHookOnly(ItemStack stack) {
		return stack.has(ModItemComponents.FORCE_HOOK_DISPLAY);
	}
}
