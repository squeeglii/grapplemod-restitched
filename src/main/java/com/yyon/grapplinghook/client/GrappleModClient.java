package com.yyon.grapplinghook.client;

import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.content.blockentity.GrappleModifierBlockEntity;
import com.yyon.grapplinghook.client.keybind.GrappleModKey;
import com.yyon.grapplinghook.client.keybind.MinecraftKey;
import com.yyon.grapplinghook.client.gui.GrappleModifierBlockGUI;
import com.yyon.grapplinghook.client.keybind.KeyBindingManagement;
import com.yyon.grapplinghook.config.GrappleModConfig;
import com.yyon.grapplinghook.physics.context.AirFrictionPhysicsContext;
import com.yyon.grapplinghook.physics.context.ForcefieldPhysicsContext;
import com.yyon.grapplinghook.physics.context.GrapplingHookPhysicsContext;
import com.yyon.grapplinghook.content.entity.grapplinghook.GrapplinghookEntity;
import com.yyon.grapplinghook.content.entity.grapplinghook.GrapplinghookEntityRenderer;
import com.yyon.grapplinghook.network.NetworkContext;
import com.yyon.grapplinghook.network.NetworkManager;
import com.yyon.grapplinghook.network.clientbound.BaseMessageClient;
import com.yyon.grapplinghook.content.registry.GrappleModEntities;
import com.yyon.grapplinghook.content.registry.GrappleModEntityRenderLayers;
import com.yyon.grapplinghook.content.registry.GrappleModItems;
import com.yyon.grapplinghook.customization.GrappleCustomization;
import com.yyon.grapplinghook.util.GrappleModUtils;
import com.yyon.grapplinghook.util.Vec;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
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
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@Environment(EnvType.CLIENT)
public class GrappleModClient implements ClientModInitializer {

    private static GrappleModClient clientInstance;


    private static final ResourceLocation SOUND_DOUBLE_JUMP = new ResourceLocation("grapplemod", "doublejump");
    private static final  ResourceLocation SOUND_SLIDE = new ResourceLocation("grapplemod", "slide");

    private ClientPhysicsContextTracker clientPhysicsContextTracker;


    @Override
    public void onInitializeClient() {
        GrappleModClient.clientInstance = this;
        ClientCheck.clientLoaded = true;

        EntityRendererRegistry.register(GrappleModEntities.GRAPPLE_HOOK.get(), new GrapplehookEntityRenderFactory());

        KeyBindingManagement.registerAll();
        GrappleModEntityRenderLayers.registerAll();

        NetworkManager.registerClientPacketListeners();

        this.clientPhysicsContextTracker = new ClientPhysicsContextTracker();
        this.registerPropertyOverride();
        this.registerResourcePacks();
    }

    public static GrappleModClient get() {
        return GrappleModClient.clientInstance;
    }



    public void registerPropertyOverride() {
        ItemProperties.register(GrappleModItems.GRAPPLING_HOOK.get(), new ResourceLocation("rocket"), (stack, world, entity, seed) -> GrappleModItems.GRAPPLING_HOOK.get().getPropertyRocket(stack, world, entity) ? 1 : 0);
        ItemProperties.register(GrappleModItems.GRAPPLING_HOOK.get(), new ResourceLocation("double"), (stack, world, entity, seed) -> GrappleModItems.GRAPPLING_HOOK.get().getPropertyDouble(stack, world, entity) ? 1 : 0);
        ItemProperties.register(GrappleModItems.GRAPPLING_HOOK.get(), new ResourceLocation("motor"), (stack, world, entity, seed) -> GrappleModItems.GRAPPLING_HOOK.get().getPropertyMotor(stack, world, entity) ? 1 : 0);
        ItemProperties.register(GrappleModItems.GRAPPLING_HOOK.get(), new ResourceLocation("smart"), (stack, world, entity, seed) -> GrappleModItems.GRAPPLING_HOOK.get().getPropertySmart(stack, world, entity) ? 1 : 0);
        ItemProperties.register(GrappleModItems.GRAPPLING_HOOK.get(), new ResourceLocation("enderstaff"), (stack, world, entity, seed) -> GrappleModItems.GRAPPLING_HOOK.get().getPropertyEnderstaff(stack, world, entity) ? 1 : 0);
        ItemProperties.register(GrappleModItems.GRAPPLING_HOOK.get(), new ResourceLocation("magnet"), (stack, world, entity, seed) -> GrappleModItems.GRAPPLING_HOOK.get().getPropertyMagnet(stack, world, entity) ? 1 : 0);
        ItemProperties.register(GrappleModItems.GRAPPLING_HOOK.get(), new ResourceLocation("attached"), (stack, world, entity, seed) -> {
            if (entity == null) return 0;
            return (ClientPhysicsContextTracker.controllers.containsKey(entity.getId()) && !(ClientPhysicsContextTracker.controllers.get(entity.getId()) instanceof AirFrictionPhysicsContext)) ? 1 : 0;
        });
        ItemProperties.register(GrappleModItems.FORCE_FIELD.get(), new ResourceLocation("attached"), (stack, world, entity, seed) -> {
            if (entity == null) return 0;
            return (ClientPhysicsContextTracker.controllers.containsKey(entity.getId()) && ClientPhysicsContextTracker.controllers.get(entity.getId()) instanceof ForcefieldPhysicsContext) ? 1 : 0;
        });
        ItemProperties.register(GrappleModItems.GRAPPLING_HOOK.get(), new ResourceLocation("hook"), (stack, world, entity, seed) -> GrappleModItems.GRAPPLING_HOOK.get().getPropertyHook(stack, world, entity) ? 1 : 0);
    }

    public void registerResourcePacks() {
        Optional<ModContainer> cont = FabricLoader.getInstance().getModContainer(GrappleMod.MODID);

        if(cont.isEmpty()) {
            GrappleMod.LOGGER.error("Unable to register resource packs! This mod technically doesn't exist!!");
            return;
        }

        ModContainer container = cont.get();

        GrappleModUtils.registerPack("original_textures", Component.translatable("pack.grapplemod.original"), container, ResourcePackActivationType.NORMAL);
    }


    public void startRocket(Player player, GrappleCustomization custom) {
        ClientPhysicsContextTracker.instance.startRocket(player, custom);
    }

    public String getKeyname(MinecraftKey keyEnum) {
        Options gs = Minecraft.getInstance().options;

        KeyMapping binding = switch (keyEnum) {
            case keyBindUseItem -> gs.keyUse;
            case keyBindForward -> gs.keyUp;
            case keyBindLeft -> gs.keyLeft;
            case keyBindBack -> gs.keyDown;
            case keyBindRight -> gs.keyRight;
            case keyBindJump -> gs.keyJump;
            case keyBindSneak -> gs.keyShift;
            case keyBindAttack -> gs.keyAttack;
        };

        String displayName = binding.getTranslatedKeyMessage().getString();
        return switch (displayName) {
            case "Button 1" -> "Left Click";
            case "Button 2" -> "Right Click";
            default -> displayName;
        };
    }

    public void openModifierScreen(GrappleModifierBlockEntity tile) {
        Minecraft.getInstance().setScreen(new GrappleModifierBlockGUI(tile));
    }

    public void onMessageReceivedClient(BaseMessageClient msg, NetworkContext ctx) {
        msg.processMessage(ctx);
    }


    public void playSlideSound() {
        this.playSound(GrappleModClient.SOUND_SLIDE, GrappleModConfig.getClientConf().sounds.slide_sound_volume);
    }

    public void playDoubleJumpSound() {
        this.playSound(GrappleModClient.SOUND_DOUBLE_JUMP, GrappleModConfig.getClientConf().sounds.doublejump_sound_volume * 0.7F);
    }

    public void playWallrunJumpSound() {
        this.playSound(GrappleModClient.SOUND_DOUBLE_JUMP, GrappleModConfig.getClientConf().sounds.wallrunjump_sound_volume * 0.7F);
    }

    public void resetLauncherTime(int playerId) {
        ClientPhysicsContextTracker.instance.resetLauncherTime(playerId);
    }

    public void launchPlayer(Player player) {
        ClientPhysicsContextTracker.instance.launchPlayer(player);
    }

    public void updateRocketRegen(double rocketActiveTime, double rocketRefuelRatio) {
        ClientPhysicsContextTracker.instance.updateRocketRegen(rocketActiveTime, rocketRefuelRatio);
    }

    public double getRocketFunctioning() {
        return ClientPhysicsContextTracker.instance.getRocketFunctioning();
    }

    public boolean isWallRunning(Entity entity, Vec motion) {
        return ClientPhysicsContextTracker.instance.isWallRunning(entity, motion);
    }

    public boolean isSliding(Entity entity, Vec motion) {
        return ClientPhysicsContextTracker.instance.isSliding(entity, motion);
    }

    public GrapplingHookPhysicsContext createControl(int id, int hookEntityId, int entityId, Level world, Vec pos, BlockPos blockpos, GrappleCustomization custom) {
        return ClientPhysicsContextTracker.instance.createControl(id, hookEntityId, entityId, world, blockpos, custom);
    }

    public boolean isKeyDown(GrappleModKey key) {
        return switch (key) {
            case key_boththrow -> KeyBindingManagement.key_boththrow.isDown();
            case key_leftthrow -> KeyBindingManagement.key_leftthrow.isDown();
            case key_rightthrow -> KeyBindingManagement.key_rightthrow.isDown();
            case key_motoronoff -> KeyBindingManagement.key_motoronoff.isDown();
            case key_jumpanddetach -> KeyBindingManagement.key_jumpanddetach.isDown();
            case key_slow -> KeyBindingManagement.key_slow.isDown();
            case key_climb -> KeyBindingManagement.key_climb.isDown();
            case key_climbup -> KeyBindingManagement.key_climbup.isDown();
            case key_climbdown -> KeyBindingManagement.key_climbdown.isDown();
            case key_enderlaunch -> KeyBindingManagement.key_enderlaunch.isDown();
            case key_rocket -> KeyBindingManagement.key_rocket.isDown();
            case key_slide -> KeyBindingManagement.key_slide.isDown();
        };
    }

    public GrapplingHookPhysicsContext unregisterController(int entityId) {
        return ClientPhysicsContextTracker.unregisterController(entityId);
    }

    public double getTimeSinceLastRopeJump(Level world) {
        return GrappleModUtils.getTime(world) - ClientPhysicsContextTracker.prevRopeJumpTime;
    }

    public void resetRopeJumpTime(Level world) {
        ClientPhysicsContextTracker.prevRopeJumpTime = GrappleModUtils.getTime(world);
    }

    public boolean isKeyDown(MinecraftKey keyEnum) {

        Options options = Minecraft.getInstance().options;

        return switch (keyEnum) {
            case keyBindUseItem ->  options.keyUse.isDown();
            case keyBindForward -> options.keyUp.isDown();
            case keyBindLeft -> options.keyLeft.isDown();
            case keyBindBack -> options.keyDown.isDown();
            case keyBindRight -> options.keyRight.isDown();
            case keyBindJump -> options.keyJump.isDown();
            case keyBindSneak -> options.keyShift.isDown();
            case keyBindAttack -> options.keyAttack.isDown();
        };
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
        return ClientPhysicsContextTracker.instance.ticksWallRunning;
    }

    public void setWallrunTicks(int newWallrunTicks) {
        ClientPhysicsContextTracker.instance.ticksWallRunning = newWallrunTicks;
    }


    public ClientPhysicsContextTracker getClientControllerManager() {
        return clientPhysicsContextTracker;
    }

    private static class GrapplehookEntityRenderFactory implements EntityRendererProvider<GrapplinghookEntity> {

        @Override
        @NotNull
        public EntityRenderer<GrapplinghookEntity> create(Context manager) {
            return new GrapplinghookEntityRenderer<>(manager, GrappleModItems.GRAPPLING_HOOK.get());
        }

    }
}
