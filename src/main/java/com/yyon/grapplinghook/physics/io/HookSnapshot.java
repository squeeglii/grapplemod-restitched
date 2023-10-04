package com.yyon.grapplinghook.physics.io;

import com.yyon.grapplinghook.content.entity.grapplinghook.GrapplinghookEntity;
import com.yyon.grapplinghook.content.entity.grapplinghook.RopeSegmentHandler;
import com.yyon.grapplinghook.util.NBTHelper;
import com.yyon.grapplinghook.util.Vec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

public class HookSnapshot {

    private Vec hookPos;
    private double ropeLength;
    private RopeSegmentHandler segmentHandler;


    public HookSnapshot(CompoundTag source) {
        if(!isTagValid(source))
            throw new IllegalArgumentException("Tag passed is missing required data for hook.");

        ListTag posTag = source.getList("Pos", ListTag.TAG_DOUBLE);
        CompoundTag ropeSegHandlerTag = source.getCompound("RopeShape");

        this.hookPos = new Vec(posTag);
        this.ropeLength = source.getDouble("RopeLength");
        this.segmentHandler = new RopeSegmentHandler(ropeSegHandlerTag);
    }

    public HookSnapshot(GrapplinghookEntity source) {
        this.hookPos = new Vec(source.position());
        this.ropeLength = source.getCurrentRopeLength();
        this.segmentHandler = source.getSegmentHandler();
    }


    public CompoundTag saveToNBT() {
        CompoundTag hookData = new CompoundTag();
        ListTag hookPos = NBTHelper.newDoubleList(this.getX(), this.getY(), this.getZ());
        ListTag ropeShape = this.segmentHandler.saveToNBT();

        hookData.put("Pos", hookPos);
        hookData.put("RopeShape", ropeShape);
        hookData.putDouble("RopeLength", this.getRopeLength());

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

    public double getRopeLength() {
        return this.ropeLength;
    }

    public static boolean isTagValid(CompoundTag tag) {
        if(tag == null) return false;

        if(!tag.contains("Pos")) return false;
        if(!tag.contains("RopeShape")) return false;
        if(!tag.contains("RopeLength")) return false;

        return true;
    }
}
