package com.yyon.grapplinghook.util;

//todo: move all this into enchantment definitions.
//      It's a lesser used thing so I don't want to block everything behind it, but
//      this should receive an update soon.
@Deprecated(since = "mc 1.21.1")
public class EnchantmentValues {

    public static final float WALLRUN_JUMP_UP_FORCE = 0.7f;
    public static final float WALLRUN_JUMP_SIDE_FORCE = 0.4f;
    public static final float WALLRUN_DRAG = 0.01f;
    public static final float MAX_WALLRUN_TIME = 6.0f; // seconds
    public static final float BASE_WALLRUN_SPEED = 0.1f;
    public static final float MIN_WALLRUN_SPEED = 0.0f;
    public static final float MAX_WALLRUN_SPEED = 0.7f;


    public static final boolean DOUBLE_JUMP_RELATIVE_TO_FALL_SPEED = false;
    public static final float DOUBLE_JUMP_FORCE = 0.8f;
    public static final boolean LIMIT_DOUBLE_JUMP_AT_FALL_SPEED_LIMIT = true;
    public static final float DOUBLE_JUMP_FALL_SPEED_LIMIT = 4.0f;

    public static final float SLIDE_JUMP_FORCE =  0.6f;
    public static final float SLIDE_FRICTION = 1.0f / 150.0f;
    public static final float MIN_SLIDE_SPEED = 0.15f;
    public static final float MIN_SUSTAIN_SLIDE_SPEED = 0.01f;

}
