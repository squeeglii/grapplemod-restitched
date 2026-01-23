package com.yyon.grapplinghook.client;

import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.client.gui.GrappleModifierBlockGUI;
import com.yyon.grapplinghook.client.keybind.GrappleKey;
import com.yyon.grapplinghook.client.physics.ClientPhysicsControllerTracker;
import com.yyon.grapplinghook.client.physics.context.AirFrictionPhysicsController;
import com.yyon.grapplinghook.client.physics.context.ForcefieldPhysicsController;
import com.yyon.grapplinghook.client.render.entity.GrapplinghookEntityRenderer;
import com.yyon.grapplinghook.config.GrappleModLegacyConfig;
import com.yyon.grapplinghook.content.blockentity.GrappleModifierBlockEntity;
import com.yyon.grapplinghook.content.entity.grapplinghook.GrapplinghookEntity;
import com.yyon.grapplinghook.content.entity.grapplinghook.RopeSegmentHandler;
import com.yyon.grapplinghook.content.physics.PhysicsControllers;
import com.yyon.grapplinghook.content.registry.internal.ModEntities;
import com.yyon.grapplinghook.content.registry.internal.ModEntityLayerIdentifiers;
import com.yyon.grapplinghook.content.registry.internal.ModItems;
import com.yyon.grapplinghook.customization.data.HookCustomization;
import com.yyon.grapplinghook.customization.type.BooleanProperty;
import com.yyon.grapplinghook.network.NetworkContext;
import com.yyon.grapplinghook.network.NetworkManager;
import com.yyon.grapplinghook.network.clientbound.BaseMessageClient;
import com.yyon.grapplinghook.network.clientbound.GrappleAttachMessage;
import com.yyon.grapplinghook.network.clientbound.GrappleAttachPosMessage;
import com.yyon.grapplinghook.util.GrappleModUtils;
import com.yyon.grapplinghook.util.Vec;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

import static com.yyon.grapplinghook.content.registry.CustomizationProperties.*;

@Environment(EnvType.CLIENT)
public class GrappleModClient implements ClientModInitializer {

    private static GrappleModClient clientInstance;


    private static final ResourceLocation SOUND_DOUBLE_JUMP = new ResourceLocation("grapplemod", "doublejump");
    private static final  ResourceLocation SOUND_SLIDE = new ResourceLocation("grapplemod", "slide");

    private ClientPhysicsControllerTracker clientPhysicsControllerTracker;


    @Override
    public void onInitializeClient() {
        GrappleModClient.clientInstance = this;

        EntityRendererRegistry.register(ModEntities.GRAPPLE_HOOK.get(), new GrapplehookEntityRenderFactory());

        GrappleKey.registerAll();
        ModEntityLayerIdentifiers.registerAll();

        NetworkManager.registerClientPacketListeners();
        GrappleModClient.registerClientsidePacketHandlers();

        this.clientPhysicsControllerTracker = new ClientPhysicsControllerTracker();
        this.registerPropertyOverride();
        this.registerResourcePacks();
    }

    public static GrappleModClient get() {
        return GrappleModClient.clientInstance;
    }



    public void registerPropertyOverride() {
        ItemProperties.register(ModItems.GRAPPLING_HOOK.get(), new ResourceLocation("rocket"), (stack, world, entity, seed) -> propertyEquipOverride(stack, ROCKET_ATTACHED.get()));
        ItemProperties.register(ModItems.GRAPPLING_HOOK.get(), new ResourceLocation("double"), (stack, world, entity, seed) -> propertyEquipOverride(stack, DOUBLE_HOOK_ATTACHED.get()));
        ItemProperties.register(ModItems.GRAPPLING_HOOK.get(), new ResourceLocation("motor"), (stack, world, entity, seed) -> propertyEquipOverride(stack, MOTOR_ATTACHED.get()));
        ItemProperties.register(ModItems.GRAPPLING_HOOK.get(), new ResourceLocation("smart"), (stack, world, entity, seed) -> propertyEquipOverride(stack, SMART_MOTOR.get()));
        ItemProperties.register(ModItems.GRAPPLING_HOOK.get(), new ResourceLocation("enderstaff"), (stack, world, entity, seed) -> propertyEquipOverride(stack, ENDER_STAFF_ATTACHED.get()));
        ItemProperties.register(ModItems.GRAPPLING_HOOK.get(), new ResourceLocation("magnet"), (stack, world, entity, seed) -> propertyEquipOverride(stack, MAGNET_ATTACHED.get()));
        ItemProperties.register(ModItems.GRAPPLING_HOOK.get(), new ResourceLocation("attached"), (stack, world, entity, seed) -> {
            if (entity == null) return 0;
            return (this.getClientControllerManager().controllers.containsKey(entity.getId()) && !(this.getClientControllerManager().controllers.get(entity.getId()) instanceof AirFrictionPhysicsController)) ? 1 : 0;
        });
        ItemProperties.register(ModItems.FORCE_FIELD.get(), new ResourceLocation("attached"), (stack, world, entity, seed) -> {
            if (entity == null) return 0;
            return (this.getClientControllerManager().controllers.containsKey(entity.getId()) && this.getClientControllerManager().controllers.get(entity.getId()) instanceof ForcefieldPhysicsController) ? 1 : 0;
        });
        ItemProperties.register(ModItems.GRAPPLING_HOOK.get(), new ResourceLocation("hook"), (stack, world, entity, seed) -> ModItems.GRAPPLING_HOOK.get().shouldDisplayAsHookOnly(stack) ? 1 : 0);
        ItemProperties.register(ModItems.BLUEPRINT.get(), GrappleMod.id("written"), (stack, world, entity, seed) -> ModItems.BLUEPRINT.get().isBlank(stack) ? 0 : 1);
    }

    public void registerResourcePacks() {
        Optional<ModContainer> cont = FabricLoader.getInstance().getModContainer(GrappleMod.MOD_ID);

        if(cont.isEmpty()) {
            GrappleMod.LOGGER.error("Unable to register resource packs! This mod technically doesn't exist!!");
            return;
        }

        ModContainer container = cont.get();

        GrappleModUtils.registerPack("original_textures", Component.translatable("pack.grapplemod.original"), container, ResourcePackActivationType.NORMAL);
    }

    private static void registerClientsidePacketHandlers() {
        GrappleAttachMessage.packetProcessor = packet -> {
            Level world = (Level) Minecraft.getInstance().level;

            if(world == null) {
                GrappleMod.LOGGER.warn("Network Message received in invalid context (World not present | GrappleAttach)");
                return;
            }


            Entity e = world.getEntity(packet.id);

            if (e == null) {
                GrappleMod.LOGGER.warn("GrappleAttachMessage received for a hook that doesn't exist on the client side! (yet?)");
                return;
            }

            if (e instanceof GrapplinghookEntity grapple) {

                grapple.clientAttach(packet.x, packet.y, packet.z);
                RopeSegmentHandler segmentHandler = grapple.getSegmentHandler();
                segmentHandler.segments = packet.segments;
                segmentHandler.segmentBottomSides = packet.segmentBottomSides;
                segmentHandler.segmentTopSides = packet.segmentTopSides;

                Entity holder = world.getEntity(packet.entityId);

                if (holder == null) {
                    GrappleMod.LOGGER.warn("Network Message received in invalid context (Holder does not exist | GrappleAttach)");
                    return;
                }

                segmentHandler.forceSetPos(new Vec(packet.x, packet.y, packet.z), Vec.positionVec(holder));
                GrappleModClient.get()
                        .getClientControllerManager()
                        .createControl(PhysicsControllers.GRAPPLING_HOOK, packet.id, packet.entityId, world, packet.blockPos, packet.custom);
            }
        };

        GrappleAttachPosMessage.packetProcessor = packet -> {
            Level world = Minecraft.getInstance().level;

            if (world == null) {
                GrappleMod.LOGGER.warn("Network Message received in invalid context (World not present | GrappleAttachPos)");
                return;
            }

            Entity e = world.getEntity(packet.id);

            if (e == null) {
                GrappleMod.LOGGER.warn("GrappleAttachPos received for a hook that doesn't exist on the client side! (yet?)");
                return;
            }

            if (e instanceof GrapplinghookEntity grapple) {
                grapple.setAttachPos(packet.x, packet.y, packet.z);
            }
        };
    }


    public void startRocket(Player player, HookCustomization custom) {
        this.getClientControllerManager().startRocket(player, custom);
    }

    public void openModifierScreen(GrappleModifierBlockEntity tile) {
        Minecraft.getInstance().setScreen(new GrappleModifierBlockGUI(tile));
    }

    public void onMessageReceivedClient(BaseMessageClient msg, NetworkContext ctx) {
        msg.processMessage(ctx);
    }


    public void playSlideSound() {
        this.playSound(GrappleModClient.SOUND_SLIDE, GrappleModLegacyConfig.getClientConf().sounds.slide_sound_volume);
    }

    public void playDoubleJumpSound() {
        this.playSound(GrappleModClient.SOUND_DOUBLE_JUMP, GrappleModLegacyConfig.getClientConf().sounds.doublejump_sound_volume * 0.7F);
    }

    public void playWallrunJumpSound() {
        this.playSound(GrappleModClient.SOUND_DOUBLE_JUMP, GrappleModLegacyConfig.getClientConf().sounds.wallrunjump_sound_volume * 0.7F);
    }

    public void resetLauncherTime(int playerId) {
        this.getClientControllerManager().resetLauncherTime(playerId);
    }

    public void launchPlayer(Player player) {
        this.getClientControllerManager().launchPlayer(player);
    }

    public void updateRocketRegen(double rocketActiveTime, double rocketRefuelRatio) {
        this.getClientControllerManager().updateRocketRegen(rocketActiveTime, rocketRefuelRatio);
    }

    public double getRocketFunctioning() {
        return this.getClientControllerManager().getRocketFunctioning();
    }

    public boolean isWallRunning(LivingEntity entity, Vec motion) {
        return this.getClientControllerManager().isWallRunning(entity, motion);
    }

    public boolean isSliding(LivingEntity entity, Vec motion) {
        return this.getClientControllerManager().isSliding(entity, motion);
    }

    public double getTimeSinceLastRopeJump(Level world) {
        return world.getGameTime() - ClientPhysicsControllerTracker.prevRopeJumpTime;
    }

    public void resetRopeJumpTime(Level world) {
        ClientPhysicsControllerTracker.prevRopeJumpTime = world.getGameTime();
    }

    public boolean isMovingSlowly(Entity entity) {
        if (entity instanceof LocalPlayer player) {
            return player.isMovingSlowly();
        }

        return false;
    }

    public void playSound(ResourceLocation loc, float volume) {
        Player player = Minecraft.getInstance().player;
        if(player == null) return;

        SimpleSoundInstance sound = new SimpleSoundInstance(
                loc, SoundSource.PLAYERS, volume, 1.0F, RandomSource.create(),
                false, 0,
                SoundInstance.Attenuation.NONE,
                player.getX(), player.getY(), player.getZ(),
                false
        );

        Minecraft.getInstance()
                .getSoundManager()
                .play(sound);
    }

    public int getWallrunTicks() {
        return this.getClientControllerManager().ticksWallRunning;
    }

    public void setWallrunTicks(int newWallrunTicks) {
        this.getClientControllerManager().ticksWallRunning = newWallrunTicks;
    }

    private static int propertyEquipOverride(ItemStack stack, BooleanProperty property) {
        HookCustomization volume = ModItems.GRAPPLING_HOOK.get().getCustomizations(stack);
        return volume.get(property) ? 1 : 0;
    }


    public ClientPhysicsControllerTracker getClientControllerManager() {
        return this.clientPhysicsControllerTracker;
    }

    private static class GrapplehookEntityRenderFactory implements EntityRendererProvider<GrapplinghookEntity> {

        @Override
        @NotNull
        public EntityRenderer<GrapplinghookEntity> create(Context manager) {
            return new GrapplinghookEntityRenderer<>(manager, ModItems.GRAPPLING_HOOK.get());
        }

    }
}
