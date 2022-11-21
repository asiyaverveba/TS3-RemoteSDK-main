package com.enhancell.remotesample;

import static com.enhancell.remotesample.CommonFunctions.DASHURL;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.verveba.ts3.R;
import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

public class LoginActivity extends AppCompatActivity implements LocationTracker.LocationChangeListener {

    private static final boolean isDebugging = false;

    Button btn_login;
    EditText edit_username;
    String username,validity;
    private LoginPost loginPost = new LoginPost(LoginActivity.this);
    private ProgressDialog upprogressDialog;
    double latitude=67.99;
    double longitude=99.56;
    String TAG="LoginActivity";
    String imei;
    public static String url;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    private GoogleApiClient googleApiClient;
    private LocationTracker mLocationTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_login);
        btn_login = findViewById(R.id.email_sign_in_button);
        edit_username = findViewById(R.id.edit_username);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username=edit_username.getText().toString();
                if (username!=null)
                {
                    upprogressDialog.show();
                    new checkValidUser().execute();
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Enter Valid username!!!",Toast.LENGTH_LONG).show();
                }
            }
        });

        upprogressDialog = new ProgressDialog(LoginActivity.this);
        upprogressDialog.setCancelable(false);
        upprogressDialog.setMessage("Setting up, please wait...");
        upprogressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        upprogressDialog.setIndeterminate(true);
        upprogressDialog.setMax(100);
        upprogressDialog.setCanceledOnTouchOutside(false);
       // upprogressDialog.show();

        // check for user permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                beForeLoginSetup();
            } else {
                checkUserPermission();
            }
        } else {
            beForeLoginSetup();
        }
    }
    public void showBasicDialog(Context context, String message, String title) {
        AlertDialog.Builder  builder = new AlertDialog.Builder(this);
        //Uncomment the below code to Set the message and title from the strings.xml file
        builder.setMessage(message) .setTitle(title);

        //Setting message manually and performing action on button click
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                       dialog.dismiss();
                    }
                });
        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.setTitle("AlertDialogExample");
        alert.show();
    }

    @Override
    public void onCustomLocationChanged(Location loc) {
        if (mLocationTracker != null)
            mLocationTracker.UnRegisterLocationTracker(this.getClass().toString());
        latitude = loc.getLatitude();
        longitude = loc.getLongitude();

        Log.v("Latitude", "" + latitude);
        Log.v("longitude", "" + longitude);
    }

    @Override
    public void finish() {
        super.finish();
        if (mLocationTracker != null)
            mLocationTracker.UnRegisterLocationTracker(this.getClass().toString());
    }


    // get url from file
    private class getUrlAsyntask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
             url = getDashUrl();
            Log.v(TAG, "Base Url:-" + url);
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            String ssvpath = CommonFunctions.path
                    + DebugConstant.SSV_FOLDER_PATH;
            File ssvfilepath = new File(ssvpath);
            if (!ssvfilepath.exists()) {
                ssvfilepath.mkdir();
            }
        }
    }

    static String formatIP(String ipaddr) {
        Log.v("formatIP called ", "" + ipaddr);
        String formatip = "";
        String[] ip = ipaddr.split("\\.");
        formatip = "http://" + Integer.parseInt(ip[0]) / 3 + "." + Integer.parseInt(ip[1]) / 8 + "." + Integer.parseInt(ip[2]) / 7 + "." + Integer.parseInt(ip[3]) / 5;
        Log.v("final ip is--->", "" + formatip);
        return formatip;
    }

    public String getDashUrl() {
        Log.v("getDashUrl is called", "getDashUrl");
        DASHURL="http://52.13.20.11";
//        File file = new File(CommonFunctions.path + DebugConstant.SSV_FOLDER_PATH + "/url.txt");
//        if (!file.exists()) {
//            Log.d(TAG, "url.txt file not found in ssv folder and we are cretaing that folder");
//            try {
//                updateIpAddress();
//
//            }
//            catch (Exception e)
//            {
//                Log.e(TAG,"Exception :- "+e.getMessage());
//
//            }
//
//
//        } else {
//            try {
//                BufferedReader br = null;
//                br = new BufferedReader(new FileReader(file));
//                String ip = "";
//                while ((ip = br.readLine()) != null) {
//                    Log.d(TAG, "ip is " + ip);
//                    if (ip != null && ip.contains("https")) {
//                        DASHURL = ip;
//                    } else {
//                        if (ip != null && !ip.equals("")) {
//                            DASHURL = formatIP(ip);
//                        } else
//                            return DASHURL;
//
//                    }
//                    Log.v("DASHURL is", "" + DASHURL);
//
//                }
//                br.close();
//            } catch (FileNotFoundException e) {
//                updateIpAddress();
//                e.printStackTrace();
//            } catch (IOException e) {
//                updateIpAddress();
//                e.printStackTrace();
//            } finally {
//            }
//        }
        return DASHURL;
    }

    private void updateIpAddress() {
        File file = new File(CommonFunctions.path + DebugConstant.SSV_FOLDER_PATH + "/url.txt");
        try {
            String content = "156.104.140.55";
            File file1 = new File(file.getAbsolutePath());
            if (!file1.exists()) {
                file1.createNewFile();
            }
            FileWriter fw = new FileWriter(file1.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(content);
            Log.v("content ip is--->", "" + content);
            bw.close();
            DASHURL = formatIP(content);
            Log.v("DASHURL is", "" + DASHURL);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void checkUserPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(LoginActivity.this,
                        new String[]{
                                Manifest.permission.VIBRATE,
                                Manifest.permission.CAMERA,
                                Manifest.permission.INTERNET,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS,
                                Manifest.permission.READ_PHONE_STATE,
                                Manifest.permission.READ_PHONE_NUMBERS,
                                Manifest.permission.ANSWER_PHONE_CALLS,
                                Manifest.permission.PROCESS_OUTGOING_CALLS,
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.CALL_PHONE,
                                Manifest.permission.SYSTEM_ALERT_WINDOW,
                                Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
                                Manifest.permission.ACCESS_WIFI_STATE,
                                Manifest.permission.CHANGE_WIFI_STATE,
                                Manifest.permission.MODIFY_AUDIO_SETTINGS,
                                Manifest.permission.BROADCAST_STICKY,
                                Manifest.permission.WRITE_SETTINGS,
                                Manifest.permission.WRITE_SECURE_SETTINGS,
                                Manifest.permission.MASTER_CLEAR,
                                Manifest.permission.MODIFY_PHONE_STATE,
                                Manifest.permission.REBOOT,
                                Manifest.permission.WRITE_APN_SETTINGS,
                                Manifest.permission.ACCESS_NETWORK_STATE,
                                Manifest.permission.CHANGE_NETWORK_STATE,
                                Manifest.permission.CHANGE_CONFIGURATION,
                                Manifest.permission.BIND_INPUT_METHOD,
                                Manifest.permission.READ_CONTACTS,
                                Manifest.permission.READ_CALL_LOG,
                                Manifest.permission.CAPTURE_AUDIO_OUTPUT,
                                Manifest.permission.RECORD_AUDIO,

                        },
                        1);
            }
        } else {
            beForeLoginSetup();
        }
    }

    // Check Valid User
    private class checkValidUser extends AsyncTask<Void, Void, Void> {
        String res;
        @Override
        protected Void doInBackground(Void... params) {
            res = loginPost.postServerData(username, username, latitude, longitude);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if ((res != null && !res.equals("Invalid") && !res.isEmpty()) || isDebugging) {
                new checkLicense().execute();
            } else if (res != null && !res.isEmpty() && res.equals("timeout")) {
                upprogressDialog.dismiss();
                showBasicDialog(LoginActivity.this, getResources().getString(R.string.hint_connect_timeout_exception), "Login Failed");
            } else {
                upprogressDialog.dismiss();
                showBasicDialog(LoginActivity.this, "Invalid credentials", "Login Failed");
            }
        }
    }

    // check Lincese Expiry
    private class checkLicense extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            validity = loginPost.checklicense(imei); //login details ok
            Log.v(TAG, "Module Response " + validity);

            //res=res+"[-1:3:4:6:7:9]";
//            String mocsfb = loginPost.getcsfbmo(email);
//            if (!validity.equals("")) {
//                pref.putMOCSFB(mocsfb);
//            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            upprogressDialog.dismiss();
            if ((validity != null && !validity.equals("") && validity.contains("Valid")) || isDebugging) {
                // call stationary activity
                Intent intent=new Intent(LoginActivity.this,StationaryActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            } else if (validity != null && !validity.isEmpty() && validity.equals("timeout")) {
                upprogressDialog.dismiss();
                showBasicDialog(LoginActivity.this, getResources().getString(R.string.hint_connect_timeout_exception), "Login Failed");
            } else {
                upprogressDialog.dismiss();
                showBasicDialog(LoginActivity.this, "License expired", "Login Failed");
            }
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 23 || requestCode == 22) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "You have allowed Phone permission", Toast.LENGTH_LONG).show();
                //  beForeLoginSetup();
            } else {
                Log.v(TAG, "1 Call permission issue ");
                if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    Log.v(TAG, "1 READ_PHONE_STATE not granted ");
                    ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, 22);
                    Toast.makeText(getApplicationContext(), "You have not allow some important permission", Toast.LENGTH_LONG).show();
                } else {
                    Log.v(TAG, "1 READ_PHONE_STATE granted ");
                    Toast.makeText(getApplicationContext(), "Phone state granted", Toast.LENGTH_LONG).show();
                }

                if (checkSelfPermission(Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
                    Log.v(TAG, "1 READ_CALL_LOG NOT  granted ");
                    ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.READ_CALL_LOG}, 23);
                    Toast.makeText(getApplicationContext(), "You have not allow some important permission", Toast.LENGTH_LONG).show();
                } else {
                    Log.v(TAG, "1 READ_CALL_LOG granted ");
                    Toast.makeText(getApplicationContext(), "Phone Call details granted", Toast.LENGTH_LONG).show();
                }
            }
        } else {
            //check if all permissions are granted
            boolean allgranted = false;
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    allgranted = true;
                } else {
                    Log.v(TAG, "Failed permission :- " + grantResults[i]);
                    allgranted = false;
                    break;
                }
            }
            if (allgranted) {
                Toast.makeText(getApplicationContext(), "You have allowed Phone permission", Toast.LENGTH_LONG).show();

                beForeLoginSetup();
            } else {

                Log.v(TAG, "Call permission issue ");
                if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    Log.v(TAG, "READ_PHONE_STATE not granted ");
                    ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, 22);
                    Toast.makeText(getApplicationContext(), "You have not allow some important permission", Toast.LENGTH_LONG).show();
                } else {
                    Log.v(TAG, "READ_PHONE_STATE granted ");
                    Toast.makeText(getApplicationContext(), "Phone state granted", Toast.LENGTH_LONG).show();
                }

                if (checkSelfPermission(Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
                    Log.v(TAG, "READ_CALL_LOG NOT  granted ");
                    ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.READ_CALL_LOG}, 23);
                    Toast.makeText(getApplicationContext(), "You have not allow some important permission", Toast.LENGTH_LONG).show();
                } else {
                    Log.v(TAG, "READ_CALL_LOG granted ");
                    Toast.makeText(getApplicationContext(), "Phone Call details granted", Toast.LENGTH_LONG).show();
                    beForeLoginSetup();
                }
            }

        }
    }

    private void beForeLoginSetup() {
        TelephonyManager mngr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            imei = Settings.Secure.getString(
                    getApplicationContext().getContentResolver(),
                    Settings.Secure.ANDROID_ID);
            Log.v(TAG, "getimei Imei is :- " + imei);
        } else {
            imei = mngr.getImei();
            Log.v(TAG, "getdevice Imei is :- " + imei);
        }

        Log.v(TAG, "Imei is :- " + imei);
        new getUrlAsyntask().execute();

        mLocationTracker = LocationTracker.GetLocationTrackerInstance();
        mLocationTracker.RegisterLocationTracker(LoginActivity.this, this);
        googleApiClient = getAPIClientInstance();
        if (googleApiClient != null) {
            googleApiClient.connect();
        }
        requestGPSSettings();
    }


    //get google api client instance to check location service is enabled or disabled
    private GoogleApiClient getAPIClientInstance() {
        GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API).build();
        return mGoogleApiClient;
    }

    //this is request dialog of enable gps programmatically but only if location service is disabled
    private void requestGPSSettings() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(2000);
        locationRequest.setFastestInterval(500);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);
        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.i("", "All location settings are satisfied.");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.i("", "Location settings are not satisfied. Show the user a dialog to" + "upgrade location settings ");
                        try {
                            status.startResolutionForResult(LoginActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            Log.e("Applicationsett", e.toString());
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.i("", "Location settings are inadequate, and cannot be fixed here. Dialog " + "not created.");
                        break;
                }
            }
        });
    }
}