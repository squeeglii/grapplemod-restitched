package com.yyon.grapplinghook.server;

import com.yyon.grapplinghook.entity.grapplehook.GrapplehookEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.HashSet;

public class ServerControllerManager {
	public static HashSet<Integer> attached = new HashSet<>(); // server side
	public static HashMap<Integer, HashSet<GrapplehookEntity>> allGrapplehookEntities = new HashMap<>(); // server side

	public static void addGrapplehookEntity(int id, GrapplehookEntity hookEntity) {
		if (!allGrapplehookEntities.containsKey(id)) {
			allGrapplehookEntities.put(id, new HashSet<>());
		}
		allGrapplehookEntities.get(id).add(hookEntity);
	}
	
	public static void removeAllMultiHookGrapplehookEntities(int id) {
		if (!allGrapplehookEntities.containsKey(id)) {
			allGrapplehookEntities.put(id, new HashSet<>());
		}
		for (GrapplehookEntity hookEntity : allGrapplehookEntities.get(id)) {
			if (hookEntity != null && hookEntity.isAlive()) {
				hookEntity.removeServer();
			}
		}
		allGrapplehookEntities.put(id, new HashSet<>());
	}
	
	public static void receiveGrappleEnd(int id, Level world, HashSet<Integer> hookEntityIds) {
		attached.remove(id);
		
		for (int hookEntityId : hookEntityIds) {
	      	Entity grapple = world.getEntity(hookEntityId);
	  		if (grapple instanceof GrapplehookEntity) {
	  			((GrapplehookEntity) grapple).removeServer();
	  		}
		}
  		
  		Entity entity = world.getEntity(id);
  		if (entity != null) {
      		entity.fallDistance = 0;
  		}
  		
  		removeAllMultiHookGrapplehookEntities(id);
	}
}
