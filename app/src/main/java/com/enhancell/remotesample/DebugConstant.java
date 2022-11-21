package com.enhancell.remotesample;


import android.util.Log;

public class DebugConstant {
    public static final boolean DEBUG_ENABLE_LOG = true;
    //for production make it false
    public static final boolean DEBUG_ENABLE_WCDMAMODE = false;
    //for production make it false
    public static final boolean DEBUG_ENABLE_LTEMODE = false;
    //for production make it false
    public static final boolean DEBUG_ENABLE_GSMMODE = false;
    public static final boolean DEBUG_ENABLE_AUTOMT = true;
    public static final boolean DEBUG_ENABLE_HOTEST = false;

    ///////////////////////////////////
    //Control for diff war location in server sides
    public static final String SSV_FOLDER_PATH = "/.TS3";
    /***********************************************
     * Over The Air upload feature  enable/disable *
     ***********************************************/
    public static boolean QMDL_PARSING = false;
    public static boolean MEXICO_LOCKING = true;
    public static final boolean OTA_FEATURE_ENABLED = true;
    //Enable and disable key
    public static final boolean ONLY_SA = true;
    public static String FTPFOLDER = DebugConstant.SSV_FOLDER_PATH + "/ftptest";
    public final static String ACTION_UPLOAD_COMPLETED = "com.verveba.tapas.UploadLayer3.intent.action.ACTION_UPLOAD_COMPLETED";

    /*Survey image location path */
    public static final String SURVEY_IMAGES_FOLDER = CommonFunctions.path + DebugConstant.SSV_FOLDER_PATH + "/Survey/";

    public static void printDLog(String tag, String message) {
        if (DEBUG_ENABLE_LOG)
            Log.v(tag, " " + message);
    }

    public static void printELog(String tag, String message) {
        if (DEBUG_ENABLE_LOG)
            Log.v(tag, " " + message);
    }
}
