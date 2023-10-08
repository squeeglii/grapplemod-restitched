package com.yyon.grapplinghook.physics.io;

import com.yyon.grapplinghook.content.entity.grapplinghook.GrapplinghookEntity;
import com.yyon.grapplinghook.util.NBTHelper;
import com.yyon.grapplinghook.util.Vec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;

public class HookSnapshot {

    private final Vec hookPos;
    private final RopeSnapshot ropeSnapshot;

    private final BlockPos lastBlockCollision;
    private final Direction lastBlockCollisionSide;
    private final Vec lastSubCollisionPos;


    public HookSnapshot(GrapplinghookEntity source) {
        this.hookPos = new Vec(source.position());
        this.ropeSnapshot = new RopeSnapshot(source.getSegmentHandler());

        this.lastBlockCollision = source.getLastBlockCollision();
        this.lastBlockCollisionSide = source.getLastBlockCollisionSide();
        this.lastSubCollisionPos = source.getLastSubCollisionPos();
    }

    public HookSnapshot(CompoundTag source) {
        if(!isTagValid(source))
            throw new IllegalArgumentException("Tag passed is missing required data for hook.");

        ListTag posTag = source.getList("Pos", ListTag.TAG_DOUBLE);
        CompoundTag ropeSegHandlerTag = source.getCompound("RopeShape");
        CompoundTag collisionTag = source.getCompound("Collision");

        CompoundTag collisionPosTag = collisionTag.getCompound("Pos");
        ListTag lastSubCollisionPosTag = collisionTag.getList("SubPos", Tag.TAG_DOUBLE);
        String directionString = collisionTag.getString("Direction");

        BlockPos collisionPos = NbtUtils.readBlockPos(collisionPosTag);
        Vec collisionSubPos = new Vec(lastSubCollisionPosTag);
        Direction direction = directionString.equalsIgnoreCase("null")
                ? null
                : Direction.byName(directionString);

        this.hookPos = new Vec(posTag);
        this.ropeSnapshot = new RopeSnapshot(ropeSegHandlerTag);

        this.lastBlockCollision = collisionPos;
        this.lastSubCollisionPos = collisionSubPos;
        this.lastBlockCollisionSide = direction;
    }


    public CompoundTag saveToNBT() {
        CompoundTag hookData = new CompoundTag();

        ListTag hookPos = NBTHelper.newDoubleList(this.getX(), this.getY(), this.getZ());
        CompoundTag ropeShape = this.ropeSnapshot.toNBT();
        CompoundTag collision = new CompoundTag();

        CompoundTag collisionPosTag = NbtUtils.writeBlockPos(this.lastBlockCollision);
        ListTag collisionSubPosTag = this.lastSubCollisionPos.toNBT();
        String directionString = this.lastBlockCollisionSide != null
                ? this.lastBlockCollisionSide.getSerializedName()
                : "null";

        collision.put("Pos", collisionPosTag);
        collision.put("SubPos", collisionSubPosTag);
        collision.putString("Direction", directionString);

        hookData.put("Pos", hookPos);
        hookData.put("RopeShape", ropeShape);
        hookData.put("Collision", collision);

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

    public RopeSnapshot getRopeSnapshot() {
        return this.ropeSnapshot;
    }

    public boolean isAttached() {
        return true;
    }

    public BlockPos getLastBlockCollidedWith() {
        return this.lastBlockCollision;
    }

    public Vec getLastSubCollisionPos() {
        return this.lastSubCollisionPos;
    }

    public Direction getLastBlockCollisionSide() {
        return this.lastBlockCollisionSide;
    }

    public static boolean isTagValid(CompoundTag tag) {
        if(tag == null) return false;

        if(!tag.contains("Pos", Tag.TAG_LIST)) return false;
        if(!tag.contains("RopeShape", Tag.TAG_COMPOUND)) return false;

        if(!tag.contains("Collision", Tag.TAG_COMPOUND)) return false;

        return true;
    }
}
