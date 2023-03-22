package com.yyon.grapplinghook.customization.type;

import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.content.registry.GrappleModMetaRegistry;
import com.yyon.grapplinghook.customization.CustomizationVolume;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class AttachmentProperty extends BooleanProperty {

    private static Set<AttachmentProperty> attachments = new HashSet<>();

    public static void bake() {
        int initialLength = attachments.size();
        AttachmentProperty.attachments = attachments.stream()
                .filter(property -> GrappleModMetaRegistry.CUSTOMIZATION_PROPERTIES.getKey(property) != null)
                .collect(Collectors.toSet());
        GrappleMod.LOGGER.info("Baked Attachment Properties (Reduced %s -> %s)".formatted(initialLength, attachments.size()));
    }

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
        Optional<AttachmentProperty> optOther = this.shadowedBy.getShadowingProperty();
        if(optOther.isEmpty()) return false;

        AttachmentProperty other = optOther.get();
        Optional<AttachmentProperty> optOtherDep = other.getShadowingProperty();

        return optOtherDep.isPresent() && optOtherDep.get() == this;
    }

    public Optional<AttachmentProperty> getShadowingProperty() {
        return Optional.ofNullable(this.shadowedBy);
    }

    public static Set<AttachmentProperty> getBakedProperties() {
        return Collections.unmodifiableSet(AttachmentProperty.attachments);
    }

    public static boolean shouldUseShadowerName(CustomizationVolume custom, AttachmentProperty attachment) {
        Optional<AttachmentProperty> optShadower = attachment.getShadowingProperty();
        if(optShadower.isEmpty())
            return false;

        AttachmentProperty shadower = optShadower.get();
        return custom.get(shadower);
    }
}
