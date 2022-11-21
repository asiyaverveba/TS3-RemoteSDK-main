package com.enhancell.remotesample;




import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;




public class LoginPost {
    private static Context context;
    public static String cellinfourl = "", homappingurl = "", thresholdurl = "", apkpath, apkversion, apkfiletype, urlfilepath, testseqcsv = "", temperaturethresholdurl = "";
    public static String extraparam1, extraparam2, extraparam3;
    private AlertDialog alertDialog;
    private long DIAGLOG_DELAY = 30 * 1000;
    static boolean downloadFailFlag = false;
    static boolean exceptionflag = false;
    private static boolean flagDiag, flagDiagLte, flagDiagUmts;

    public LoginPost(Context context) {
        this.context = context;
    }

    // Check valid user from server
    public String postServerData(String email, String pwd, double latitude, double longitude) {
        LoginResponse wst = new LoginResponse(LoginResponse.POST_TASK,
                context, "Connecting Ver_VQual");
        Log.v("Login ","Inside postServerData:" + email + "," + pwd);
        wst.addNameValuePair("User_Name", email);
        wst.addNameValuePair("Password", pwd);
        wst.addNameValuePair("latitude", Double.toString(latitude));
        wst.addNameValuePair("longitude", Double.toString(longitude));
        Log.v("Login ","Inside postServerData:" + latitude + "," + longitude);
        String result = wst.doResponse(LoginActivity.url + UrlConstants.SERVICE_URL_RF_INFO);
        Log.v("URL SERVICE_URL_RF_INFO", "" + "" + UrlConstants.SERVICE_URL_RF_INFO);
        Log.v("SERVER IP-1", "" + "");
        return result;
    }

    // License  Validation from server
    public String checklicense(String ImeiId) {
        LoginResponse wst = new LoginResponse(LoginResponse.POST_TASK,
                context, "Connecting Ver_VQual");

        wst.addNameValuePair("IMEI_Number", ImeiId);
        wst.addNameValuePair("APK_TYPE", "TS2.0");
        //FIXME Make wst async and add timeout validation
        String result = wst.doResponse(LoginActivity.url + UrlConstants.SERVICE_URL_LICENSE_INFO);
        Log.v("SERVICE_URL_LICENSE_INFO", "http://52.13.20.11" + UrlConstants.SERVICE_URL_LICENSE_INFO);
        Log.v("SERVER IP-2", "" + "");
        return result;
    }


}
