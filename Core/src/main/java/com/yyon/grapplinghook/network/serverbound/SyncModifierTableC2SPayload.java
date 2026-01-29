package com.yyon.grapplinghook.network.serverbound;

import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.content.blockentity.GrappleModifierBlockEntity;
import com.yyon.grapplinghook.content.customization.data.HookCustomization;
import com.yyon.grapplinghook.network.C2SPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

/*
    GrappleMod is free software: you can redistribute it and/or modify
    it under the teHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with GrappleMod.  If not, see <http://www.gnu.org/licenses/>.
 */

// previously GrappleModifierMessage
public record SyncModifierTableC2SPayload(BlockPos pos, HookCustomization customization) implements C2SPayload {
	public static final ResourceLocation IDENTIFIER = GrappleMod.id("sync_modifier_table");
	public static final CustomPacketPayload.Type<SyncModifierTableC2SPayload> PAYLOAD_TYPE = new Type<>(IDENTIFIER);

	public static final StreamCodec<RegistryFriendlyByteBuf, SyncModifierTableC2SPayload> STREAM_CODEC = StreamCodec.composite(
			BlockPos.STREAM_CODEC,
			SyncModifierTableC2SPayload::pos,
			HookCustomization.STREAM_CODEC,
			SyncModifierTableC2SPayload::customization,
			SyncModifierTableC2SPayload::new
	);

	@NotNull
	@Override
	public Type<SyncModifierTableC2SPayload> type() {
		return PAYLOAD_TYPE;
	}

	@Override
	public void process(ServerPlayNetworking.Context ctx) {
		// Block Entities must be obtained on the main thread.
		ctx.server().execute(() -> {
			Level level = ctx.player().level();
			BlockEntity ent = level.getBlockEntity(this.pos);

			if (ent instanceof GrappleModifierBlockEntity e) {
				e.setCustomization(this.customization);
				return;
			}

			GrappleMod.LOGGER.warn("Wrong type! is null: %s, pos: %s, isClient: %s".formatted(ent == null, this.pos, level.isClientSide));
		});
	}
}
