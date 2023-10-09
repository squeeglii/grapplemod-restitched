package com.yyon.grapplinghook.customization.predicate;

import com.yyon.grapplinghook.customization.CustomizationVolume;

public class SuccessCustomizationPredicate implements CustomizationPredicate<Object> {

    public static final SuccessCustomizationPredicate INSTANCE = new SuccessCustomizationPredicate();

    @Override
    public boolean shouldPass(CustomizationVolume volume) {
        return true;
    }

    @Override
    public boolean shouldPass(Object value) {
        return true;
    }

}
