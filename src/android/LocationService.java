package com.justinbaumgartner.cordova.backgroundlocation;

import java.util.Arrays;
import java.util.List;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationClient.OnAddGeofencesResultListener;
import com.google.android.gms.location.LocationStatusCodes;

public class LocationService extends IntentService implements 
		GooglePlayServicesClient.ConnectionCallbacks, 
		GooglePlayServicesClient.OnConnectionFailedListener, 
		OnAddGeofencesResultListener {
	
	private LocationClient locationClient;
	private SimpleGeofenceStore geofenceStore;
	
	public final static String IS_INITIALIZATION_KEY = "isInit";
	
	private static final String TAG = "LocationService";
	
	
	public LocationService() {
		super(TAG);
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
		// Initialization service call
		if (intent.hasExtra(IS_INITIALIZATION_KEY)) {
			Log.d(TAG, "Initialization call.");
			
			// Abort if services not connected
			if (!isServicesConnected())
				return;
			
			geofenceStore = new SimpleGeofenceStore(this);
			setupLocationClient();
			locationClient.connect();
		}
		// User left geofence
		else {
			Log.d(TAG, "Geofence exit call.");
			
			// Errors
			if (LocationClient.hasError(intent)) {
				int errorCode = LocationClient.getErrorCode(intent);
				Log.e(TAG, "Location Services error: " + errorCode);
			}
			else {
				geofenceStore = new SimpleGeofenceStore(this);
				
				List<Geofence> triggeredGeofences = LocationClient.getTriggeringGeofences(intent);
				for (Geofence geofence : triggeredGeofences) {
					geofenceStore.removeGeofence(geofence.getRequestId());
				}
			}
		}
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
		Log.d(TAG, "Setting up the location client.");
		if (locationClient == null)
			locationClient = new LocationClient(this, this, this);
	}
	
	/**
	 * 
	 * @return
	 */
	private SimpleGeofence createGeofenceAtCurrentLocation() {
		Log.d(TAG, "Getting current location");
		Location currentLocation = locationClient.getLastLocation();
		Log.d(TAG, "Latitude: " + currentLocation.getLatitude() + ", Longitude: " + currentLocation.getLongitude());
		
		return new SimpleGeofence("1", 
				currentLocation.getLatitude(), 
				currentLocation.getLongitude(), 
				100, 
				Geofence.NEVER_EXPIRE, 
				Geofence.GEOFENCE_TRANSITION_EXIT);
	}
	
	private PendingIntent getTransitionPendingIntent() {
		Intent intent = new Intent(this, LocationService.class);
		return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
	}
	
	private void createNotification(){
//		Notification.Builder builder = new Notification.Builder(this)
//			.setSmallIcon(3)
//			.setContentTitle("")
//			.setContentText("");
	}
	
	/**
     * Called by Location Services when the request to connect the
     * client finishes successfully. At this point, you can
     * request the current location or start periodic updates
     */
	@Override
	public void onConnected(Bundle data) {
		Log.d(TAG, "Location services connected.");
		
		// Create a geofence object and add it to the store
		SimpleGeofence geofence = createGeofenceAtCurrentLocation();
		geofenceStore.addGeofence(geofence);
		
		// Send request to add geofences to the location client
		Log.d(TAG, "Attempting to add geofences to the location client.");
		List<Geofence> geofences = Arrays.asList(geofence.toGeofence());
		locationClient.addGeofences(geofences, getTransitionPendingIntent(), this);
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

	@Override
	public void onAddGeofencesResult(int statusCode, String[] geofenceRequestIds) {
		// Adding geofences was successful
		if (statusCode == LocationStatusCodes.SUCCESS) {
			Log.d(TAG, "Successfully added the geofences.");
		}
		else {
			Log.e(TAG, "Failed to add the geofences.");
			// Remove the failed geofences from the store
			for (String id : geofenceRequestIds) {
				geofenceStore.removeGeofence(id);
			}
		}
		
		locationClient.disconnect();
	}
}
