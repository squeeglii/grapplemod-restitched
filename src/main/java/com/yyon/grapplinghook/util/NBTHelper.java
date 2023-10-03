package com.yyon.grapplinghook.util;

import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.ListTag;

public class NBTHelper {

    /**
     * Taken from Entity - shorthand helper for double nbt lists
     * @param numbers the numbers to place in the list
     * @return a list tag containing said numbers
     */
    public static ListTag newDoubleList(double ... numbers) {
        ListTag listTag = new ListTag();
        for (double d : numbers) {
            listTag.add(DoubleTag.valueOf(d));
        }
        return listTag;
    }

}
