package com.enhancell.remotesample;

import android.os.Environment;

public class CommonFunctions {

    public static String path = getDeviceStoragePath();
    public static String DASHURL;

    public static String getDeviceStoragePath() {
        String path1 = Environment.getExternalStorageDirectory().getPath();
        return path1;
    }

}
