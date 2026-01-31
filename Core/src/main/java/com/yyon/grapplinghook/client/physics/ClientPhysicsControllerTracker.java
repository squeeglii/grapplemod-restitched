package com.yyon.grapplinghook.client.physics;

import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.client.GrappleModClient;
import com.yyon.grapplinghook.client.api.GrappleModClientEvents;
import com.yyon.grapplinghook.client.ClientKey;
import com.yyon.grapplinghook.client.physics.context.AirFrictionPhysicsController;
import com.yyon.grapplinghook.client.physics.context.ForcefieldPhysicsController;
import com.yyon.grapplinghook.client.physics.context.GrapplingHookPhysicsController;
import com.yyon.grapplinghook.client.sound.RocketSound;
import com.yyon.grapplinghook.config.GrappleModClientConfig;
import com.yyon.grapplinghook.config.GrappleModCommonConfig;
import com.yyon.grapplinghook.content.entity.grapplinghook.GrapplinghookEntity;
import com.yyon.grapplinghook.content.item.EnderStaffItem;
import com.yyon.grapplinghook.content.item.GrapplehookItem;
import com.yyon.grapplinghook.content.physics.PhysicsControllers;
import com.yyon.grapplinghook.content.registry.internal.ModEnchantments;
import com.yyon.grapplinghook.content.customization.data.HookCustomization;
import com.yyon.grapplinghook.util.EnchantmentValues;
import com.yyon.grapplinghook.util.GrappleModUtils;
import com.yyon.grapplinghook.util.Vec;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

import static com.yyon.grapplinghook.content.registry.CustomizationProperties.*;

public class ClientPhysicsControllerTracker {


	public HashMap<Integer, GrapplingHookPhysicsController> controllers = new HashMap<>();
	public HashMap<BlockPos, GrapplingHookPhysicsController> controllerPos = new HashMap<>();

	public static long prevRopeJumpTime = 0;

	public HashMap<Integer, Long> enderLaunchTimer = new HashMap<>();

	public double rocketFuel = 1.0;
	public double rocketIncreaseTick = 0.0;
	public double rocketDecreaseTick = 0.0;

	public int ticksWallRunning = 0;

	private boolean prevJumpButton = false;
	private int ticksSinceLastOnGround = 0;
	private boolean alreadyUsedDoubleJump = false;


	public void onClientTick(Player player) {
		if (player.onGround() || (this.controllers.containsKey(player.getId()) && this.controllers.get(player.getId()).getType() == PhysicsControllers.GRAPPLING_HOOK)) {
			this.ticksWallRunning = 0;
		}

		if (this.isWallRunning(player, Vec.motionVec(player))) {
			if (!this.controllers.containsKey(player.getId())) {
				GrapplingHookPhysicsController controller = this.createControl(PhysicsControllers.AIR_FRICTION, -1, player.getId(), player.level(), null, null);

				if (controller != null && controller.getWallDirection() == null)
					controller.disable();
			}
			
			if (this.controllers.containsKey(player.getId())) {
				this.ticksSinceLastOnGround = 0;
				this.alreadyUsedDoubleJump = false;
			}
		}
		
		this.checkDoubleJump();
		
		this.checkSlide(player);
		
		this.rocketFuel += this.rocketIncreaseTick;

		for (GrapplingHookPhysicsController controller : new LinkedList<>(this.controllers.values()))
			controller.doClientTick();

		if (this.rocketFuel > 1)
			this.rocketFuel = 1;
		
		if (player.onGround()) {
			if (this.enderLaunchTimer.containsKey(player.getId())) {
				long timer = player.level().getGameTime() - this.enderLaunchTimer.get(player.getId());
				if (timer > 10)
					this.resetLauncherTime(player.getId());
			}
		}
	}

	public void checkSlide(Player player) {
		if (ClientKey.SLIDE.get().isDown() && !controllers.containsKey(player.getId()) && this.isSliding(player, Vec.motionVec(player))) {
			this.createControl(PhysicsControllers.AIR_FRICTION, -1, player.getId(), player.level(), null, null);
		}
	}

	public void launchPlayer(Player player) {
		long previousTime = this.enderLaunchTimer.containsKey(player.getId())
				? this.enderLaunchTimer.get(player.getId())
				: 0 ;

		long timer = player.level().getGameTime() - previousTime;

		if (timer > GrappleModCommonConfig.get().getEnderStaffCooldown()) {
			ItemStack mainHandStack = player.getItemInHand(InteractionHand.MAIN_HAND);
			ItemStack offHandStack = player.getItemInHand(InteractionHand.OFF_HAND);
			Item mainHandItem = mainHandStack.getItem();
			Item offHandItem = offHandStack.getItem();

			boolean isMainHolding = mainHandItem instanceof EnderStaffItem || mainHandItem instanceof GrapplehookItem;
			boolean isOffHolding = offHandItem instanceof EnderStaffItem || offHandItem instanceof GrapplehookItem;

			if(! (isMainHolding || isOffHolding)) return;

			ItemStack usedStack = isMainHolding ? mainHandStack : offHandStack;
			Item usedItem = isMainHolding ? mainHandItem : offHandItem;

			this.enderLaunchTimer.put(player.getId(), player.level().getGameTime());

			Vec facing = Vec.lookVec(player);

			HookCustomization custom = null;
			if (usedItem instanceof GrapplehookItem grapple)
				custom = grapple.getCustomizationsOrDefault(usedStack);

			if (!controllers.containsKey(player.getId())) {
				player.setOnGround(false);
				this.createControl(PhysicsControllers.AIR_FRICTION, -1, player.getId(), player.level(), null, custom);
			}

			facing.mutableScale(GrappleModCommonConfig.get().getEnderStaffStrength());
			this.receiveEnderLaunch(player.getId(), facing.x, facing.y, facing.z);
			GrappleModClient.get().playSound(GrappleMod.id("enderstaff"), GrappleModClientConfig.get().getEnderstaffVolume() * 0.5F);
		}
	}
	
	public void resetLauncherTime(int playerId) {
		if (this.enderLaunchTimer.containsKey(playerId))
			this.enderLaunchTimer.put(playerId, (long) 0);
	}

	public void updateRocketRegen(double rocketActiveTime, double rocketRefuelRatio) {
		this.rocketDecreaseTick = 0.05 / 2.0 / rocketActiveTime;
		this.rocketIncreaseTick = 0.05 / 2.0 / rocketActiveTime / rocketRefuelRatio;
	}
	

	public double getRocketFunctioning() {
		this.rocketFuel -= this.rocketIncreaseTick;
		this.rocketFuel -= this.rocketDecreaseTick;
		
		if (this.rocketFuel >= 0) {
			return 1;
		} else {
			this.rocketFuel = 0;
			return this.rocketIncreaseTick / this.rocketDecreaseTick / 2.0;
		}
	}

	public boolean isWallRunning(LivingEntity entity, Vec motion) {
		if(!(entity.horizontalCollision && !entity.onGround() && !entity.isCrouching())) return false;
		if(entity.onClimbable()) return false;
		if(!GrappleModUtils.hasArmourAbility(entity, ModEnchantments.EFFECT_WALL_RUNNING)) return false;
		if(ClientKey.DETACH.get().isDown() || Minecraft.getInstance().options.keyJump.isDown()) return false;

		BlockHitResult rayTraceResult = GrappleModUtils.rayTraceBlocks(entity, entity.level(), Vec.positionVec(entity), Vec.positionVec(entity).add(new Vec(0, -1, 0)));
		if(rayTraceResult == null) {
			double currentSpeed = Math.sqrt(Math.pow(motion.x, 2) + Math.pow(motion.z,  2));
			if(currentSpeed >= EnchantmentValues.MIN_WALLRUN_SPEED) {
				return true;
			}
		}

		return false;
	}
	
	public void checkDoubleJump() {
		Player player = Minecraft.getInstance().player;
		if(player == null) return;
		
		if (player.onGround()) {
			this.ticksSinceLastOnGround = 0;
			this.alreadyUsedDoubleJump = false;
		} else {
			this.ticksSinceLastOnGround++;
		}
		
		boolean isJumpButtonDown = Minecraft.getInstance().options.keyJump.isDown();

		List<Supplier<Boolean>> conditions = List.of(
				() -> isJumpButtonDown,
				() -> !prevJumpButton,
				() -> !player.isInWater(),
				() -> !player.isInLava(),
				() -> ticksSinceLastOnGround > 3,
				() -> GrappleModUtils.hasArmourAbility(player, ModEnchantments.EFFECT_DOUBLE_JUMP),
				() -> !player.getAbilities().flying,
				() -> !alreadyUsedDoubleJump
		);

		boolean allConditionsMet = GrappleModUtils.and(conditions);

		if(allConditionsMet && !controllers.containsKey(player.getId())) {
			this.createControl(PhysicsControllers.AIR_FRICTION, -1, player.getId(), player.level(), null, null);
			GrappleModClient.get().playDoubleJumpSound();
		}

		if(allConditionsMet && controllers.get(player.getId()) instanceof AirFrictionPhysicsController ctrl) {
			this.alreadyUsedDoubleJump = true;
			ctrl.doDoubleJump();
			GrappleModClient.get().playDoubleJumpSound();
		}
		
		this.prevJumpButton = isJumpButtonDown;
	}

	public boolean isSliding(LivingEntity entity, Vec motion) {
		if (entity.isInWater() || entity.isInLava()) return false;
		
		if (entity.onGround() && ClientKey.SLIDE.get().isDown()) {
			if (!GrappleModUtils.hasArmourAbility(entity, ModEnchantments.EFFECT_SLIDING)) return false;
			boolean wasSliding = false;
			int id = entity.getId();

			GrapplingHookPhysicsController controller = controllers.get(id);
			if (controller instanceof AirFrictionPhysicsController afc && afc.wasSliding()) {
				wasSliding = true;
			}

			double speed = motion.removeAlong(new Vec (0,1,0)).length();
			return speed > EnchantmentValues.MIN_SUSTAIN_SLIDE_SPEED && (wasSliding || speed > EnchantmentValues.MIN_SLIDE_SPEED);

		}
		
		return false;
	}


	public GrapplingHookPhysicsController createControl(ResourceLocation controllerId, int grapplehookEntityId, int playerId, Level world, BlockPos blockPos, HookCustomization custom) {
		GrapplinghookEntity grapplinghookEntity = world.getEntity(grapplehookEntityId) instanceof GrapplinghookEntity g
				? g
				: null;

		GrapplingHookPhysicsController currentController = this.controllers.get(playerId);

		boolean thisMulti = custom != null && custom.get(DOUBLE_HOOK_ATTACHED.get());

		if(currentController != null) {
			boolean currentMulti = currentController.getCurrentCustomizations() != null &&
					               currentController.getCurrentCustomizations().get(DOUBLE_HOOK_ATTACHED.get());

			if (!(thisMulti && currentMulti))
				currentController.disable();
		}
		
		GrapplingHookPhysicsController control;
		if (controllerId == PhysicsControllers.GRAPPLING_HOOK) {
			if (!thisMulti) {
				control = new GrapplingHookPhysicsController(grapplehookEntityId, playerId, world, custom);

			} else {
				control = this.controllers.get(playerId);

				GrapplingHookPhysicsController finalControl = control;
				List<Supplier<Boolean>> conditions = List.of(
						() -> finalControl != null,
						() -> finalControl.getClass().equals(GrapplingHookPhysicsController.class),
						() -> finalControl.getCurrentCustomizations().get(DOUBLE_HOOK_ATTACHED.get()),
						() -> grapplinghookEntity != null
				);

				if(GrappleModUtils.and(conditions)) {
					control.addHookEntity(grapplinghookEntity);
					return control;
				}

				control = new GrapplingHookPhysicsController(grapplehookEntityId, playerId, world, custom);
			}

		} else if (controllerId == PhysicsControllers.FORCEFIELD) {
			control = new ForcefieldPhysicsController(grapplehookEntityId, playerId, world);

		} else if (controllerId == PhysicsControllers.AIR_FRICTION) {
			control = new AirFrictionPhysicsController(grapplehookEntityId, playerId, world, custom);

		} else {
			GrappleMod.LOGGER.warn("Physics controller '%s' does not exist. Failed to create controller.".formatted(controllerId));
			return null;
		}

		if (blockPos != null)
			this.controllerPos.put(blockPos, control);

		this.registerController(playerId, control);
		
		Entity e = world.getEntity(playerId);
		if (e instanceof LocalPlayer p)
			control.receivePlayerMovementMessage(p.input.leftImpulse, p.input.forwardImpulse, p.input.shiftKeyDown);

		if(e != null) {
			GrappleModClientEvents.PHYSICS_APPLIED.invoker().onPhysicsApplied(e, controllerId);
		}

		return control;
	}

	private void registerController(int entityId, GrapplingHookPhysicsController controller) {
		if (this.controllers.containsKey(entityId))
			this.controllers.get(entityId).disable();

		this.controllers.put(entityId, controller);
	}

	public GrapplingHookPhysicsController unregisterController(int entityId) {
		if (!this.controllers.containsKey(entityId))
			return null;

		GrapplingHookPhysicsController controller = this.controllers.get(entityId);
		controllers.remove(entityId);
		controller.disable(); // TODO: Fix this up and force everything through disable so it's predictable.

		BlockPos pos = null;
		for (BlockPos blockpos : this.controllerPos.keySet()) {
			GrapplingHookPhysicsController otherController = this.controllerPos.get(blockpos);
			if (otherController == controller)
				pos = blockpos;
		}

		if (pos != null)
			this.controllerPos.remove(pos);

		return controller;
	}

	public GrapplingHookPhysicsController getController(int entityId) {
		return this.controllers.get(entityId);
	}

	public void receiveGrappleDetach(int id) {
		GrapplingHookPhysicsController controller = this.controllers.get(id);
		if (controller != null)
			controller.receiveGrappleDetach();
	}
	
	public void receiveGrappleDetachHook(int id, int hookId) {
		GrapplingHookPhysicsController controller = this.controllers.get(id);
		if (controller != null)
			controller.receiveGrappleDetachHook(hookId);
	}

	public void receiveEnderLaunch(int id, double x, double y, double z) {
		GrapplingHookPhysicsController controller = this.controllers.get(id);

		if (controller == null) {
			GrappleMod.LOGGER.warn("Couldn't find a  controller for handling Ender-Launch (hookId: %s)".formatted(id));
			return;
		}

		controller.receiveEnderLaunch(x, y, z);
	}

	public void startRocket(Player player, HookCustomization custom) {
		if (!custom.get(ROCKET_ATTACHED.get())) return;
		
		GrapplingHookPhysicsController controller;
		if (this.controllers.containsKey(player.getId())) {
			controller = this.controllers.get(player.getId());
			HookCustomization serverCustom = controller.getCurrentCustomizations();

			// Syncing controller's rocket property
			if (serverCustom == null || !serverCustom.get(ROCKET_ATTACHED.get())) {
				if (serverCustom == null)
					serverCustom = custom;

				serverCustom.copyPropertyFrom(custom, IS_EQUIPMENT_OVERRIDE.get());
				serverCustom.copyPropertyFrom(custom, ROCKET_ATTACHED.get());
				serverCustom.copyPropertyFrom(custom, ROCKET_FUEL_DEPLETION_RATIO.get());
				serverCustom.copyPropertyFrom(custom, ROCKET_FORCE.get());
				serverCustom.copyPropertyFrom(custom, ROCKET_REFUEL_RATIO.get());
				this.updateRocketRegen(custom.get(ROCKET_FUEL_DEPLETION_RATIO.get()), custom.get(ROCKET_REFUEL_RATIO.get()));

				controller.overrideCustomizations(serverCustom);
			}

		} else {
			controller = this.createControl(PhysicsControllers.AIR_FRICTION, -1, player.getId(), player.level(), null, custom);
		}

		if(controller == null)
			return;

		controller.resetRocketProgression();
		RocketSound sound = new RocketSound(controller, SoundEvent.createVariableRangeEvent(GrappleMod.id("rocket")), SoundSource.PLAYERS);
		Minecraft.getInstance().getSoundManager().play(sound);
	}


}
