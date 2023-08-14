package com.yyon.grapplinghook.content.item;

import com.yyon.grapplinghook.client.GrappleModClient;
import com.yyon.grapplinghook.client.keybind.KeyBindingManagement;
import com.yyon.grapplinghook.client.keybind.MinecraftKey;
import com.yyon.grapplinghook.config.GrappleModLegacyConfig;
import com.yyon.grapplinghook.content.entity.grapplinghook.GrapplinghookEntity;
import com.yyon.grapplinghook.content.item.type.DroppableItem;
import com.yyon.grapplinghook.content.item.type.KeypressItem;
import com.yyon.grapplinghook.customization.template.GrapplingHookTemplate;
import com.yyon.grapplinghook.customization.type.AttachmentProperty;
import com.yyon.grapplinghook.customization.type.BooleanProperty;
import com.yyon.grapplinghook.customization.type.CustomizationProperty;
import com.yyon.grapplinghook.network.NetworkManager;
import com.yyon.grapplinghook.network.clientbound.DetachSingleHookMessage;
import com.yyon.grapplinghook.network.clientbound.GrappleDetachMessage;
import com.yyon.grapplinghook.network.serverbound.KeypressMessage;
import com.yyon.grapplinghook.physics.PhysicsContextTracker;
import com.yyon.grapplinghook.customization.CustomizationVolume;
import com.yyon.grapplinghook.util.GrappleModUtils;
import com.yyon.grapplinghook.util.Vec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static com.yyon.grapplinghook.content.registry.GrappleModCustomizationProperties.*;


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

public class GrapplehookItem extends Item implements KeypressItem, DroppableItem {

	public static final String NBT_HOOK_CUSTOMIZATIONS = "custom";
	public static final String NBT_HOOK_TEMPLATE = "hook_template";
	public static final String NBT_TEMPLATE_DISPLAY_NAME = "display_name";

	public static HashMap<Entity, GrapplinghookEntity> grapplehookEntitiesLeft = new HashMap<>();
	public static HashMap<Entity, GrapplinghookEntity> grapplehookEntitiesRight = new HashMap<>();


	public GrapplehookItem() {
		super(new Item.Properties().stacksTo(1).durability(GrappleModLegacyConfig.getConf().grapplinghook.other.default_durability));
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
	public void onCustomKeyDown(ItemStack stack, Player player, KeypressItem.Keys key, boolean ismainhand) {
		if (player.level().isClientSide) {
			if (key == KeypressItem.Keys.LAUNCHER) {
				if (this.getCustomization(stack).get(ENDER_STAFF_ATTACHED.get()))
					GrappleModClient.get().launchPlayer(player);

			} else if (key == KeypressItem.Keys.THROWLEFT || key == KeypressItem.Keys.THROWRIGHT || key == KeypressItem.Keys.THROWBOTH) {
				NetworkManager.packetToServer(new KeypressMessage(key, true));

			} else if (key == KeypressItem.Keys.ROCKET) {
				CustomizationVolume custom = this.getCustomization(stack);
				if (custom.get(ROCKET_ATTACHED.get()))
					GrappleModClient.get().startRocket(player, custom);
			}

			return;
		}

		CustomizationVolume custom = this.getCustomization(stack);

		boolean isEitherSingleHandThrowKeyDown = key == KeypressItem.Keys.THROWLEFT || key == KeypressItem.Keys.THROWRIGHT;

		if (key == KeypressItem.Keys.THROWBOTH || (!custom.get(DOUBLE_HOOK_ATTACHED.get()) && isEitherSingleHandThrowKeyDown)) {
			throwBoth(stack, player.level(), player, ismainhand);
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

		stack.hurtAndBreak(1, (ServerPlayer) player, (p) -> {});
		if (stack.getCount() <= 0) return;

		boolean threw = isLeft
				? throwLeft(stack, player.level(), player)
				: throwRight(stack, player.level(), player, ismainhand);

		if (!threw) return;

		player.level().playSound(null, player.position().x, player.position().y, player.position().z, SoundEvents.ARROW_SHOOT, SoundSource.NEUTRAL, 1.0F, 1.0F / (player.getRandom().nextFloat() * 0.4F + 1.2F) + 2.0F * 0.5F);
	}
	
	@Override
	public void onCustomKeyUp(ItemStack stack, Player player, KeypressItem.Keys key, boolean ismainhand) {
		if (player.level().isClientSide) {
			if (key == KeypressItem.Keys.THROWLEFT || key == KeypressItem.Keys.THROWRIGHT || key == KeypressItem.Keys.THROWBOTH) {
				NetworkManager.packetToServer(new KeypressMessage(key, false));
			}

		} else {
	    	CustomizationVolume custom = this.getCustomization(stack);
	    	
	    	if (custom.get(DETACH_HOOK_ON_KEY_UP.get())) {
	    		GrapplinghookEntity hookLeft = getHookEntityLeft(player);
	    		GrapplinghookEntity hookRight = getHookEntityRight(player);
	    		
				if (key == KeypressItem.Keys.THROWBOTH) {
					detachBoth(player);
				} else if (key == KeypressItem.Keys.THROWLEFT) {
		    		if (hookLeft != null) detachLeft(player);
				} else if (key == KeypressItem.Keys.THROWRIGHT) {
		    		if (hookRight != null) detachRight(player);
				}
	    	}
		}
	}

	@Override
	public void onDroppedByPlayer(ItemStack item, Player player) {
		int id = player.getId();
		GrappleModUtils.sendToCorrectClient(new GrappleDetachMessage(id), id, player.level());

		if (!player.level().isClientSide) {
			PhysicsContextTracker.attached.remove(id);
		}

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
	public void appendHoverText(ItemStack stack, Level world, List<Component> list, TooltipFlag par4) {
		CustomizationVolume custom = getCustomization(stack);

		if (Screen.hasShiftDown()) {
			list.add(Component.translatable("grappletooltip.controls.title").withStyle(
					ChatFormatting.GRAY, ChatFormatting.BOLD, ChatFormatting.UNDERLINE
			));
			list.add(Component.literal(""));

			if (!custom.get(DETACH_HOOK_ON_KEY_UP.get())) {
				list.add(Component.literal(KeyBindingManagement.key_boththrow.getTranslatedKeyMessage().getString() + " " + Component.translatable("grappletooltip.throw.desc").getString()).withStyle(ChatFormatting.DARK_GRAY));
				list.add(Component.literal(KeyBindingManagement.key_boththrow.getTranslatedKeyMessage().getString() + " " + Component.translatable("grappletooltip.release.desc").getString()).withStyle(ChatFormatting.DARK_GRAY));
				list.add(Component.translatable("grappletooltip.double.desc").append(KeyBindingManagement.key_boththrow.getTranslatedKeyMessage()).append(" ").append(Component.translatable("grappletooltip.releaseandthrow.desc")).withStyle(ChatFormatting.DARK_GRAY));

			} else {
				list.add(Component.literal(KeyBindingManagement.key_boththrow.getTranslatedKeyMessage().getString() + " " + Component.translatable("grappletooltip.throwhold.desc").getString()).withStyle(ChatFormatting.DARK_GRAY));
			}

			list.add(Component.literal(GrappleModClient.get().getKeyname(MinecraftKey.keyBindForward) + ", " +
					GrappleModClient.get().getKeyname(MinecraftKey.keyBindLeft) + ", " +
					GrappleModClient.get().getKeyname(MinecraftKey.keyBindBack) + ", " +
					GrappleModClient.get().getKeyname(MinecraftKey.keyBindRight) +
					" " + Component.translatable("grappletooltip.swing.desc").getString()).withStyle(ChatFormatting.DARK_GRAY));
			list.add(Component.literal(KeyBindingManagement.key_jumpanddetach.getTranslatedKeyMessage().getString() + " " + Component.translatable("grappletooltip.jump.desc").getString()).withStyle(ChatFormatting.DARK_GRAY));
			list.add(Component.literal(KeyBindingManagement.key_slow.getTranslatedKeyMessage().getString() + " " + Component.translatable("grappletooltip.slow.desc").getString()).withStyle(ChatFormatting.DARK_GRAY));
			list.add(Component.literal(KeyBindingManagement.key_climb.getTranslatedKeyMessage().getString() + " + " + GrappleModClient.get().getKeyname(MinecraftKey.keyBindForward) + " / " +
					KeyBindingManagement.key_climbup.getTranslatedKeyMessage().getString() +
					" " + Component.translatable("grappletooltip.climbup.desc").getString()).withStyle(ChatFormatting.DARK_GRAY));
			list.add(Component.literal(KeyBindingManagement.key_climb.getTranslatedKeyMessage().getString() + " + " + GrappleModClient.get().getKeyname(MinecraftKey.keyBindBack) + " / " +
					KeyBindingManagement.key_climbdown.getTranslatedKeyMessage().getString() +
					" " + Component.translatable("grappletooltip.climbdown.desc").getString()).withStyle(ChatFormatting.DARK_GRAY));

			if (custom.get(ENDER_STAFF_ATTACHED.get())) {
				list.add(Component.literal(KeyBindingManagement.key_enderlaunch.getTranslatedKeyMessage().getString() + " " + Component.translatable("grappletooltip.enderlaunch.desc").getString()).withStyle(ChatFormatting.DARK_GRAY));
			}

			if (custom.get(ROCKET_ATTACHED.get())) {
				list.add(Component.literal(KeyBindingManagement.key_rocket.getTranslatedKeyMessage().getString() + " " + Component.translatable("grappletooltip.rocket.desc").getString()).withStyle(ChatFormatting.DARK_GRAY));
			}

			if (custom.get(MOTOR_ATTACHED.get())) {

				Component text = switch (custom.get(MOTOR_ACTIVATION.get())) {
					case WHEN_CROUCHING -> Component.literal(KeyBindingManagement.key_motoronoff.getTranslatedKeyMessage().getString() + " " + Component.translatable("grappletooltip.motoron.desc").getString());
					case WHEN_NOT_CROUCHING -> Component.literal(KeyBindingManagement.key_motoronoff.getTranslatedKeyMessage().getString() + " " + Component.translatable("grappletooltip.motoroff.desc").getString());
					default -> null;
				};

				if(text != null)
					list.add(text.copy().withStyle(ChatFormatting.DARK_GRAY));
			}

			if (custom.get(DOUBLE_HOOK_ATTACHED.get())) {
				if (!custom.get(DETACH_HOOK_ON_KEY_UP.get())) {
					list.add(Component.literal(KeyBindingManagement.key_leftthrow.getTranslatedKeyMessage().getString() + " " + Component.translatable("grappletooltip.throwleft.desc").getString()).withStyle(ChatFormatting.DARK_GRAY));
					list.add(Component.literal(KeyBindingManagement.key_rightthrow.getTranslatedKeyMessage().getString() + " " + Component.translatable("grappletooltip.throwright.desc").getString()).withStyle(ChatFormatting.DARK_GRAY));
				} else {
					list.add(Component.literal(KeyBindingManagement.key_leftthrow.getTranslatedKeyMessage().getString() + " " + Component.translatable("grappletooltip.throwlefthold.desc").getString()).withStyle(ChatFormatting.DARK_GRAY));
					list.add(Component.literal(KeyBindingManagement.key_rightthrow.getTranslatedKeyMessage().getString() + " " + Component.translatable("grappletooltip.throwrighthold.desc").getString()).withStyle(ChatFormatting.DARK_GRAY));
				}

			} else {
				list.add(Component.literal(KeyBindingManagement.key_rightthrow.getTranslatedKeyMessage().getString() + " " + Component.translatable("grappletooltip.throwalt.desc").getString()).withStyle(ChatFormatting.DARK_GRAY));
			}

			if (custom.get(HOOK_REEL_IN_ON_SNEAK.get())) {
				list.add(Component.literal(GrappleModClient.get().getKeyname(MinecraftKey.keyBindSneak) + " " + Component.translatable("grappletooltip.reelin.desc").getString()).withStyle(ChatFormatting.DARK_GRAY));
			}

			return;
		}

		if (Screen.hasControlDown()) {
			list.add(Component.translatable("grappletooltip.properties.title").withStyle(
					ChatFormatting.GRAY, ChatFormatting.BOLD, ChatFormatting.UNDERLINE
			));
			list.add(Component.literal(""));

			for(CustomizationProperty<?> property: custom.getPropertiesPresent()) {
				Component hintText = property.getDisplay().getModificationHint(custom);
				if(hintText == null) continue;

				Component formatted = hintText.copy().withStyle(ChatFormatting.DARK_GRAY);

				list.add(formatted);
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
			list.add(Component.translatable("grappletooltip.attachments.title").withStyle(
					ChatFormatting.GRAY, ChatFormatting.BOLD, ChatFormatting.UNDERLINE
			));

			list.add(Component.literal(""));
			list.addAll(attachmentTexts.values());
			list.add(Component.literal(""));
		}

		list.add(Component.translatable("grappletooltip.shiftcontrols.desc").withStyle(
				ChatFormatting.ITALIC, ChatFormatting.GRAY
		));
		list.add(Component.translatable("grappletooltip.controlconfiguration.desc").withStyle(
				ChatFormatting.ITALIC, ChatFormatting.GRAY
		));
	}

	@NotNull
	@Override
	public Component getName(ItemStack stack) {
		Component templateDisplayName = this.getTemplateDisplayName(stack);

		return templateDisplayName == null
				? super.getName(stack)
				: templateDisplayName;
	}

	public Component getTemplateDisplayName(ItemStack stack) {
		CompoundTag templateDisplayTag = stack.getTagElement(NBT_HOOK_TEMPLATE);

		if(templateDisplayTag == null)
			return null;

		String nameJson = templateDisplayTag.getString(NBT_TEMPLATE_DISPLAY_NAME);

		if(nameJson.isEmpty())
			return null;

		try {
            return Component.Serializer.fromJson(nameJson);
		} catch (Exception exception) {
			return null;
		}
	}

	public boolean hasHookEntity(Entity entity) {
		GrapplinghookEntity hookLeft = getHookEntityLeft(entity);
		GrapplinghookEntity hookRight = getHookEntityRight(entity);
		return (hookLeft != null) || (hookRight != null);
	}

	public void setHookEntityLeft(Entity entity, GrapplinghookEntity hookEntity) {
		GrapplehookItem.grapplehookEntitiesLeft.put(entity, hookEntity);
	}
	public void setHookEntityRight(Entity entity, GrapplinghookEntity hookEntity) {
		GrapplehookItem.grapplehookEntitiesRight.put(entity, hookEntity);
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

	public void throwBoth(ItemStack stack, Level worldIn, LivingEntity entityLiving, boolean rightHand) {
		if (this.hasHookEntity(entityLiving)) {
			detachBoth(entityLiving);
    		return;
		}

		stack.hurtAndBreak(1, (ServerPlayer) entityLiving, (p) -> {});
		if (stack.getCount() <= 0) return;

    	CustomizationVolume custom = this.getCustomization(stack);
  		double angle = this.getSingleHookAngle(entityLiving, custom);

	    boolean shouldThrowLeft = !custom.get(DOUBLE_HOOK_ATTACHED.get()) || angle == 0;

    	if (!shouldThrowLeft) {
    		this.throwLeft(stack, worldIn, entityLiving);
    	}

		this.throwRight(stack, worldIn, entityLiving, rightHand);

		entityLiving.level().playSound(null, entityLiving.position().x, entityLiving.position().y, entityLiving.position().z, SoundEvents.ARROW_SHOOT, SoundSource.NEUTRAL, 1.0F, 1.0F / (worldIn.random.nextFloat() * 0.4F + 1.2F) + 2.0F * 0.5F);
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


	
	public boolean throwLeft(ItemStack stack, Level worldIn, LivingEntity entityLiving) {
    	CustomizationVolume custom = this.getCustomization(stack);

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
	    CustomizationVolume custom = this.getCustomization(stack);
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
	
	public void detachBoth(LivingEntity entityLiving) {
		GrapplinghookEntity hookLeft = getHookEntityLeft(entityLiving);
		GrapplinghookEntity hookRight = getHookEntityRight(entityLiving);

		setHookEntityLeft(entityLiving, null);
		setHookEntityRight(entityLiving, null);
		
		if (hookLeft != null) hookLeft.removeServer();
		if (hookRight != null) hookRight.removeServer();

		int id = entityLiving.getId();
		GrappleModUtils.sendToCorrectClient(new GrappleDetachMessage(id), entityLiving.getId(), entityLiving.level());

		PhysicsContextTracker.attached.remove(id);
	}
	
	public void detachLeft(LivingEntity entityLiving) {
		GrapplinghookEntity hookLeft = getHookEntityLeft(entityLiving);
		setHookEntityLeft(entityLiving, null);
		
		if (hookLeft != null) hookLeft.removeServer();

		int id = entityLiving.getId();
		
		// remove controller if hook is attached
		if (getHookEntityRight(entityLiving) == null) {
			GrappleModUtils.sendToCorrectClient(new GrappleDetachMessage(id), id, entityLiving.level());
		} else {
			GrappleModUtils.sendToCorrectClient(new DetachSingleHookMessage(id, hookLeft.getId()), id, entityLiving.level());
		}

		PhysicsContextTracker.attached.remove(id);
	}
	
	public void detachRight(LivingEntity entityLiving) {
		GrapplinghookEntity hookRight = getHookEntityRight(entityLiving);
		setHookEntityRight(entityLiving, null);
		
		if (hookRight != null) hookRight.removeServer();
		
		int id = entityLiving.getId();
		
		// remove controller if hook is attached
		if (getHookEntityLeft(entityLiving) == null) {
			GrappleModUtils.sendToCorrectClient(new GrappleDetachMessage(id), id, entityLiving.level());
		} else {
			GrappleModUtils.sendToCorrectClient(new DetachSingleHookMessage(id, hookRight.getId()), id, entityLiving.level());
		}

		PhysicsContextTracker.attached.remove(id);
	}
	
    public double getSingleHookAngle(LivingEntity entity, CustomizationVolume custom) {
		return entity.isCrouching()
				? custom.get(HOOK_THROW_ANGLE_ON_SNEAK.get())
				: custom.get(HOOK_THROW_ANGLE.get());
    }

	public double getDoubleHookAngle(LivingEntity entity, CustomizationVolume custom) {
		return entity.isCrouching()
				? custom.get(DOUBLE_HOOK_ANGLE_ON_SNEAK.get())
				: custom.get(DOUBLE_HOOK_ANGLE.get());
	}
	
	public GrapplinghookEntity createGrapplehookEntity(ItemStack stack, Level worldIn, LivingEntity entityLiving, boolean righthand, boolean isdouble) {
		GrapplinghookEntity hookEntity = new GrapplinghookEntity(worldIn, entityLiving, righthand, this.getCustomization(stack), isdouble);
		PhysicsContextTracker.addGrapplehookEntity(entityLiving.getId(), hookEntity);
		return hookEntity;
	}
    
    public CustomizationVolume getCustomization(ItemStack itemstack) {
    	CompoundTag tag = itemstack.getOrCreateTag();
    	
    	if (!tag.contains(NBT_HOOK_CUSTOMIZATIONS)) {
			return this.resetCustomizations(itemstack);
    	}

		CustomizationVolume custom = new CustomizationVolume();
		custom.loadFromNBT(tag.getCompound(NBT_HOOK_CUSTOMIZATIONS));
		return custom;
    }

	private CustomizationVolume resetCustomizations(ItemStack stack) {
		CustomizationVolume custom = this.getDefaultCustomization();
		this.applyCustomizations(stack, custom);

		return custom;
	}

	public void applyCustomizations(ItemStack stack, CustomizationVolume custom) {
		CompoundTag tag = stack.getOrCreateTag();
		CompoundTag nbt = custom.writeToNBT();
		
		tag.put(NBT_HOOK_CUSTOMIZATIONS, nbt);
		tag.remove(NBT_HOOK_TEMPLATE);
		
		stack.setTag(tag);
	}

	public void applyHookTemplateName(ItemStack stack, GrapplingHookTemplate template) {
		CompoundTag tag = stack.getOrCreateTag();
		CompoundTag nbt = template.getMetadataBlob();

		tag.put("hook_template", nbt);

		stack.setTag(tag);
	}

	public static Vec applyHolderRotation(Vec angleVec, LivingEntity holder) {
		Vec newVec = angleVec.rotatePitch(Math.toRadians(-holder.getViewXRot(1.0F)));
		return newVec.rotateYaw(Math.toRadians(holder.getViewYRot(1.0F)));
	}

	public boolean getPropertyHook(ItemStack stack) {
    	return stack.getOrCreateTag().contains("hook");
	}

	public CustomizationVolume getDefaultCustomization() {
		return new CustomizationVolume();
	}
}
