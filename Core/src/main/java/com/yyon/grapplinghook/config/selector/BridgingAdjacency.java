package com.yyon.grapplinghook.config.selector;

import me.cg360.mod.bridging.config.helper.Translatable;

/** See Path#calculateMissedPoints() - that's what this affects. */
public enum BridgingAdjacency implements Translatable {

    DISABLED (false, false, false),
    FACES    (true, false, false),
    EDGES    (true, true, false),
    CORNERS  (true, false, false);

    private boolean supportsFaces;
    private boolean supportsEdges;
    private boolean supportsCorners;

    BridgingAdjacency(boolean supportsFaces, boolean supportsEdges, boolean supportsCorners) {
        this.supportsFaces = supportsFaces;
        this.supportsEdges = supportsEdges;
        this.supportsCorners = supportsCorners;
    }

    public boolean supportsFaces() {
        return this.supportsFaces;
    }

    public boolean supportsEdges() {
        return this.supportsEdges;
    }

    public boolean supportsCorners() {
        return this.supportsCorners;
    }

    @Override
    public String getTranslationKey() {
        return "enum.bridgingmod.bridging_adjacency.%s".formatted(this.name().toLowerCase());
    }
}
