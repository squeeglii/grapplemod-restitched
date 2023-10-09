package com.yyon.grapplinghook.physics;

import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.api.GrappleModServerEvents;
import com.yyon.grapplinghook.content.entity.grapplinghook.GrapplinghookEntity;
import com.yyon.grapplinghook.physics.io.HookSnapshot;
import com.yyon.grapplinghook.physics.io.IHookStateHolder;
import com.yyon.grapplinghook.physics.io.SerializableHookState;
import com.yyon.grapplinghook.util.Vec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;

/**
 * Handles server-side tracking and aggregation of grappling hook
 */
public class ServerHookEntityTracker {

	private static final float HOOK_DISTANCE_LENIENCY = 1.2f;

	public static final String NBT_HOOK_STATE = "grapplemod:hook_state";

	private static final HashMap<Integer, HashSet<GrapplinghookEntity>> allGrapplehookEntities = new HashMap<>();


	public static void checkOwnerIsNotHookElseWarn(Entity entity) {
		if(!(entity instanceof GrapplinghookEntity)) return;

		// If someone needs to throw a hook from a hook, what the hell are you doing???
		// Submit a PR explaining yourself if this is really a problem. <3
		GrappleMod.LOGGER.warn(new Throwable(
				"A mod checks if a hook has other hooks attached to it. This is probably not right."
		));
	}


	/**
	 * Adds a grappling hook entity to be tracked
	 * @param hookEntity the entity instance of the hook thrown
	 */
	public static void addGrappleEntity(Entity thrower, GrapplinghookEntity hookEntity) {
		int id = thrower.getId();
		if (!allGrapplehookEntities.containsKey(id))
			allGrapplehookEntities.put(id, new HashSet<>());

		allGrapplehookEntities.get(id).add(hookEntity);
		GrappleModServerEvents.HOOK_THROW.invoker().onHookThrown(thrower, hookEntity);
	}

	/**
	 * Adds a grappling hook entity to be tracked.
	 * @param ownerEntity the thrower of the hook
	 */
	public static void removeAllHooksFor(Entity ownerEntity) {
		ServerHookEntityTracker.checkOwnerIsNotHookElseWarn(ownerEntity);
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

		if(!ServerHookEntityTracker.isAttachedToHooks(hookHolder))
			return;

		SerializableHookState holderHookState = SerializableHookState.saveNewFrom(hookHolder);
		CompoundTag grapplemodState = holderHookState.toNBT();

		if(grapplemodState.isEmpty())
			return;

		saveTarget.put(NBT_HOOK_STATE, grapplemodState);
	}

	public static void applyFromSavedHookState(ServerPlayer player) {
		IHookStateHolder stateHolder = (IHookStateHolder) player;
		SerializableHookState state = stateHolder.grapplemod$getLastHookState().orElseThrow();
		state.applyTo(player);
	}

	/**
	 * Does the hook state still respect physics (the players position hasn't been changed?)
	 * and is there still a hook in the player's inventory
	 * For checking integrity of a Compound Tag, {@link SerializableHookState#isValidNBT(CompoundTag)}
	 */
	public static boolean isSavedHookStateValid(ServerPlayer player) {

		IHookStateHolder stateHolder = (IHookStateHolder) player;
		Optional<SerializableHookState> optState = stateHolder.grapplemod$getLastHookState();

		if(optState.isEmpty())
			return false;

		SerializableHookState state = optState.get();
		List<HookSnapshot> hooks = state.getHooks();

		Vec playerPos = Vec.positionVec(player);

		for(HookSnapshot snapshot: hooks) {
			Vec hookPos = snapshot.getHookPos();
			Vec delta = hookPos.sub(playerPos);

			// lengthSquared is more efficient? - this just makes sure the distance between the player and the
			// hook hasn't changed *unreasonably*. A bit of innacuracy should be fine.
			double maxDist = Math.pow(snapshot.getRopeSnapshot().getRopeLength(), 2) * HOOK_DISTANCE_LENIENCY;
			double distSq = delta.lengthSquared();

			if(distSq > maxDist)
				return false;

			// Check that the block the player is hooked to hasn't been nuked since they left.
			BlockPos lastBlock = snapshot.getLastBlockCollidedWith();
			Direction collideFace = snapshot.getLastBlockCollisionSide();
			boolean cannotHookOn = !player.level().loadedAndEntityCanStandOnFace(lastBlock, player, collideFace);

			if(cannotHookOn)
				return false;
		}

		return true;
	}


	public static Set<GrapplinghookEntity> getHooksThrownBy(Entity ownerEntity) {
		ServerHookEntityTracker.checkOwnerIsNotHookElseWarn(ownerEntity);
		return ServerHookEntityTracker.getHooksThrownBy(ownerEntity.getId());
	}

	public static Set<GrapplinghookEntity> getHooksThrownBy(int ownerId) {
		Set<GrapplinghookEntity> hookEntities = allGrapplehookEntities.get(ownerId);
		return hookEntities != null
				? Collections.unmodifiableSet(hookEntities)
				: new HashSet<>();
	}

	public static boolean isAttachedToHooks(Entity ownerEntity) {
		ServerHookEntityTracker.checkOwnerIsNotHookElseWarn(ownerEntity);
		return ServerHookEntityTracker.isAttachedToHooks(ownerEntity.getId());
	}

	public static boolean isAttachedToHooks(int ownerId) {
		Set<GrapplinghookEntity> hookEntities = allGrapplehookEntities.get(ownerId);
		return hookEntities != null && !hookEntities.isEmpty();
	}
}
