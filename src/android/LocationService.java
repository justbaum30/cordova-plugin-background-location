package com.justinbaumgartner.cordova.backgroundlocation;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;

public class LocationService extends Service implements 
		GooglePlayServicesClient.ConnectionCallbacks, 
		GooglePlayServicesClient.OnConnectionFailedListener {
	
	private LocationClient locationClient;
	private boolean servicesAvailable = false;
	
	private final IBinder binder = new LocalBinder();
	private static final String TAG = "LocationService";
	
	
	@Override
	public void onCreate(){
		super.onCreate();
		
		servicesAvailable = isServicesConnected();
		locationClient = new LocationClient(this, this, this);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		
		setupLocationClient();
		if (!locationClient.isConnected() || !locationClient.isConnecting())
			locationClient.connect();
		
		return START_STICKY;
	}
	
	/**
     * Check if user has connection to Google Play services
     */
	public boolean isServicesConnected() {
        // Check that Google Play services is available
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        
        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            Log.d(TAG, "Google Play services is available.");
            return true;
        }
        // Google Play services was not available for some reason
        else {
            Log.e(TAG, "Google Play services is not available with result code - " + resultCode);
            return false;
        }
    }
	
	/**
	 * 
	 */
	private void setupLocationClient() {
		if (locationClient == null)
			locationClient = new LocationClient(this, this, this);
	}
	
	/**
	 * 
	 */
	public Location getCurrentLocation() {
		return locationClient.getLastLocation();
	}
	
	@Override
	public void onDestroy(){
		if (locationClient != null)
			locationClient = null;
		
		super.onDestroy();
	}
	
	/**
     * Called by Location Services when the request to connect the
     * client finishes successfully. At this point, you can
     * request the current location or start periodic updates
     */
	@Override
	public void onConnected(Bundle data) {
		Log.d(TAG, "Location services connected.");
	}

	/**
     * Called by Location Services if the connection to the
     * location client drops because of an error.
     */
	@Override
	public void onDisconnected() {
		Log.d(TAG, "Location services disconnected.");
	}
	
	/**
     * Called by Location Services if the attempt to
     * Location Services fails.
     */
	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
//		// Check if Google Play services can resolve the error.
//        if (connectionResult.hasResolution()) {
//            try {
//            	
//                // Start an Activity that tries to resolve the error
//                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
//            }
//            // Thrown if Google Play services canceled the original PendingIntent
//            catch (IntentSender.SendIntentException e) {
//                // Log the error
//                e.printStackTrace();
//            }
//        }
//        // No resolution available
//        else {
//            Log.e(TAG, "Connection to location services failed with no resolution.");
//        }
		Log.e(TAG, "Connection to location services failed.");
	}
	
	public class LocalBinder extends Binder {
		LocationService getService() {
			return LocationService.this;
		}
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	
}
