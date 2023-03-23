package com.yyon.grapplinghook.customization.predicate;

import com.yyon.grapplinghook.customization.CustomizationVolume;

public interface CustomizationPredicate<T> {

    boolean shouldPass(CustomizationVolume volume);
    boolean shouldPass(T value);

}
