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

    private static final String NBT_POS = "pos";
    private static final String NBT_ROPE_SHAPE = "rope_shape";
    private static final String NBT_COLLISION = "last_collision";
    private static final String NBT_SUB_POS = "sub_pos";
    private static final String NBT_DIRECTION = "direction";

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

        ListTag posTag = source.getList(NBT_POS, ListTag.TAG_DOUBLE);
        CompoundTag ropeSegHandlerTag = source.getCompound(NBT_ROPE_SHAPE);
        CompoundTag collisionTag = source.getCompound(NBT_COLLISION);

        CompoundTag collisionPosTag = collisionTag.getCompound(NBT_POS);
        ListTag lastSubCollisionPosTag = collisionTag.getList(NBT_SUB_POS, Tag.TAG_DOUBLE);
        String directionString = collisionTag.getString(NBT_DIRECTION);

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

        collision.put(NBT_POS, collisionPosTag);
        collision.put(NBT_SUB_POS, collisionSubPosTag);
        collision.putString(NBT_DIRECTION, directionString);

        hookData.put(NBT_POS, hookPos);
        hookData.put(NBT_ROPE_SHAPE, ropeShape);
        hookData.put(NBT_COLLISION, collision);

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

    public Vec getHookPos() {
        return new Vec(this.hookPos);
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

        if(!tag.contains(NBT_POS, Tag.TAG_LIST)) return false;
        if(!tag.contains(NBT_ROPE_SHAPE, Tag.TAG_COMPOUND)) return false;

        if(!tag.contains(NBT_COLLISION, Tag.TAG_COMPOUND)) return false;

        return true;
    }
}
