package com.yyon.grapplinghook.physics.io;

import com.yyon.grapplinghook.content.entity.grapplinghook.GrapplinghookEntity;
import com.yyon.grapplinghook.util.NBTHelper;
import com.yyon.grapplinghook.util.Vec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

public class HookSnapshot {

    private final Vec hookPos;
    private final RopeSnapshot segmentHandler;


    public HookSnapshot(GrapplinghookEntity source) {
        this.hookPos = new Vec(source.position());
        this.segmentHandler = new RopeSnapshot(source.getSegmentHandler());
    }

    public HookSnapshot(CompoundTag source) {
        if(!isTagValid(source))
            throw new IllegalArgumentException("Tag passed is missing required data for hook.");

        ListTag posTag = source.getList("Pos", ListTag.TAG_DOUBLE);
        CompoundTag ropeSegHandlerTag = source.getCompound("RopeShape");

        this.hookPos = new Vec(posTag);
        this.segmentHandler = new RopeSnapshot(ropeSegHandlerTag);
    }


    public CompoundTag saveToNBT() {
        CompoundTag hookData = new CompoundTag();

        ListTag hookPos = NBTHelper.newDoubleList(this.getX(), this.getY(), this.getZ());
        CompoundTag ropeShape = this.segmentHandler.toNBT();

        hookData.put("Pos", hookPos);
        hookData.put("RopeShape", ropeShape);

        return hookData;
    }


    public double getX() {
        return this.hookPos.x;
    }

    public double getY() {
        return this.hookPos.y;
    }

    public double getZ() {
        return this.hookPos.z;
    }


    public static boolean isTagValid(CompoundTag tag) {
        if(tag == null) return false;

        if(!tag.contains("Pos", Tag.TAG_LIST)) return false;
        if(!tag.contains("RopeShape", Tag.TAG_COMPOUND)) return false;

        return true;
    }
}
