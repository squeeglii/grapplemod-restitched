package com.yyon.grapplinghook.physics;

import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.content.entity.grapplinghook.GrapplinghookEntity;
import com.yyon.grapplinghook.customization.CustomizationVolume;
import com.yyon.grapplinghook.physics.io.HookSnapshot;
import com.yyon.grapplinghook.physics.io.SerializableHookState;
import com.yyon.grapplinghook.util.NBTHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Handles server-side tracking and aggregation of grappling hook
 */
public class ServerHookEntityTracker {

	private static final HashMap<Integer, HashSet<GrapplinghookEntity>> allGrapplehookEntities = new HashMap<>();


	private static void checkOwnerTypeWarning(Entity entity) {
		if(!(entity instanceof GrapplinghookEntity)) return;

		// If someone needs to throw a hook from a hook, what the hell are you doing???
		// Submit a PR explaining yourself if this is really a problem. <3
		GrappleMod.LOGGER.warn(new Throwable(
				"A mod checks if a hook has other hooks attached to it. This is probably not right."
		));
	}


	/**
	 * Adds a grappling hook entity to be tracked.
	 * @param ownerId the thrower of the hook
	 * @param hookEntity the entity instance of the hook thrown
	 */
	public static void addGrappleEntity(int ownerId, GrapplinghookEntity hookEntity) {
		if (!allGrapplehookEntities.containsKey(ownerId))
			allGrapplehookEntities.put(ownerId, new HashSet<>());

		allGrapplehookEntities.get(ownerId).add(hookEntity);
	}

	/**
	 * Adds a grappling hook entity to be tracked.
	 * @param ownerEntity the thrower of the hook
	 */
	public static void removeAllHooksFor(Entity ownerEntity) {
		ServerHookEntityTracker.checkOwnerTypeWarning(ownerEntity);
		ServerHookEntityTracker.removeAllHooksFor(ownerEntity.getId());
	}

	/**
	 * Adds a grappling hook entity to be tracked.
	 * @param ownerId the id of the hook thrower
	 */
	public static void removeAllHooksFor(int ownerId) {
		if (!allGrapplehookEntities.containsKey(ownerId)) {
			allGrapplehookEntities.put(ownerId, new HashSet<>());
			return;
		}

		for (GrapplinghookEntity hookEntity : allGrapplehookEntities.get(ownerId)) {
			if (hookEntity == null) continue;
			if(!hookEntity.isAlive()) continue;

			hookEntity.removeServer();
		}

		allGrapplehookEntities.put(ownerId, new HashSet<>());
	}
	
	public static void handleGrappleEndFromClient(int ownerId, Level world, HashSet<Integer> hookEntityIds) {
		
		for (int hookEntityId : hookEntityIds) {
	      	Entity grapple = world.getEntity(hookEntityId);
	  		if (grapple instanceof GrapplinghookEntity) {
	  			((GrapplinghookEntity) grapple).removeServer();
	  		}
		}
  		
  		Entity entity = world.getEntity(ownerId);
  		if (entity != null) entity.fallDistance = 0;

  		
  		ServerHookEntityTracker.removeAllHooksFor(ownerId);
	}

	public static void savePlayerHookState(ServerPlayer hookHolder, CompoundTag saveTarget) {

		if(ServerHookEntityTracker.isAttachedToHooks(hookHolder))
			return;

		SerializableHookState holderHookState = SerializableHookState.saveNewFrom(hookHolder);
		CompoundTag grapplemodState = holderHookState.toNBT();

		if(grapplemodState.isEmpty())
			return;

		saveTarget.put("grapplemod", grapplemodState);
	}

	public static void initFromSavedHookState(ServerPlayer player) {
		//TODO: Decode
	}

	public static boolean isSavedHookStateValid(ServerPlayer player) {
		//TODO: Decode
	}


	public static Set<GrapplinghookEntity> getHooksThrownBy(Entity ownerEntity) {
		ServerHookEntityTracker.checkOwnerTypeWarning(ownerEntity);
		return ServerHookEntityTracker.getHooksThrownBy(ownerEntity.getId());
	}

	public static Set<GrapplinghookEntity> getHooksThrownBy(int ownerId) {
		Set<GrapplinghookEntity> hookEntities = allGrapplehookEntities.get(ownerId);
		return hookEntities != null
				? Collections.unmodifiableSet(hookEntities)
				: new HashSet<>();
	}

	public static boolean isAttachedToHooks(Entity ownerEntity) {
		ServerHookEntityTracker.checkOwnerTypeWarning(ownerEntity);
		return ServerHookEntityTracker.isAttachedToHooks(ownerEntity.getId());
	}

	public static boolean isAttachedToHooks(int ownerId) {
		Set<GrapplinghookEntity> hookEntities = allGrapplehookEntities.get(ownerId);
		return hookEntities != null && !hookEntities.isEmpty();
	}
}
