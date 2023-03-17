package com.yyon.grapplinghook.content.item;

import com.yyon.grapplinghook.client.GrappleModClient;
import com.yyon.grapplinghook.client.keybind.KeyBindingManagement;
import com.yyon.grapplinghook.client.keybind.MinecraftKey;
import com.yyon.grapplinghook.config.GrappleModConfig;
import com.yyon.grapplinghook.content.entity.grapplinghook.GrapplinghookEntity;
import com.yyon.grapplinghook.content.item.type.DroppableItem;
import com.yyon.grapplinghook.content.item.type.KeypressItem;
import com.yyon.grapplinghook.network.NetworkManager;
import com.yyon.grapplinghook.network.clientbound.DetachSingleHookMessage;
import com.yyon.grapplinghook.network.clientbound.GrappleDetachMessage;
import com.yyon.grapplinghook.network.serverbound.KeypressMessage;
import com.yyon.grapplinghook.physics.PhysicsContextTracker;
import com.yyon.grapplinghook.customization.GrappleCustomization;
import com.yyon.grapplinghook.util.GrappleModUtils;
import com.yyon.grapplinghook.util.Vec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
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

import java.util.HashMap;
import java.util.List;


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
	public static HashMap<Entity, GrapplinghookEntity> grapplehookEntitiesLeft = new HashMap<>();
	public static HashMap<Entity, GrapplinghookEntity> grapplehookEntitiesRight = new HashMap<>();
	
	public GrapplehookItem() {
		super(new Item.Properties().stacksTo(1).durability(GrappleModConfig.getConf().grapplinghook.other.default_durability));
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
		if (GrapplehookItem.grapplehookEntitiesLeft.containsKey(entity)) {
			GrapplinghookEntity hookEntity = GrapplehookItem.grapplehookEntitiesLeft.get(entity);
			if (hookEntity != null && hookEntity.isAlive()) {
				return hookEntity;
			}
		}
		return null;
	}
	public GrapplinghookEntity getHookEntityRight(Entity entity) {
		if (GrapplehookItem.grapplehookEntitiesRight.containsKey(entity)) {
			GrapplinghookEntity hookEntity = GrapplehookItem.grapplehookEntitiesRight.get(entity);
			if (hookEntity != null && hookEntity.isAlive()) {
				return hookEntity;
			}
		}
		return null;
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
	public boolean canAttackBlock(BlockState p_195938_1_, Level p_195938_2_, BlockPos p_195938_3_,
			Player p_195938_4_) {
		return false;
	}

	@Override
	public void onCustomKeyDown(ItemStack stack, Player player, KeypressItem.Keys key, boolean ismainhand) {
		if (player.level().isClientSide) {
			if (key == KeypressItem.Keys.LAUNCHER) {
				if (this.getCustomization(stack).enderstaff) {
					GrappleModClient.get().launchPlayer(player);
				}
			} else if (key == KeypressItem.Keys.THROWLEFT || key == KeypressItem.Keys.THROWRIGHT || key == KeypressItem.Keys.THROWBOTH) {
				NetworkManager.packetToServer(new KeypressMessage(key, true));

			} else if (key == KeypressItem.Keys.ROCKET) {
				GrappleCustomization custom = this.getCustomization(stack);
				if (custom.rocket) {
					GrappleModClient.get().startRocket(player, custom);
				}
			}
		} else {
	    	GrappleCustomization custom = this.getCustomization(stack);

			if (key == KeypressItem.Keys.THROWBOTH || (!custom.doublehook && (key == KeypressItem.Keys.THROWLEFT || key == KeypressItem.Keys.THROWRIGHT))) {
	        	throwBoth(stack, player.level(), player, ismainhand);

			} else if (key == KeypressItem.Keys.THROWLEFT) {
				GrapplinghookEntity hookLeft = getHookEntityLeft(player);

	    		if (hookLeft != null) {
	    			detachLeft(player);
		    		return;
				}
				
				stack.hurtAndBreak(1, (ServerPlayer) player, (p) -> {});
				if (stack.getCount() <= 0) {
					return;
				}
				
				boolean threw = throwLeft(stack, player.level(), player, ismainhand);

				if (threw) {
			        player.level().playSound(null, player.position().x, player.position().y, player.position().z, SoundEvents.ARROW_SHOOT, SoundSource.NEUTRAL, 1.0F, 1.0F / (player.getRandom().nextFloat() * 0.4F + 1.2F) + 2.0F * 0.5F);
				}

			} else if (key == KeypressItem.Keys.THROWRIGHT) {
				GrapplinghookEntity hookRight = getHookEntityRight(player);

	    		if (hookRight != null) {
	    			detachRight(player);
		    		return;
				}
				
				stack.hurtAndBreak(1, (ServerPlayer) player, (p) -> {});
				if (stack.getCount() <= 0) {
					return;
				}
				
				throwRight(stack, player.level(), player, ismainhand);

		        player.level().playSound(null, player.position().x, player.position().y, player.position().z, SoundEvents.ARROW_SHOOT, SoundSource.NEUTRAL, 1.0F, 1.0F / (player.getRandom().nextFloat() * 0.4F + 1.2F) + 2.0F * 0.5F);
			}
		}
	}
	
	@Override
	public void onCustomKeyUp(ItemStack stack, Player player, KeypressItem.Keys key, boolean ismainhand) {
		if (player.level().isClientSide) {
			if (key == KeypressItem.Keys.THROWLEFT || key == KeypressItem.Keys.THROWRIGHT || key == KeypressItem.Keys.THROWBOTH) {
				NetworkManager.packetToServer(new KeypressMessage(key, false));
			}
		} else {
	    	GrappleCustomization custom = this.getCustomization(stack);
	    	
	    	if (custom.detachonkeyrelease) {
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

	public void throwBoth(ItemStack stack, Level worldIn, LivingEntity entityLiving, boolean righthand) {
		GrapplinghookEntity hookLeft = getHookEntityLeft(entityLiving);
		GrapplinghookEntity hookRight = getHookEntityRight(entityLiving);

		if (hookLeft != null || hookRight != null) {
			detachBoth(entityLiving);
    		return;
		}

		stack.hurtAndBreak(1, (ServerPlayer) entityLiving, (p) -> {});
		if (stack.getCount() <= 0) {
			return;
		}

    	GrappleCustomization custom = this.getCustomization(stack);
  		double angle = custom.angle;
//  		double verticalangle = custom.verticalthrowangle;
  		if (entityLiving.isCrouching()) {
  			angle = custom.sneakingangle;
//  			verticalangle = custom.sneakingverticalthrowangle;
  		}

    	if (!(!custom.doublehook || angle == 0)) {
    		throwLeft(stack, worldIn, entityLiving, righthand);
    	}
		throwRight(stack, worldIn, entityLiving, righthand);

		entityLiving.level().playSound(null, entityLiving.position().x, entityLiving.position().y, entityLiving.position().z, SoundEvents.ARROW_SHOOT, SoundSource.NEUTRAL, 1.0F, 1.0F / (worldIn.random.nextFloat() * 0.4F + 1.2F) + 2.0F * 0.5F);
	}
	
	public boolean throwLeft(ItemStack stack, Level worldIn, LivingEntity entityLiving, boolean righthand) {
    	GrappleCustomization custom = this.getCustomization(stack);
    	
  		double angle = custom.angle;
  		double verticalangle = custom.verticalthrowangle;
  		
  		if (entityLiving.isCrouching()) {
  			angle = custom.sneakingangle;
  			verticalangle = custom.sneakingverticalthrowangle;
  		}

		Vec anglevec = Vec.fromAngles(Math.toRadians(-angle), Math.toRadians(verticalangle));
  		anglevec = anglevec.rotatePitch(Math.toRadians(-entityLiving.getViewXRot(1.0F)));
  		anglevec = anglevec.rotateYaw(Math.toRadians(entityLiving.getViewYRot(1.0F)));
        float velx = -Mth.sin((float) anglevec.getYaw() * 0.017453292F) * Mth.cos((float) anglevec.getPitch() * 0.017453292F);
        float vely = -Mth.sin((float) anglevec.getPitch() * 0.017453292F);
        float velz = Mth.cos((float) anglevec.getYaw() * 0.017453292F) * Mth.cos((float) anglevec.getPitch() * 0.017453292F);
		GrapplinghookEntity hookEntity = this.createGrapplehookEntity(stack, worldIn, entityLiving, false, true);
        float extravelocity = (float) Vec.motionVec(entityLiving).distanceAlong(new Vec(velx, vely, velz));
        if (extravelocity < 0) { extravelocity = 0; }
        hookEntity.shoot( velx, vely, velz, hookEntity.getVelocity() + extravelocity, 0.0F);
        
		worldIn.addFreshEntity(hookEntity);
		setHookEntityLeft(entityLiving, hookEntity);    			
		
		return true;
	}
	
	public void throwRight(ItemStack stack, Level worldIn, LivingEntity entityLiving, boolean righthand) {
    	GrappleCustomization custom = this.getCustomization(stack);
    	
  		double angle = custom.angle;
  		double verticalangle = custom.verticalthrowangle;
  		if (entityLiving.isCrouching()) {
  			angle = custom.sneakingangle;
  			verticalangle = custom.sneakingverticalthrowangle;
  		}

    	if (!custom.doublehook || angle == 0) {
			GrapplinghookEntity hookEntity = this.createGrapplehookEntity(stack, worldIn, entityLiving, righthand, false);
      		Vec anglevec = new Vec(0,0,1).rotatePitch(Math.toRadians(verticalangle));
      		anglevec = anglevec.rotatePitch(Math.toRadians(-entityLiving.getViewXRot(1.0F)));
      		anglevec = anglevec.rotateYaw(Math.toRadians(entityLiving.getViewYRot(1.0F)));
	        float velx = -Mth.sin((float) anglevec.getYaw() * 0.017453292F) * Mth.cos((float) anglevec.getPitch() * 0.017453292F);
	        float vely = -Mth.sin((float) anglevec.getPitch() * 0.017453292F);
	        float velz = Mth.cos((float) anglevec.getYaw() * 0.017453292F) * Mth.cos((float) anglevec.getPitch() * 0.017453292F);
	        float extravelocity = (float) Vec.motionVec(entityLiving).distanceAlong(new Vec(velx, vely, velz));
	        if (extravelocity < 0) { extravelocity = 0; }
	        hookEntity.shoot(velx, vely, velz, hookEntity.getVelocity() + extravelocity, 0.0F);
			setHookEntityRight(entityLiving, hookEntity);
			worldIn.addFreshEntity(hookEntity);
    	} else {

			Vec anglevec = Vec.fromAngles(Math.toRadians(angle), Math.toRadians(verticalangle));
      		anglevec = anglevec.rotatePitch(Math.toRadians(-entityLiving.getViewXRot(1.0F)));
      		anglevec = anglevec.rotateYaw(Math.toRadians(entityLiving.getViewYRot(1.0F)));
	        float velx = -Mth.sin((float) anglevec.getYaw() * 0.017453292F) * Mth.cos((float) anglevec.getPitch() * 0.017453292F);
	        float vely = -Mth.sin((float) anglevec.getPitch() * 0.017453292F);
	        float velz = Mth.cos((float) anglevec.getYaw() * 0.017453292F) * Mth.cos((float) anglevec.getPitch() * 0.017453292F);
			GrapplinghookEntity hookEntity = this.createGrapplehookEntity(stack, worldIn, entityLiving, true, true);
	        float extravelocity = (float) Vec.motionVec(entityLiving).distanceAlong(new Vec(velx, vely, velz));
	        if (extravelocity < 0) { extravelocity = 0; }
	        hookEntity.shoot(velx, vely, velz, hookEntity.getVelocity() + extravelocity, 0.0F);
            
			worldIn.addFreshEntity(hookEntity);
			setHookEntityRight(entityLiving, hookEntity);
		}
	}
	
	public void detachBoth(LivingEntity entityLiving) {
		GrapplinghookEntity hookLeft = getHookEntityLeft(entityLiving);
		GrapplinghookEntity hookRight = getHookEntityRight(entityLiving);

		setHookEntityLeft(entityLiving, null);
		setHookEntityRight(entityLiving, null);
		
		if (hookLeft != null) {
			hookLeft.removeServer();
		}
		if (hookRight != null) {
			hookRight.removeServer();
		}

		int id = entityLiving.getId();
		GrappleModUtils.sendToCorrectClient(new GrappleDetachMessage(id), entityLiving.getId(), entityLiving.level());

		PhysicsContextTracker.attached.remove(id);
	}
	
	public void detachLeft(LivingEntity entityLiving) {
		GrapplinghookEntity hookLeft = getHookEntityLeft(entityLiving);
		
		setHookEntityLeft(entityLiving, null);
		
		if (hookLeft != null) {
			hookLeft.removeServer();
		}
		
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
		
		if (hookRight != null) {
			hookRight.removeServer();
		}
		
		int id = entityLiving.getId();
		
		// remove controller if hook is attached
		if (getHookEntityLeft(entityLiving) == null) {
			GrappleModUtils.sendToCorrectClient(new GrappleDetachMessage(id), id, entityLiving.level());
		} else {
			GrappleModUtils.sendToCorrectClient(new DetachSingleHookMessage(id, hookRight.getId()), id, entityLiving.level());
		}

		PhysicsContextTracker.attached.remove(id);
	}
	
    public double getAngle(LivingEntity entity, ItemStack stack) {
    	GrappleCustomization custom = this.getCustomization(stack);
    	if (entity.isCrouching()) {
    		return custom.sneakingangle;
    	} else {
    		return custom.angle;
    	}
    }
	
	public GrapplinghookEntity createGrapplehookEntity(ItemStack stack, Level worldIn, LivingEntity entityLiving, boolean righthand, boolean isdouble) {
		GrapplinghookEntity hookEntity = new GrapplinghookEntity(worldIn, entityLiving, righthand, this.getCustomization(stack), isdouble);
		PhysicsContextTracker.addGrapplehookEntity(entityLiving.getId(), hookEntity);
		return hookEntity;
	}
    
    public GrappleCustomization getCustomization(ItemStack itemstack) {
    	CompoundTag tag = itemstack.getOrCreateTag();
    	
    	if (tag.contains("custom")) {
        	GrappleCustomization custom = new GrappleCustomization();
    		custom.loadNBT(tag.getCompound("custom"));
        	return custom;
    	} else {
    		GrappleCustomization custom = this.getDefaultCustomization();

			CompoundTag nbt = custom.writeNBT();
			
			tag.put("custom", nbt);
			itemstack.setTag(tag);

    		return custom;
    	}
    }
    
    public GrappleCustomization getDefaultCustomization() {
    	return new GrappleCustomization();
    }
    
	@Override
	@Environment(EnvType.CLIENT)
	public void appendHoverText(ItemStack stack, Level world, List<Component> list, TooltipFlag par4) {
		GrappleCustomization custom = getCustomization(stack);
		
		if (Screen.hasShiftDown()) {
			if (!custom.detachonkeyrelease) {
				list.add(Component.literal(KeyBindingManagement.key_boththrow.getTranslatedKeyMessage().getString() + " " + Component.translatable("grappletooltip.throw.desc").getString()));
				list.add(Component.literal(KeyBindingManagement.key_boththrow.getTranslatedKeyMessage().getString() + " " + Component.translatable("grappletooltip.release.desc").getString()));
				list.add(Component.translatable("grappletooltip.double.desc").append(KeyBindingManagement.key_boththrow.getTranslatedKeyMessage()).append(" ").append(Component.translatable("grappletooltip.releaseandthrow.desc")));
			} else {
				list.add(Component.literal(KeyBindingManagement.key_boththrow.getTranslatedKeyMessage().getString() + " " + Component.translatable("grappletooltip.throwhold.desc").getString()));
			}
			list.add(Component.literal(GrappleModClient.get().getKeyname(MinecraftKey.keyBindForward) + ", " +
					GrappleModClient.get().getKeyname(MinecraftKey.keyBindLeft) + ", " +
					GrappleModClient.get().getKeyname(MinecraftKey.keyBindBack) + ", " +
					GrappleModClient.get().getKeyname(MinecraftKey.keyBindRight) +
					" " + Component.translatable("grappletooltip.swing.desc").getString()));
			list.add(Component.literal(KeyBindingManagement.key_jumpanddetach.getTranslatedKeyMessage().getString() + " " + Component.translatable("grappletooltip.jump.desc").getString()));
			list.add(Component.literal(KeyBindingManagement.key_slow.getTranslatedKeyMessage().getString() + " " + Component.translatable("grappletooltip.slow.desc").getString()));
			list.add(Component.literal(KeyBindingManagement.key_climb.getTranslatedKeyMessage().getString() + " + " + GrappleModClient.get().getKeyname(MinecraftKey.keyBindForward) + " / " +
					KeyBindingManagement.key_climbup.getTranslatedKeyMessage().getString() +
					" " + Component.translatable("grappletooltip.climbup.desc").getString()));
			list.add(Component.literal(KeyBindingManagement.key_climb.getTranslatedKeyMessage().getString() + " + " + GrappleModClient.get().getKeyname(MinecraftKey.keyBindBack) + " / " +
					KeyBindingManagement.key_climbdown.getTranslatedKeyMessage().getString() +
					" " + Component.translatable("grappletooltip.climbdown.desc").getString()));
			if (custom.enderstaff) {
				list.add(Component.literal(KeyBindingManagement.key_enderlaunch.getTranslatedKeyMessage().getString() + " " + Component.translatable("grappletooltip.enderlaunch.desc").getString()));
			}
			if (custom.rocket) {
				list.add(Component.literal(KeyBindingManagement.key_rocket.getTranslatedKeyMessage().getString() + " " + Component.translatable("grappletooltip.rocket.desc").getString()));
			}
			if (custom.motor) {
				if (custom.motorwhencrouching && !custom.motorwhennotcrouching) {
					list.add(Component.literal(KeyBindingManagement.key_motoronoff.getTranslatedKeyMessage().getString() + " " + Component.translatable("grappletooltip.motoron.desc").getString()));
				}
				else if (!custom.motorwhencrouching && custom.motorwhennotcrouching) {
					list.add(Component.literal(KeyBindingManagement.key_motoronoff.getTranslatedKeyMessage().getString() + " " + Component.translatable("grappletooltip.motoroff.desc").getString()));
				}
			}
			if (custom.doublehook) {
				if (!custom.detachonkeyrelease) {
					list.add(Component.literal(KeyBindingManagement.key_leftthrow.getTranslatedKeyMessage().getString() + " " + Component.translatable("grappletooltip.throwleft.desc").getString()));
					list.add(Component.literal(KeyBindingManagement.key_rightthrow.getTranslatedKeyMessage().getString() + " " + Component.translatable("grappletooltip.throwright.desc").getString()));
				} else {
					list.add(Component.literal(KeyBindingManagement.key_leftthrow.getTranslatedKeyMessage().getString() + " " + Component.translatable("grappletooltip.throwlefthold.desc").getString()));
					list.add(Component.literal(KeyBindingManagement.key_rightthrow.getTranslatedKeyMessage().getString() + " " + Component.translatable("grappletooltip.throwrighthold.desc").getString()));
				}
			} else {
				list.add(Component.literal(KeyBindingManagement.key_rightthrow.getTranslatedKeyMessage().getString() + " " + Component.translatable("grappletooltip.throwalt.desc").getString()));
			}
			if (custom.reelin) {
				list.add(Component.literal(GrappleModClient.get().getKeyname(MinecraftKey.keyBindSneak) + " " + Component.translatable("grappletooltip.reelin.desc").getString()));
			}
		} else {
			if (Screen.hasControlDown()) {
				for (String option : GrappleCustomization.booleanoptions) {
					if (custom.isOptionValid(option) && custom.getBoolean(option) != GrappleCustomization.DEFAULT.getBoolean(option)) {
						list.add(Component.literal((custom.getBoolean(option) ? "" : Component.translatable("grappletooltip.negate.desc").getString() + " ") + Component.translatable(custom.getName(option)).getString()));
					}
				}
				for (String option : GrappleCustomization.doubleoptions) {
					if (custom.isOptionValid(option) && (custom.getDouble(option) != GrappleCustomization.DEFAULT.getDouble(option))) {
						list.add(Component.translatable(custom.getName(option)).append(": " + Math.floor(custom.getDouble(option) * 100) / 100));
					}
				}
			} else {
				if (custom.doublehook) {
					list.add(Component.translatable(custom.getName("doublehook")));
				}
				if (custom.motor) {
					if (custom.smartmotor) {
						list.add(Component.translatable(custom.getName("smartmotor")));
					} else {
						list.add(Component.translatable(custom.getName("motor")));
					}
				}
				if (custom.enderstaff) {
					list.add(Component.translatable(custom.getName("enderstaff")));
				}
				if (custom.rocket) {
					list.add(Component.translatable(custom.getName("rocket")));
				}
				if (custom.attract) {
					list.add(Component.translatable(custom.getName("attract")));
				}
				if (custom.repel) {
					list.add(Component.translatable(custom.getName("repel")));
				}
				
				list.add(Component.literal(""));
				list.add(Component.translatable("grappletooltip.shiftcontrols.desc"));
				list.add(Component.translatable("grappletooltip.controlconfiguration.desc"));
			}
		}
	}

	public void setCustomOnServer(ItemStack helditemstack, GrappleCustomization custom) {
		CompoundTag tag = helditemstack.getOrCreateTag();
		CompoundTag nbt = custom.writeNBT();
		
		tag.put("custom", nbt);
		
		helditemstack.setTag(tag);
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
	
	public boolean getPropertyRocket(ItemStack stack, Level world, LivingEntity entity) {
		return this.getCustomization(stack).rocket;
	}

	public boolean getPropertyDouble(ItemStack stack, Level world, LivingEntity entity) {
		return this.getCustomization(stack).doublehook;
	}

	public boolean getPropertyMotor(ItemStack stack, Level world, LivingEntity entity) {
		return this.getCustomization(stack).motor;
	}

	public boolean getPropertySmart(ItemStack stack, Level world, LivingEntity entity) {
		return this.getCustomization(stack).smartmotor;
	}

	public boolean getPropertyEnderstaff(ItemStack stack, Level world, LivingEntity entity) {
		return this.getCustomization(stack).enderstaff;
	}

	public boolean getPropertyMagnet(ItemStack stack, Level world, LivingEntity entity) {
		return this.getCustomization(stack).attract || this.getCustomization(stack).repel;
	}

	public boolean getPropertyHook(ItemStack stack, Level world, LivingEntity entity) {
    	CompoundTag tag = stack.getOrCreateTag();
    	return tag.contains("hook");
	}
}
