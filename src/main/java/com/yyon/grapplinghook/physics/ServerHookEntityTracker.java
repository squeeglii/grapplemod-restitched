package com.yyon.grapplinghook.physics;

import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.content.entity.grapplinghook.GrapplinghookEntity;
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



	public static HashSet<Integer> attached = new HashSet<>();
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

	public static void removeAllHooksFor(Entity ownerEntity) {
		ServerHookEntityTracker.checkOwnerTypeWarning(ownerEntity);
		ServerHookEntityTracker.removeAllHooksFor(ownerEntity.getId());
	}

	/**
	 * Adds a grappling hook entity to be tracked.
	 * @param ownerId the thrower of the hook
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
	
	public static void receiveGrappleEnd(int ownerId, Level world, HashSet<Integer> hookEntityIds) {
		attached.remove(ownerId);
		
		for (int hookEntityId : hookEntityIds) {
	      	Entity grapple = world.getEntity(hookEntityId);
	  		if (grapple instanceof GrapplinghookEntity) {
	  			((GrapplinghookEntity) grapple).removeServer();
	  		}
		}
  		
  		Entity entity = world.getEntity(ownerId);
  		if (entity != null) {
      		entity.fallDistance = 0;
  		}
  		
  		removeAllHooksFor(ownerId);
	}

	public void saveStateToPlayer() {

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
