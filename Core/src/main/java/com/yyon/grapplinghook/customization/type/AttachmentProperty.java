package com.yyon.grapplinghook.customization.type;

import com.yyon.grapplinghook.customization.CustomizationVolume;

import java.util.Optional;

public class AttachmentProperty extends BooleanProperty {

    private final AttachmentProperty shadowedBy;

    public AttachmentProperty(Boolean defaultValue) {
        this(defaultValue, null);
    }

    public AttachmentProperty(Boolean defaultValue, AttachmentProperty shadowedBy) {
        super(defaultValue);

        this.shadowedBy = shadowedBy;

        if(this.isInVisibilityCycleTrap())
            throw new IllegalArgumentException("Cycle detected! Cannot be shadowed by a property that is shadowed by this property.");
    }

    /** Checks to see if the other shadowing property defines this as it's
     * shadowing property, thus forming a circular dependency.
     */
    private boolean isInVisibilityCycleTrap() {
        if(this.getShadowingProperty().isEmpty()) return false;
        Optional<AttachmentProperty> optShadower = this.getShadowingProperty();
        if(optShadower.isEmpty()) return false;

        AttachmentProperty shadower = optShadower.get();
        Optional<AttachmentProperty> optOtherDep = shadower.getShadowingProperty();

        return optOtherDep.isPresent() && optOtherDep.get() == this;
    }

    public Optional<AttachmentProperty> getShadowingProperty() {
        return Optional.ofNullable(this.shadowedBy);
    }

    public static boolean isShadowed(CustomizationVolume custom, AttachmentProperty attachment) {
        Optional<AttachmentProperty> optShadower = attachment.getShadowingProperty();
        if(optShadower.isEmpty())
            return false;

        AttachmentProperty shadower = optShadower.get();
        return custom.get(shadower);
    }
}
