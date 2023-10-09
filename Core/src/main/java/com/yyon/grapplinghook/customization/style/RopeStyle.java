package com.yyon.grapplinghook.customization.style;

public enum RopeStyle {

    REGULAR(0),
    IRON(1),
    RIBBON(2),
    TRANS_PRIDE(3),
    GHOSTLY(4),
    TAPE_MEASURE(5);


    private final int id;

    RopeStyle(int id) {
        this.id = id;
    }

    public float getTextureMinBound() {
        return (this.id) / 8f;
    }

    public float getTextureMidBound() {
        return (this.id + 0.5f) / 8f;
    }

    public float getTextureMaxBound() {
        return (this.id + 1) / 8f;
    }

}
