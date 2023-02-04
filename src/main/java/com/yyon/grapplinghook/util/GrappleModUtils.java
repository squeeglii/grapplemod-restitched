package com.yyon.grapplinghook.util;

import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.network.NetworkManager;
import com.yyon.grapplinghook.network.clientbound.BaseMessageClient;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class GrappleModUtils {

	private static int controllerid = 0;
	public static final int GRAPPLE_ID = controllerid++;
	public static final int REPEL_ID = controllerid++;
	public static final int AIR_FRICTION_ID = controllerid++;

	public static void sendToCorrectClient(BaseMessageClient message, int playerid, Level w) {
		Entity entity = w.getEntity(playerid);
		if (entity instanceof ServerPlayer player) {
			NetworkManager.packetToClient(message, player);
		} else {
			GrappleMod.LOGGER.warn("ERROR! couldn't find player");
		}
	}

	public static BlockHitResult rayTraceBlocks(Entity entity, Level world, Vec from, Vec to) {
		BlockHitResult result = world.clip(new ClipContext(from.toVec3d(), to.toVec3d(), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity));

		return result.getType() == HitResult.Type.BLOCK
				? result
				: null;
	}

	public static long getTime(Level w) {
		return w.getGameTime();
	}

	@SafeVarargs
	public static boolean and(Supplier<Boolean>... conditions) {
		boolean failed = Arrays.stream(conditions).anyMatch(bool -> !bool.get());
		return !failed;
	}

	public static boolean and(List<Supplier<Boolean>> conditions) {
		boolean failed = conditions.stream().anyMatch(bool -> !bool.get());
		return !failed;
	}

	public static synchronized ServerPlayer[] getChunkPlayers(ServerLevel level, Vec point) {
		ChunkPos chunk = level.getChunkAt(new BlockPos(point.x, point.y, point.z)).getPos();
		return PlayerLookup.tracking(level, chunk).toArray(new ServerPlayer[0]);
	}

}
