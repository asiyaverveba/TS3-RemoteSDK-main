package com.enhancell.remotesample;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;


import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

//singleton class for getting locations
public class LocationTracker implements LocationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    //callback interface
    public interface LocationChangeListener {
        public void onCustomLocationChanged(Location loc);
    }

    public static final long INTERVAL = 1000 * 1;
    public static final long FASTEST_INTERVAL = 1000 * 1;
    private static final String TAG = "LocationTracker";
    LocationRequest mLocationRequest;
    private static GoogleApiClient mGoogleApiClient = null;
    Activity mActivity;
    private static LocationTracker MLocationTracker = null;
    Map<String, LocationChangeListener> callback = new ConcurrentHashMap<String, LocationChangeListener>();

    public static LocationTracker GetLocationTrackerInstance() {
        if (MLocationTracker == null) {
            MLocationTracker = new LocationTracker();
        }
        return MLocationTracker;
    }

    private LocationTracker() {

    }

    public void RegisterLocationTracker(Activity act, LocationChangeListener calbak) {
        mActivity = act;
        callback.put(calbak.getClass().toString(), (LocationChangeListener) calbak);
        Log.d(TAG, "RegisterLocationTracker callback list sz  " + callback.size() + "," + callback);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(act.getApplicationContext())
                    .addApi(LocationServices.API).addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).build();
            mGoogleApiClient.connect();
        }
        createLocationRequest();
    }

    public void UnRegisterLocationTracker(String classname) {
        mActivity = null;
        callback.remove(classname);
        Log.d(TAG, "UnRegisterLocationTracker list sz  " + callback.size() + "," + callback);
        if (callback.size() > 0) {//it should be the case.
            callback.clear();
        }
        if (callback.size() == 0) {
            stopLocationUpdates();
            if (mGoogleApiClient != null)
                mGoogleApiClient.disconnect();
            mGoogleApiClient = null;
            mLocationRequest = null;
        }
    }

    //related to current location
    @SuppressLint("RestrictedApi")
    protected void createLocationRequest() {
        if (mLocationRequest == null) {
            mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(INTERVAL);
            mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onConnected(Bundle connectionHint) {
        if (mGoogleApiClient.isConnected()) {
            Log.d(TAG, "Google_Api_Client: It was connected on (onConnected) function, working as it should.");
            startLocationUpdates();
        } else {
            Log.d(TAG, "Google_Api_Client: It was NOT connected on (onConnected) function, It is definetly bugged.");
        }

    }

    public void startLocationUpdates() {
        if (mGoogleApiClient.isConnected()) {

            PendingResult<Status> pendingResult = LocationServices.FusedLocationApi
                    .requestLocationUpdates(mGoogleApiClient, mLocationRequest,
                            this);
            Log.d(TAG, "Location update started ..............: ");

            //Scuba Update location

            if (hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    && hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                Location lastLocation = LocationServices.FusedLocationApi
                        .getLastLocation(mGoogleApiClient);
//                if (lastLocation != null) {
//                    LocationHolder.get()
//                            .update(lastLocation);
//                }
            }
        }
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onLocationChanged(Location location) {
        Log.v(TAG, "Firing onLocationChanged............................");
        if (location == null)
            return;
        Log.v(TAG, "Location :- " + location.getLatitude() + " :- " + location.getLongitude());

        if (location.getLatitude() == 0.0 || location.getLongitude() == 0.0)
            return;
				/*Log.d(TAG,"Location provider :"+location.getProvider());
				Log.d(TAG,"Location speed meter/second :"+location.getSpeed());
				Log.d(TAG,"Location accuracy :"+location.getAccuracy());*/

        if (callback != null) {
            Set<Entry<String, LocationChangeListener>> mapSet = callback.entrySet();
            Iterator<Entry<String, LocationChangeListener>> mapIterator = mapSet.iterator();

            while (mapIterator.hasNext()) {
                Entry<String, LocationChangeListener> mapEntry = mapIterator.next();
                String keyValue = mapEntry.getKey();
                if (keyValue != null) {
                    callback.get(keyValue).onCustomLocationChanged(location);
                }
            }
        }
        //Scuba Update location
       // LocationHolder.get().update(location);
        Log.v(TAG, "Location Latitude :" + location.getLatitude());
        Log.v(TAG, "Location Longitude : " + location.getLongitude());
    }

    private void stopLocationUpdates() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            Log.d(TAG, "Location update stopped .......................");
            mGoogleApiClient.disconnect();
        }
    }
    //END OF CURRENT LOCATION code

    private boolean hasPermission(String permission) {
        boolean checkPermission = false;
        try {
            if (ActivityCompat.checkSelfPermission(mActivity, permission)
                    == PackageManager.PERMISSION_GRANTED)
                checkPermission = true;
        } catch (Exception e) {
            Log.e(TAG, "Exception in permission check");

        }
        return checkPermission;
    }

    /**
     * Determines whether one Location reading is better than the current Location fix
     *
     * @param location            The new Location that you want to evaluate
     * @param currentBestLocation The current Location fix, to which you want to compare the new one
     */
    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > INTERVAL;
        boolean isSignificantlyOlder = timeDelta < -INTERVAL;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /**
     * Checks whether two providers are the same
     */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }
}
