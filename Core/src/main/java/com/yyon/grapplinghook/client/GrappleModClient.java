package com.yyon.grapplinghook.client;

import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.client.gui.screen.LegacyGrappleModifierBlockScreen;
import com.yyon.grapplinghook.client.physics.ClientPhysicsControllerTracker;
import com.yyon.grapplinghook.client.physics.context.AirFrictionPhysicsController;
import com.yyon.grapplinghook.client.physics.context.ForcefieldPhysicsController;
import com.yyon.grapplinghook.client.render.entity.GrapplinghookEntityRenderer;
import com.yyon.grapplinghook.config.GrappleModClientConfig;
import com.yyon.grapplinghook.content.blockentity.GrappleModifierBlockEntity;
import com.yyon.grapplinghook.content.entity.grapplinghook.GrapplinghookEntity;
import com.yyon.grapplinghook.content.registry.internal.ModEntities;
import com.yyon.grapplinghook.content.registry.internal.ModEntityLayerIdentifiers;
import com.yyon.grapplinghook.content.registry.internal.ModItems;
import com.yyon.grapplinghook.content.customization.data.HookCustomization;
import com.yyon.grapplinghook.content.customization.type.BooleanProperty;
import com.yyon.grapplinghook.content.registry.internal.ModMenuScreens;
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


    private static final ResourceLocation SOUND_DOUBLE_JUMP = GrappleMod.id("doublejump");
    private static final ResourceLocation SOUND_SLIDE = GrappleMod.id("slide");

    private ClientPhysicsControllerTracker clientPhysicsControllerTracker;


    @Override
    public void onInitializeClient() {
        GrappleModClient.clientInstance = this;

        try {
            this.initConfig();
        } catch (Exception e) {
            GrappleMod.LOGGER.info(e);
        }

        EntityRendererRegistry.register(ModEntities.GRAPPLE_HOOK.get(), new GrapplehookEntityRenderFactory());

        ModKeys.registerAll();
        ModEntityLayerIdentifiers.registerAll();
        ModMenuScreens.registerAll();

        this.clientPhysicsControllerTracker = new ClientPhysicsControllerTracker();
        this.registerPropertyOverride();
        this.registerResourcePacks();
    }

    public static GrappleModClient get() {
        return GrappleModClient.clientInstance;
    }

    public void initConfig() {
        GrappleModClientConfig.HANDLER.defaults().saveDefaults();
        GrappleModClientConfig.HANDLER.load();

        //todo: reload creative tabs on save / load.
    }

    public void registerPropertyOverride() {
        ItemProperties.register(ModItems.GRAPPLING_HOOK.get(), GrappleMod.vanillaId("rocket"), (stack, world, entity, seed) -> propertyEquipOverride(stack, ROCKET_ATTACHED.get()));
        ItemProperties.register(ModItems.GRAPPLING_HOOK.get(), GrappleMod.vanillaId("double"), (stack, world, entity, seed) -> propertyEquipOverride(stack, DOUBLE_HOOK_ATTACHED.get()));
        ItemProperties.register(ModItems.GRAPPLING_HOOK.get(), GrappleMod.vanillaId("motor"), (stack, world, entity, seed) -> propertyEquipOverride(stack, MOTOR_ATTACHED.get()));
        ItemProperties.register(ModItems.GRAPPLING_HOOK.get(), GrappleMod.vanillaId("smart"), (stack, world, entity, seed) -> propertyEquipOverride(stack, SMART_MOTOR.get()));
        ItemProperties.register(ModItems.GRAPPLING_HOOK.get(), GrappleMod.vanillaId("enderstaff"), (stack, world, entity, seed) -> propertyEquipOverride(stack, ENDER_STAFF_ATTACHED.get()));
        ItemProperties.register(ModItems.GRAPPLING_HOOK.get(), GrappleMod.vanillaId("magnet"), (stack, world, entity, seed) -> propertyEquipOverride(stack, MAGNET_ATTACHED.get()));
        ItemProperties.register(ModItems.GRAPPLING_HOOK.get(), GrappleMod.vanillaId("attached"), (stack, world, entity, seed) -> {
            if (entity == null) return 0;
            return (this.getClientControllerManager().controllers.containsKey(entity.getId()) && !(this.getClientControllerManager().controllers.get(entity.getId()) instanceof AirFrictionPhysicsController)) ? 1 : 0;
        });
        ItemProperties.register(ModItems.FORCE_FIELD.get(), GrappleMod.vanillaId("attached"), (stack, world, entity, seed) -> {
            if (entity == null) return 0;
            return (this.getClientControllerManager().controllers.containsKey(entity.getId()) && this.getClientControllerManager().controllers.get(entity.getId()) instanceof ForcefieldPhysicsController) ? 1 : 0;
        });
        ItemProperties.register(ModItems.GRAPPLING_HOOK.get(), GrappleMod.vanillaId("hook"), (stack, world, entity, seed) -> ModItems.GRAPPLING_HOOK.get().shouldDisplayAsHookOnly(stack) ? 1 : 0);
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


    public void startRocket(Player player, HookCustomization custom) {
        this.getClientControllerManager().startRocket(player, custom);
    }

    public void openModifierScreen(GrappleModifierBlockEntity tile) {
        Minecraft.getInstance().setScreen(new LegacyGrappleModifierBlockScreen(tile));
    }


    public void playSlideSound() {
        this.playSound(GrappleModClient.SOUND_SLIDE, GrappleModClientConfig.get().getSlideVolume());
    }

    public void playDoubleJumpSound() {
        this.playSound(GrappleModClient.SOUND_DOUBLE_JUMP, GrappleModClientConfig.get().getDoubleJumpVolume() * 0.7F);
    }

    public void playWallrunJumpSound() {
        this.playSound(GrappleModClient.SOUND_DOUBLE_JUMP, GrappleModClientConfig.get().getWallrunJumpVolume() * 0.7F);
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

    public long getTimeSinceLastRopeJump(Level world) {
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
        HookCustomization volume = ModItems.GRAPPLING_HOOK.get().getCustomizationsOrDefault(stack);
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
