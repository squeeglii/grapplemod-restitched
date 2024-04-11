package com.yyon.grapplinghook.util.scheduling;

import com.yyon.grapplinghook.GrappleMod;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Supplier;

public class Ticker {

    private final ConcurrentLinkedQueue<TickRunnable> tickingItems = new ConcurrentLinkedQueue<>();

    private int internalTickCount = 0;

    public Ticker() {}


    public void tick() {
        this.internalTickCount++;

        LinkedList<TickRunnable> queue;

        synchronized (this.tickingItems) {
             queue = new LinkedList<>(this.tickingItems);
            this.tickingItems.clear();
        }

        for (TickRunnable item : queue) {
            boolean shouldContinue = item.tryToRun(this.internalTickCount);

            if(shouldContinue) {
                this.tickingItems.add(item);
            }
        }
    }


    public void queue(int delay, Runnable task) {
        this.queueRepeating(delay, -1, () -> {
            task.run();
            return false;
        });
    }

    public void queueRepeating(int delay, int repeatAfter, Supplier<Boolean> task) {
        int runTick = this.internalTickCount + delay;
        TickRunnable runnable = new TickRunnable(runTick, repeatAfter, task);

        this.tickingItems.add(runnable);
    }


    public static Ticker grappleMod() {
        return GrappleMod.get().getTicker();
    }

}
