package com.yyon.grapplinghook.util;

import net.minecraft.core.Direction;

import java.util.HashMap;
import java.util.Map;

public enum NullableDirection {

    DOWN(Direction.DOWN, "down"),
    UP(Direction.UP, "up"),
    NORTH(Direction.NORTH, "north"),
    SOUTH(Direction.SOUTH, "south"),
    WEST(Direction.WEST, "west"),
    EAST(Direction.EAST, "east"),
    NONE(null, "null");

    private final String id;
    private final Direction direction;

    private static final Map<String, NullableDirection> REVERSE_ID = new HashMap<>();
    private static final Map<Direction, NullableDirection> REVERSE_DIRECTION = new HashMap<>();

    static {
        for (NullableDirection enumVal : values()) {
            REVERSE_ID.put(enumVal.id, enumVal);
            REVERSE_DIRECTION.put(enumVal.direction, enumVal);
        }
    }

    NullableDirection(Direction direction, String id) {
        this.direction = direction;
        this.id = id;
    }

    public Direction toVanilla() {
        return this.direction;
    }

    public static NullableDirection byName(String name) {
        String formatted = name.trim().toLowerCase();
        return REVERSE_ID.get(formatted);
    }

    public static NullableDirection fromVanilla(Direction direction) {
        if(direction == null)
            return NullableDirection.NONE;

        return REVERSE_DIRECTION.get(direction);
    }

}
