package com.yyon.grapplinghook.client;

import com.yyon.grapplinghook.common.CommonSetup;
import com.yyon.grapplinghook.config.GrappleConfig;
import com.yyon.grapplinghook.controller.AirfrictionController;
import com.yyon.grapplinghook.controller.ForcefieldController;
import com.yyon.grapplinghook.controller.GrappleController;
import com.yyon.grapplinghook.item.KeypressItem;
import com.yyon.grapplinghook.util.Vec;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.Input;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent.LoggingOut;
import net.minecraftforge.client.event.InputEvent.Key;
import net.minecraftforge.client.event.MovementInputUpdateEvent;
import net.minecraftforge.client.event.ViewportEvent.ComputeCameraAngles;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.BlockEvent.BreakEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ClientEventHandlers {
	public static ClientEventHandlers instance = null;
	
	public ClientEventHandlers() {
	    MinecraftForge.EVENT_BUS.register(this);
	}

	
	@SubscribeEvent(priority=EventPriority.LOW)
    public void onInputUpdate(MovementInputUpdateEvent event) {
		Player player = Minecraft.getInstance().player;
		if (!Minecraft.getInstance().isRunning() || player == null) {
			return;
		}
		
		int id = player.getId();
		if (ClientControllerManager.controllers.containsKey(id)) {
			Input input = event.getInput();
			GrappleController control = ClientControllerManager.controllers.get(id);
			control.receivePlayerMovementMessage(input.leftImpulse, input.forwardImpulse, input.jumping, input.shiftKeyDown);
			
			boolean overrideMovement = true;
			if (Minecraft.getInstance().player.isOnGround()) {
				if (!(control instanceof AirfrictionController) && !(control instanceof ForcefieldController)) {
					overrideMovement = false;
				}
			}
			
			if (overrideMovement) {
				input.jumping = false;
				input.down = false;
				input.up = false;
				input.left = false;
				input.right = false;
				input.forwardImpulse = 0;
				input.leftImpulse = 0;
//				input.sneak = false; // fix alternate throw angles
			}
		}
	}
	
	public float currentCameraTilt = 0;

	@SubscribeEvent
	public void onCameraSetup(ComputeCameraAngles event) {
		Player player = Minecraft.getInstance().player;
		if (!Minecraft.getInstance().isRunning() || player == null) {
			return;
		}

		int id = player.getId();
		int targetCameraTilt = 0;
		if (ClientControllerManager.controllers.containsKey(id)) {
			GrappleController controller = ClientControllerManager.controllers.get(id);
			if (controller instanceof AirfrictionController) {
				AirfrictionController afcontroller = (AirfrictionController) controller;
				if (afcontroller.wasWallrunning) {
					Vec walldirection = afcontroller.getWallDirection();
					if (walldirection != null) {
						Vec lookdirection = Vec.lookVec(player);
						int dir = lookdirection.cross(walldirection).y > 0 ? 1 : -1;
						targetCameraTilt = dir;
					}
				}
			}
		}
		
		if (currentCameraTilt != targetCameraTilt) {
			float cameraDiff = targetCameraTilt - currentCameraTilt;
			if (cameraDiff != 0) {
				float anim_s = GrappleConfig.getClientConf().camera.wallrun_camera_animation_s;
				float speed = (anim_s == 0) ? 9999 :  1.0f / (anim_s * 20.0f);
				if (speed > Math.abs(cameraDiff)) {
					currentCameraTilt = targetCameraTilt;
				} else {
					currentCameraTilt += speed * (cameraDiff > 0 ? 1 : -1);
				}
			}
		}
		
		if (currentCameraTilt != 0) {
		    event.setRoll(event.getRoll() + currentCameraTilt*GrappleConfig.getClientConf().camera.wallrun_camera_tilt_degrees);
		}
	}
}
