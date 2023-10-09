package com.yyon.grapplinghook.physics.io;

import com.yyon.grapplinghook.content.entity.grapplinghook.RopeSegmentHandler;
import com.yyon.grapplinghook.util.Vec;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class RopeSnapshot {

    private static final String NBT_SEGMENTS_LIST = "segments";
    private static final String NBT_ROPE_LENGTH = "rope_length";

    private static final String NBT_TOP = "top";
    private static final String NBT_BOTTOM = "bottom";
    private static final String NBT_POS = "pos";

    private final LinkedList<Vec> segments;
    private final LinkedList<Direction> topSides;
    private final LinkedList<Direction> bottomSides;

    private final double ropeLength;


    public RopeSnapshot(RopeSegmentHandler segmentHandler) {
        this.segments = new LinkedList<>();
        this.topSides = new LinkedList<>();
        this.bottomSides = new LinkedList<>();
        this.ropeLength = segmentHandler.getCurrentRopeLength();

        this.segments.addAll(segmentHandler.getSegments());
        this.topSides.addAll(segmentHandler.getTopSides());
        this.bottomSides.addAll(segmentHandler.getBottomSides());
    }

    public RopeSnapshot(CompoundTag nbt) {
        this.segments = new LinkedList<>();
        this.topSides = new LinkedList<>();
        this.bottomSides = new LinkedList<>();

        this.ropeLength = nbt.getDouble(NBT_ROPE_LENGTH);
        ListTag segmentsTag = nbt.getList(NBT_SEGMENTS_LIST, Tag.TAG_COMPOUND);

        for(int i = 0; i < segmentsTag.size(); i++) {
            CompoundTag entry = segmentsTag.getCompound(i);

            ListTag posTag = entry.getList(NBT_POS, Tag.TAG_DOUBLE);

            Vec pos = new Vec(posTag);
            String topSide = entry.getString(NBT_TOP);
            String bottomSide = entry.getString(NBT_BOTTOM);

            Direction topSideDir = !topSide.equalsIgnoreCase("null")
                    ? Direction.byName(topSide)
                    : null;
            Direction bottomSideDir = !bottomSide.equalsIgnoreCase("null")
                    ? Direction.byName(bottomSide)
                    : null;

            this.pushSegment(pos, topSideDir, bottomSideDir);
        }
    }


    public CompoundTag toNBT() {
        CompoundTag snapshotTag = new CompoundTag();
        ListTag segmentsTag = new ListTag();

        for(int i = 0; i < this.segments.size(); i++) {
            Vec segment = this.segments.get(i);
            Direction topDir = this.topSides.get(i);
            Direction bottomDir = this.bottomSides.get(i);

            CompoundTag entry = new CompoundTag();
            ListTag posTag = segment.toNBT();

            entry.put(NBT_POS, posTag);

            String topVal = topDir != null
                    ? topDir.getName()
                    : "null";

            String bottomVal = bottomDir != null
                    ? bottomDir.getName()
                    : "null";

            entry.putString(NBT_TOP, topVal);
            entry.putString(NBT_BOTTOM, bottomVal);

            segmentsTag.add(entry);
        }

        snapshotTag.put(NBT_SEGMENTS_LIST, segmentsTag);
        snapshotTag.putDouble(NBT_ROPE_LENGTH, this.ropeLength);

        return snapshotTag;
    }


    private void pushSegment(Vec segment, Direction topSide, Direction bottomSide) {
        this.segments.add(segment);
        this.topSides.add(topSide);
        this.bottomSides.add(bottomSide);
    }


    public List<Vec> getSegments() {
        return Collections.unmodifiableList(this.segments);
    }

    public List<Direction> getTopSides() {
        return Collections.unmodifiableList(this.topSides);
    }

    public List<Direction> getBottomSides() {
        return Collections.unmodifiableList(this.bottomSides);
    }

    public double getRopeLength() {
        return this.ropeLength;
    }
}
