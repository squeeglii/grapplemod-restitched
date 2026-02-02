package com.yyon.grapplinghook.config.helper.impl;

import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.config.helper.IDefaultProvider;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Optional;

public class DefaultValueTracker implements IDefaultProvider {

    private HashMap<String, Object> defaultValues = null;

    protected void saveFieldDefaults() {
        if(this.defaultValues != null) {
            GrappleMod.LOGGER.trace("Tried to re-save the defaults variables for object. These are locked!");
            return;
        }

        this.defaultValues = new HashMap<>();

        // Try to get the value of every field and store it.
        for(Field field: this.getClass().getDeclaredFields()) {
            try {
                field.setAccessible(true);
                Object value = field.get(this);
                this.defaultValues.put(field.getName(), value);
            } catch (Exception err) {
                GrappleMod.LOGGER.warn("Unable to get value when saving defaults! Unexpected! [%s]".formatted(err.getMessage()));
                return;
            }
        }
    }

    public Optional<Object> getDefaultForField(Field field) {
        if(this.defaultValues == null) return Optional.empty();

        Object val = this.defaultValues.get(field.getName());
        return Optional.ofNullable(val);
    }

}
