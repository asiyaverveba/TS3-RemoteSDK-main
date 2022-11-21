package com.enhancell.remotesample;

import androidx.annotation.NonNull;

import java.util.List;

public class Utils {

    @NonNull
    public static int[] toIntArray(final List<Integer> list) {
        if (list == null)
            return new int[0];
        int[] arr = new int[list.size()];
        for (int i = 0; i < list.size(); ++i) {
            arr[i] = list.get(i);
        }
        return arr;
    }
}
