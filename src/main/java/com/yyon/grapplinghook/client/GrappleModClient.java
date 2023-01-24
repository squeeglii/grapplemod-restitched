package com.yyon.grapplinghook.client;

import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.gui.GrappleModiferBlockGUI;
import com.yyon.grapplinghook.blockentity.GrappleModifierBlockEntity;
import com.yyon.grapplinghook.client.keybind.GrappleKeys;
import com.yyon.grapplinghook.client.keybind.KeyBinding;
import com.yyon.grapplinghook.client.keybind.MCKeys;
import com.yyon.grapplinghook.config.GrappleConfig;
import com.yyon.grapplinghook.controller.AirfrictionController;
import com.yyon.grapplinghook.controller.ForcefieldController;
import com.yyon.grapplinghook.controller.GrappleController;
import com.yyon.grapplinghook.entity.grapplehook.GrapplehookEntity;
import com.yyon.grapplinghook.entity.grapplehook.GrapplehookEntityRenderer;
import com.yyon.grapplinghook.item.GrapplehookItem;
import com.yyon.grapplinghook.network.NetworkContext;
import com.yyon.grapplinghook.network.clientbound.BaseMessageClient;
import com.yyon.grapplinghook.registry.GrappleModEntities;
import com.yyon.grapplinghook.registry.GrappleModItems;
import com.yyon.grapplinghook.util.GrappleCustomization;
import com.yyon.grapplinghook.util.GrappleModUtils;
import com.yyon.grapplinghook.util.Vec;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class GrappleModClient implements ClientModInitializer {

    private static GrappleModClient clientInstance;


    private static final ResourceLocation SOUND_DOUBLE_JUMP = new ResourceLocation("grapplemod", "doublejump");
    private static final  ResourceLocation SOUND_SLIDE = new ResourceLocation("grapplemod", "slide");

    private List<ItemStack> grapplingHookVariants;

    private ClientControllerManager clientControllerManager;


    @Override
    public void onInitializeClient() {
        GrappleModClient.clientInstance = this;
        this.grapplingHookVariants = null;

        //TODO: Handle this with entities how Blocks handle BlockItems in the registry handlers.
        EntityRendererRegistry.register(GrappleModEntities.GRAPPLE_HOOK.get(), new GrapplehookEntityRenderFactory());

        //TODO: Re-implement configuration with ModMenu
        // GrappleModClient.get()::onConfigScreen;

        this.registerPropertyOverride();

        this.clientControllerManager = new ClientControllerManager();
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
            return (ClientControllerManager.controllers.containsKey(entity.getId()) && !(ClientControllerManager.controllers.get(entity.getId()) instanceof AirfrictionController)) ? 1 : 0;
        });
        ItemProperties.register(GrappleModItems.FORCE_FIELD.get(), new ResourceLocation("attached"), (stack, world, entity, seed) -> {
            if (entity == null) return 0;
            return (ClientControllerManager.controllers.containsKey(entity.getId()) && ClientControllerManager.controllers.get(entity.getId()) instanceof ForcefieldController) ? 1 : 0;
        });
        ItemProperties.register(GrappleModItems.GRAPPLING_HOOK.get(), new ResourceLocation("hook"), (stack, world, entity, seed) -> GrappleModItems.GRAPPLING_HOOK.get().getPropertyHook(stack, world, entity) ? 1 : 0);
    }


    public void startRocket(Player player, GrappleCustomization custom) {
        ClientControllerManager.instance.startRocket(player, custom);
    }

    public String getKeyname(MCKeys keyEnum) {
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
        Minecraft.getInstance().setScreen(new GrappleModiferBlockGUI(tile));
    }

    public void onMessageReceivedClient(BaseMessageClient msg, NetworkContext ctx) {
        msg.processMessage(ctx);
    }


    public void playSlideSound() {
        this.playSound(GrappleModClient.SOUND_SLIDE, GrappleConfig.getClientConf().sounds.slide_sound_volume);
    }

    public void playDoubleJumpSound() {
        this.playSound(GrappleModClient.SOUND_DOUBLE_JUMP, GrappleConfig.getClientConf().sounds.doublejump_sound_volume * 0.7F);
    }

    public void playWallrunJumpSound() {
        this.playSound(GrappleModClient.SOUND_DOUBLE_JUMP, GrappleConfig.getClientConf().sounds.wallrunjump_sound_volume * 0.7F);
    }

    public void fillGrappleVariants(NonNullList<ItemStack> items) {
        if (!Minecraft.getInstance().isRunning() || Minecraft.getInstance().player == null) return;


        if (grapplingHookVariants == null) {
            grapplingHookVariants = new ArrayList<>();

            RecipeManager recipeManager = Minecraft.getInstance().player.level.getRecipeManager();
            recipeManager.getRecipeIds().filter(loc -> loc.getNamespace().equals(GrappleMod.MODID)).forEach(loc -> {
                Optional<? extends Recipe<?>> recipe = recipeManager.byKey(loc);
                if(recipe.isEmpty()) return;

                ItemStack stack = recipe.get().getResultItem();

                if (!(stack.getItem() instanceof GrapplehookItem)) return;
                if (GrappleModItems.GRAPPLING_HOOK.get().getCustomization(stack).equals(new GrappleCustomization()))
                    return;

                grapplingHookVariants.add(stack);
            });
        }

        items.addAll(grapplingHookVariants);
    }

    public Screen onConfigScreen(Screen screen) {
        return AutoConfig.getConfigScreen(GrappleConfig.class, screen).get();
    }

    public void resetLauncherTime(int playerId) {
        ClientControllerManager.instance.resetLauncherTime(playerId);
    }

    public void launchPlayer(Player player) {
        ClientControllerManager.instance.launchPlayer(player);
    }

    public void updateRocketRegen(double rocketActiveTime, double rocketRefuelRatio) {
        ClientControllerManager.instance.updateRocketRegen(rocketActiveTime, rocketRefuelRatio);
    }

    public double getRocketFunctioning() {
        return ClientControllerManager.instance.getRocketFunctioning();
    }

    public boolean isWallRunning(Entity entity, Vec motion) {
        return ClientControllerManager.instance.isWallRunning(entity, motion);
    }

    public boolean isSliding(Entity entity, Vec motion) {
        return ClientControllerManager.instance.isSliding(entity, motion);
    }

    public GrappleController createControl(int id, int hookEntityId, int entityId, Level world, Vec pos, BlockPos blockpos, GrappleCustomization custom) {
        return ClientControllerManager.instance.createControl(id, hookEntityId, entityId, world, blockpos, custom);
    }

    public boolean isKeyDown(GrappleKeys key) {
        return switch (key) {
            case key_boththrow -> KeyBinding.key_boththrow.isDown();
            case key_leftthrow -> KeyBinding.key_leftthrow.isDown();
            case key_rightthrow -> KeyBinding.key_rightthrow.isDown();
            case key_motoronoff -> KeyBinding.key_motoronoff.isDown();
            case key_jumpanddetach -> KeyBinding.key_jumpanddetach.isDown();
            case key_slow -> KeyBinding.key_slow.isDown();
            case key_climb -> KeyBinding.key_climb.isDown();
            case key_climbup -> KeyBinding.key_climbup.isDown();
            case key_climbdown -> KeyBinding.key_climbdown.isDown();
            case key_enderlaunch -> KeyBinding.key_enderlaunch.isDown();
            case key_rocket -> KeyBinding.key_rocket.isDown();
            case key_slide -> KeyBinding.key_slide.isDown();
        };
    }

    public GrappleController unregisterController(int entityId) {
        return ClientControllerManager.unregisterController(entityId);
    }

    public double getTimeSinceLastRopeJump(Level world) {
        return GrappleModUtils.getTime(world) - ClientControllerManager.prevRopeJumpTime;
    }

    public void resetRopeJumpTime(Level world) {
        ClientControllerManager.prevRopeJumpTime = GrappleModUtils.getTime(world);
    }

    public boolean isKeyDown(MCKeys keyEnum) {

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
        return ClientControllerManager.instance.ticksWallRunning;
    }

    public void setWallrunTicks(int newWallrunTicks) {
        ClientControllerManager.instance.ticksWallRunning = newWallrunTicks;
    }


    public ClientControllerManager getClientControllerManager() {
        return clientControllerManager;
    }

    private static class GrapplehookEntityRenderFactory implements EntityRendererProvider<GrapplehookEntity> {

        @Override
        @NotNull
        public EntityRenderer<GrapplehookEntity> create(Context manager) {
            return new GrapplehookEntityRenderer<>(manager, GrappleModItems.GRAPPLING_HOOK.get());
        }

    }
}
