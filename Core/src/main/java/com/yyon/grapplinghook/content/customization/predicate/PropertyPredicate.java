package com.yyon.grapplinghook.content.customization.predicate;

import com.yyon.grapplinghook.content.customization.data.HookCustomization;

public interface PropertyPredicate<T> {

    boolean shouldPass(HookCustomization volume);
    boolean shouldPass(T value);

}
