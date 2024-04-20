package com.yyon.grapplinghook.util.scheduling;

import com.yyon.grapplinghook.GrappleMod;
import net.minecraft.server.MinecraftServer;

import java.util.function.Function;

public class TickRunnable {

    private int tickToRunOn;
    private int repeatInterval;
    private Function<MinecraftServer, Boolean> task;

    protected TickRunnable(int tickToRunOn, int repeatInterval, Function<MinecraftServer, Boolean> task) {
        this.tickToRunOn = tickToRunOn;
        this.repeatInterval = repeatInterval;
        this.task = task;
    }


    /** @return true if the task should continue. false if it should be cancelled. */
    public boolean tryToRun(MinecraftServer server, int currentTick) {
        try {
            if(currentTick != this.getTickToRunOn())
                return true;

            boolean shouldContinue = this.task.apply(server);

            if(!shouldContinue)
                return false;

            this.tickToRunOn += this.repeatInterval;
            return true;

        } catch (Exception err) {
            GrappleMod.LOGGER.error("Error during ticking task. Canceling.", err);
            return false;
        }
    }

    public int getTickToRunOn() {
        return this.tickToRunOn;
    }

    public int getRepeatInterval() {
        return this.repeatInterval;
    }

    public Function<MinecraftServer, Boolean> getTask() {
        return this.task;
    }
}
