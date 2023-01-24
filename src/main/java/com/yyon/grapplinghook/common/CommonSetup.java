package com.yyon.grapplinghook.common;

import com.yyon.grapplinghook.block.modifierblock.GrappleModifierBlock;
import com.yyon.grapplinghook.blockentity.GrappleModifierBlockEntity;
import com.yyon.grapplinghook.entity.grapplehook.GrapplehookEntity;
import com.yyon.grapplinghook.network.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.Optional;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
public class CommonSetup {
	public static final CreativeModeTab tabGrapplemod = new CreativeModeTab("grapplemod") {
		@Environment(EnvType.CLIENT)
		public ItemStack makeIcon() {
			return new ItemStack(grapplingHookItem.get());
		}
	};


	public static SimpleChannel network;    // used to transmit your network messages
	public static final ResourceLocation simpleChannelRL = new ResourceLocation("grapplemod", "channel");

	@SubscribeEvent
	public static void init(FMLCommonSetupEvent event) {
		network = NetworkRegistry.newSimpleChannel(simpleChannelRL, () -> "1.0",
	            version -> true,
	            version -> true);
		int id = 0;
		network.registerMessage(id++, PlayerMovementMessage.class, PlayerMovementMessage::encode, PlayerMovementMessage::new, PlayerMovementMessage::onMessageReceived, Optional.of(NetworkDirection.PLAY_TO_SERVER));
		network.registerMessage(id++, GrappleEndMessage.class, GrappleEndMessage::encode, GrappleEndMessage::new, GrappleEndMessage::onMessageReceived, Optional.of(NetworkDirection.PLAY_TO_SERVER));
		network.registerMessage(id++, GrappleModifierMessage.class, GrappleModifierMessage::encode, GrappleModifierMessage::new, GrappleModifierMessage::onMessageReceived, Optional.of(NetworkDirection.PLAY_TO_SERVER));
		network.registerMessage(id++, KeypressMessage.class, KeypressMessage::encode, KeypressMessage::new, KeypressMessage::onMessageReceived, Optional.of(NetworkDirection.PLAY_TO_SERVER));
		network.registerMessage(id++, GrappleAttachMessage.class, GrappleAttachMessage::encode, GrappleAttachMessage::new, GrappleAttachMessage::onMessageReceived, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
		network.registerMessage(id++, GrappleDetachMessage.class, GrappleDetachMessage::encode, GrappleDetachMessage::new, GrappleDetachMessage::onMessageReceived, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
		network.registerMessage(id++, DetachSingleHookMessage.class, DetachSingleHookMessage::encode, DetachSingleHookMessage::new, DetachSingleHookMessage::onMessageReceived, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
		network.registerMessage(id++, GrappleAttachPosMessage.class, GrappleAttachPosMessage::encode, GrappleAttachPosMessage::new, GrappleAttachPosMessage::onMessageReceived, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
		network.registerMessage(id++, SegmentMessage.class, SegmentMessage::encode, SegmentMessage::new, SegmentMessage::onMessageReceived, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
		network.registerMessage(id++, LoggedInMessage.class, LoggedInMessage::encode, LoggedInMessage::new, LoggedInMessage::onMessageReceived, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
	}

}
