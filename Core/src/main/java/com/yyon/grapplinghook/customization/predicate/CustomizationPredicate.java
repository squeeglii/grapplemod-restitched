package com.yyon.grapplinghook.customization.predicate;

import com.yyon.grapplinghook.customization.data.HookCustomization;

public interface CustomizationPredicate<T> {

    boolean shouldPass(HookCustomization volume);
    boolean shouldPass(T value);

}
