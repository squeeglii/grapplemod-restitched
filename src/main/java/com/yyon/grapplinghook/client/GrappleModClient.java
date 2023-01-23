package com.yyon.grapplinghook.client;

import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.block.modifierblock.GuiModifier;
import com.yyon.grapplinghook.block.modifierblock.TileEntityGrappleModifier;
import com.yyon.grapplinghook.client.keybind.GrappleKeys;
import com.yyon.grapplinghook.client.keybind.MCKeys;
import com.yyon.grapplinghook.common.CommonSetup;
import com.yyon.grapplinghook.config.GrappleConfig;
import com.yyon.grapplinghook.controller.GrappleController;
import com.yyon.grapplinghook.item.GrapplehookItem;
import com.yyon.grapplinghook.network.BaseMessageClient;
import com.yyon.grapplinghook.util.GrappleCustomization;
import com.yyon.grapplinghook.util.GrapplemodUtils;
import com.yyon.grapplinghook.util.Vec;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class GrappleModClient implements ClientModInitializer {

    private static GrappleModClient clientInstance;


    private static final ResourceLocation SOUND_DOUBLE_JUMP = new ResourceLocation("grapplemod", "doublejump");
    private static final  ResourceLocation SOUND_SLIDE = new ResourceLocation("grapplemod", "slide");

    private List<ItemStack> grapplingHookVariants = null;


    @Override
    public void onInitializeClient() {
        GrappleModClient.clientInstance = this;
    }

    public static GrappleModClient get() {
        return GrappleModClient.clientInstance;
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

    public void openModifierScreen(TileEntityGrappleModifier tile) {
        Minecraft.getInstance().setScreen(new GuiModifier(tile));
    }

    public void onMessageReceivedClient(BaseMessageClient msg, NetworkEvent.Context ctx) {
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
                if (CommonSetup.grapplingHookItem.get().getCustomization(stack).equals(new GrappleCustomization()))
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
            case key_boththrow -> ClientSetup.key_boththrow.isDown();
            case key_leftthrow -> ClientSetup.key_leftthrow.isDown();
            case key_rightthrow -> ClientSetup.key_rightthrow.isDown();
            case key_motoronoff -> ClientSetup.key_motoronoff.isDown();
            case key_jumpanddetach -> ClientSetup.key_jumpanddetach.isDown();
            case key_slow -> ClientSetup.key_slow.isDown();
            case key_climb -> ClientSetup.key_climb.isDown();
            case key_climbup -> ClientSetup.key_climbup.isDown();
            case key_climbdown -> ClientSetup.key_climbdown.isDown();
            case key_enderlaunch -> ClientSetup.key_enderlaunch.isDown();
            case key_rocket -> ClientSetup.key_rocket.isDown();
            case key_slide -> ClientSetup.key_slide.isDown();
        };
    }

    public GrappleController unregisterController(int entityId) {
        return ClientControllerManager.unregisterController(entityId);
    }

    public double getTimeSinceLastRopeJump(Level world) {
        return GrapplemodUtils.getTime(world) - ClientControllerManager.prevRopeJumpTime;
    }

    public void resetRopeJumpTime(Level world) {
        ClientControllerManager.prevRopeJumpTime = GrapplemodUtils.getTime(world);
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

}
