package com.yyon.grapplinghook.util;

import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.network.NetworkManager;
import com.yyon.grapplinghook.network.S2CPayloadProcessor;
import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class GrappleModUtils {

	public static final StreamCodec<ByteBuf, Direction> DIRECTION_STREAM_CODEC = ByteBufCodecs.idMapper(id -> Direction.values()[id], Direction::ordinal);

	public static EquipmentSlot currentHand(boolean isMainHand) {
		return  isMainHand ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
	}

	public static boolean hasArmourAbility(LivingEntity target, DataComponentType<?> ability) {
		for (ItemStack stack : target.getArmorSlots()) {
			if (stack == null) continue;

			if(EnchantmentHelper.has(stack, ability))
				return true;
		}

		return false;
	}

	public static void sendToCorrectClient(S2CPayloadProcessor message, int playerid, Level w) {
		Entity entity = w.getEntity(playerid);
		if (entity instanceof ServerPlayer player) {
			NetworkManager.packetToClient(message, player);
			return;
		}

		GrappleMod.LOGGER.warn("ERROR! couldn't find player");
	}

	public static BlockHitResult rayTraceBlocks(Entity entity, Level world, Vec from, Vec to) {
		BlockHitResult result = world.clip(new ClipContext(from.toVec3d(), to.toVec3d(), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity));

		return result.getType() == HitResult.Type.BLOCK
				? result
				: null;
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

	public static synchronized ServerPlayer[] getPlayersThatCanSeeChunkAt(ServerLevel level, Vec point) {
		ChunkPos chunk = level.getChunkAt(BlockPos.containing(point.toVec3d())).getPos();
		return PlayerLookup.tracking(level, chunk).toArray(new ServerPlayer[0]);
	}

	public static void registerPack(String id, Component displayName, ModContainer container, ResourcePackActivationType activationType) {
		ResourceManagerHelper.registerBuiltinResourcePack(GrappleMod.id(id), container, displayName, activationType);
	}

}
