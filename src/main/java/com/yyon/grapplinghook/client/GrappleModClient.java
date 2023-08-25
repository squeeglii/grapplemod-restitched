package com.yyon.grapplinghook.client;

import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.content.blockentity.GrappleModifierBlockEntity;
import com.yyon.grapplinghook.client.gui.GrappleModifierBlockGUI;
import com.yyon.grapplinghook.client.keybind.GrappleKey;
import com.yyon.grapplinghook.config.GrappleModLegacyConfig;
import com.yyon.grapplinghook.customization.type.BooleanProperty;
import com.yyon.grapplinghook.physics.context.AirFrictionPhysicsController;
import com.yyon.grapplinghook.physics.context.ForcefieldPhysicsController;
import com.yyon.grapplinghook.physics.context.GrapplingHookPhysicsController;
import com.yyon.grapplinghook.content.entity.grapplinghook.GrapplinghookEntity;
import com.yyon.grapplinghook.content.entity.grapplinghook.GrapplinghookEntityRenderer;
import com.yyon.grapplinghook.network.NetworkContext;
import com.yyon.grapplinghook.network.NetworkManager;
import com.yyon.grapplinghook.network.clientbound.BaseMessageClient;
import com.yyon.grapplinghook.content.registry.GrappleModEntities;
import com.yyon.grapplinghook.content.registry.GrappleModEntityRenderLayers;
import com.yyon.grapplinghook.content.registry.GrappleModItems;
import com.yyon.grapplinghook.customization.CustomizationVolume;
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
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

import static com.yyon.grapplinghook.content.registry.GrappleModCustomizationProperties.*;

@Environment(EnvType.CLIENT)
public class GrappleModClient implements ClientModInitializer {

    private static GrappleModClient clientInstance;


    private static final ResourceLocation SOUND_DOUBLE_JUMP = new ResourceLocation("grapplemod", "doublejump");
    private static final  ResourceLocation SOUND_SLIDE = new ResourceLocation("grapplemod", "slide");

    private ClientPhysicsControllerTracker clientPhysicsControllerTracker;


    @Override
    public void onInitializeClient() {
        GrappleModClient.clientInstance = this;

        EntityRendererRegistry.register(GrappleModEntities.GRAPPLE_HOOK.get(), new GrapplehookEntityRenderFactory());

        GrappleKey.registerAll();
        GrappleModEntityRenderLayers.registerAll();

        NetworkManager.registerClientPacketListeners();

        this.clientPhysicsControllerTracker = new ClientPhysicsControllerTracker();
        this.registerPropertyOverride();
        this.registerResourcePacks();
    }

    public static GrappleModClient get() {
        return GrappleModClient.clientInstance;
    }



    public void registerPropertyOverride() {
        ItemProperties.register(GrappleModItems.GRAPPLING_HOOK.get(), new ResourceLocation("rocket"), (stack, world, entity, seed) -> propertyEquipOverride(stack, ROCKET_ATTACHED.get()));
        ItemProperties.register(GrappleModItems.GRAPPLING_HOOK.get(), new ResourceLocation("double"), (stack, world, entity, seed) -> propertyEquipOverride(stack, DOUBLE_HOOK_ATTACHED.get()));
        ItemProperties.register(GrappleModItems.GRAPPLING_HOOK.get(), new ResourceLocation("motor"), (stack, world, entity, seed) -> propertyEquipOverride(stack, MOTOR_ATTACHED.get()));
        ItemProperties.register(GrappleModItems.GRAPPLING_HOOK.get(), new ResourceLocation("smart"), (stack, world, entity, seed) -> propertyEquipOverride(stack, SMART_MOTOR.get()));
        ItemProperties.register(GrappleModItems.GRAPPLING_HOOK.get(), new ResourceLocation("enderstaff"), (stack, world, entity, seed) -> propertyEquipOverride(stack, ENDER_STAFF_ATTACHED.get()));
        ItemProperties.register(GrappleModItems.GRAPPLING_HOOK.get(), new ResourceLocation("magnet"), (stack, world, entity, seed) -> propertyEquipOverride(stack, MAGNET_ATTACHED.get()));
        ItemProperties.register(GrappleModItems.GRAPPLING_HOOK.get(), new ResourceLocation("attached"), (stack, world, entity, seed) -> {
            if (entity == null) return 0;
            return (ClientPhysicsControllerTracker.controllers.containsKey(entity.getId()) && !(ClientPhysicsControllerTracker.controllers.get(entity.getId()) instanceof AirFrictionPhysicsController)) ? 1 : 0;
        });
        ItemProperties.register(GrappleModItems.FORCE_FIELD.get(), new ResourceLocation("attached"), (stack, world, entity, seed) -> {
            if (entity == null) return 0;
            return (ClientPhysicsControllerTracker.controllers.containsKey(entity.getId()) && ClientPhysicsControllerTracker.controllers.get(entity.getId()) instanceof ForcefieldPhysicsController) ? 1 : 0;
        });
        ItemProperties.register(GrappleModItems.GRAPPLING_HOOK.get(), new ResourceLocation("hook"), (stack, world, entity, seed) -> GrappleModItems.GRAPPLING_HOOK.get().getPropertyHook(stack) ? 1 : 0);
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


    public void startRocket(Player player, CustomizationVolume custom) {
        ClientPhysicsControllerTracker.instance.startRocket(player, custom);
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
        ClientPhysicsControllerTracker.instance.resetLauncherTime(playerId);
    }

    public void launchPlayer(Player player) {
        ClientPhysicsControllerTracker.instance.launchPlayer(player);
    }

    public void updateRocketRegen(double rocketActiveTime, double rocketRefuelRatio) {
        ClientPhysicsControllerTracker.instance.updateRocketRegen(rocketActiveTime, rocketRefuelRatio);
    }

    public double getRocketFunctioning() {
        return ClientPhysicsControllerTracker.instance.getRocketFunctioning();
    }

    public boolean isWallRunning(Entity entity, Vec motion) {
        return ClientPhysicsControllerTracker.instance.isWallRunning(entity, motion);
    }

    public boolean isSliding(Entity entity, Vec motion) {
        return ClientPhysicsControllerTracker.instance.isSliding(entity, motion);
    }

    public GrapplingHookPhysicsController createControl(int id, int hookEntityId, int entityId, Level world, Vec pos, BlockPos blockpos, CustomizationVolume custom) {
        return ClientPhysicsControllerTracker.instance.createControl(id, hookEntityId, entityId, world, blockpos, custom);
    }

    public GrapplingHookPhysicsController unregisterController(int entityId) {
        return ClientPhysicsControllerTracker.unregisterController(entityId);
    }

    public double getTimeSinceLastRopeJump(Level world) {
        return GrappleModUtils.getTime(world) - ClientPhysicsControllerTracker.prevRopeJumpTime;
    }

    public void resetRopeJumpTime(Level world) {
        ClientPhysicsControllerTracker.prevRopeJumpTime = GrappleModUtils.getTime(world);
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

        Minecraft.getInstance().getSoundManager().play(new SimpleSoundInstance(loc, SoundSource.PLAYERS, volume, 1.0F, RandomSource.create(),false, 0, SoundInstance.Attenuation.NONE, player.getX(), player.getY(), player.getZ(), false));
    }

    public int getWallrunTicks() {
        return ClientPhysicsControllerTracker.instance.ticksWallRunning;
    }

    public void setWallrunTicks(int newWallrunTicks) {
        ClientPhysicsControllerTracker.instance.ticksWallRunning = newWallrunTicks;
    }

    private static int propertyEquipOverride(ItemStack stack, BooleanProperty property) {
        CustomizationVolume volume = GrappleModItems.GRAPPLING_HOOK.get().getCustomization(stack);
        return volume.get(property) ? 1 : 0;
    }


    public ClientPhysicsControllerTracker getClientControllerManager() {
        return clientPhysicsControllerTracker;
    }

    private static class GrapplehookEntityRenderFactory implements EntityRendererProvider<GrapplinghookEntity> {

        @Override
        @NotNull
        public EntityRenderer<GrapplinghookEntity> create(Context manager) {
            return new GrapplinghookEntityRenderer<>(manager, GrappleModItems.GRAPPLING_HOOK.get());
        }

    }
}
