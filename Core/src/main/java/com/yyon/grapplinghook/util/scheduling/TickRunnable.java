package com.yyon.grapplinghook.util.scheduling;

import com.yyon.grapplinghook.GrappleMod;

import java.util.function.Supplier;

public class TickRunnable {

    private int tickToRunOn;
    private int repeatInterval;
    private Supplier<Boolean> task;

    protected TickRunnable(int tickToRunOn, int repeatInterval, Supplier<Boolean> task) {
        this.tickToRunOn = tickToRunOn;
        this.repeatInterval = repeatInterval;
        this.task = task;
    }


    /** @return true if the task should continue. false if it should be cancelled. */
    public boolean tryToRun(int currentTick) {
        try {
            if(currentTick != this.getTickToRunOn())
                return true;

            boolean shouldContinue = this.task.get();

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

    public Supplier<Boolean> getTask() {
        return this.task;
    }
}
