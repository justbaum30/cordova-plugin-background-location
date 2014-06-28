package com.justinbaumgartner.cordova.backgroundlocation;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * Storage for geofence values, implemented in SharedPreferences.
 */
public class SimpleGeofenceStore {
    // Keys for flattened geofences stored in SharedPreferences
    public static final String KEY_LATITUDE = "com.justinbaumgartner.cordova.backgroundlocation.KEY_LATITUDE";
    public static final String KEY_LONGITUDE = "com.justinbaumgartner.cordova.backgroundlocation.KEY_LONGITUDE";
    public static final String KEY_RADIUS = "com.justinbaumgartner.cordova.backgroundlocation.KEY_RADIUS";
    public static final String KEY_EXPIRATION_DURATION = "com.justinbaumgartner.cordova.backgroundlocation.KEY_EXPIRATION_DURATION";
    public static final String KEY_TRANSITION_TYPE = "com.justinbaumgartner.cordova.backgroundlocation.KEY_TRANSITION_TYPE";
    public static final String KEY_PREFIX = "com.justinbaumgartner.cordova.backgroundlocation.KEY";
    
    // Invalid values, used to test geofence storage when retrieving geofences
    public static final long INVALID_LONG_VALUE = -999l;
    public static final float INVALID_FLOAT_VALUE = -999.0f;
    public static final int INVALID_INT_VALUE = -999;
    
    private final SharedPreferences mPrefs;
    private static final String SHARED_PREFERENCES = "BackgroundLocationGeofences";
    
    
    // Create the SharedPreferences storage with private access only
    public SimpleGeofenceStore(Context context) {
        mPrefs = context.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
    }
    
    /**
     * Returns a stored geofence by its id, or returns null if it's not found.
     *
     * @param id The ID of a stored geofence
     * @return A geofence defined by its center and radius
     */
    public SimpleGeofence getGeofence(String id) {
    	// Gets the value of each property, or an invalid default value if it does not exist
        double latitude = mPrefs.getFloat(getGeofenceFieldKey(id, KEY_LATITUDE), INVALID_FLOAT_VALUE);
        double longitude = mPrefs.getFloat(getGeofenceFieldKey(id, KEY_LONGITUDE), INVALID_FLOAT_VALUE);
        float radius = mPrefs.getFloat(getGeofenceFieldKey(id, KEY_RADIUS), INVALID_FLOAT_VALUE);
        long expirationDuration = mPrefs.getLong(getGeofenceFieldKey(id, KEY_EXPIRATION_DURATION), INVALID_LONG_VALUE);
        int transitionType = mPrefs.getInt(getGeofenceFieldKey(id, KEY_TRANSITION_TYPE), INVALID_INT_VALUE);
        
        // If none of the values are incorrect, return the object
        if (latitude != SimpleGeofenceStore.INVALID_FLOAT_VALUE &&
            longitude != SimpleGeofenceStore.INVALID_FLOAT_VALUE &&
            radius != SimpleGeofenceStore.INVALID_FLOAT_VALUE &&
            expirationDuration != SimpleGeofenceStore.INVALID_LONG_VALUE &&
            transitionType != SimpleGeofenceStore.INVALID_INT_VALUE) {

            // Return a true Geofence object
            return new SimpleGeofence(id, latitude, longitude, radius, expirationDuration, transitionType);
        }
        // Otherwise, return null.
        else {
            return null;
        }
    }
    
    /**
     * Save a geofence to shared preferences
     * 
     * @param id The ID of a Geofence object
     * @param geofence The SimpleGeofence containing the values you want to save in SharedPreferences
     */
    public void addGeofence(SimpleGeofence geofence) {
    	String id = geofence.getId();
    	
        // Get a SharedPreferences editor instance
        Editor editor = mPrefs.edit();
        
        // Write the Geofence values to SharedPreferences
        editor.putFloat(getGeofenceFieldKey(id, KEY_LATITUDE), (float)geofence.getLatitude());
        editor.putFloat(getGeofenceFieldKey(id, KEY_LONGITUDE), (float)geofence.getLongitude());
        editor.putFloat(getGeofenceFieldKey(id, KEY_RADIUS), geofence.getRadius());
        editor.putLong(getGeofenceFieldKey(id, KEY_EXPIRATION_DURATION), geofence.getExpirationDuration());
        editor.putInt(getGeofenceFieldKey(id, KEY_TRANSITION_TYPE), geofence.getTransitionType());
        
        // Commit the changes
        editor.commit();
    }
    
    /**
     * Remove a geofence from shared preferences
     * 
     * @param id The ID of a Geofence object
     */
    public void removeGeofence(String id) {
        // Remove a flattened geofence object from storage by removing all of its keys
        Editor editor = mPrefs.edit();
        editor.remove(getGeofenceFieldKey(id, KEY_LATITUDE));
        editor.remove(getGeofenceFieldKey(id, KEY_LONGITUDE));
        editor.remove(getGeofenceFieldKey(id, KEY_RADIUS));
        editor.remove(getGeofenceFieldKey(id, KEY_EXPIRATION_DURATION));
        editor.remove(getGeofenceFieldKey(id, KEY_TRANSITION_TYPE));
        editor.commit();
    }
    
    /**
     * Given a Geofence object's ID and the name of a field (for example, KEY_LATITUDE), 
     * return the key name of the object's values in SharedPreferences.
     *
     * @param id The ID of a Geofence object
     * @param fieldName The field represented by the key
     * @return The full key name of a value in SharedPreferences
     */
    private String getGeofenceFieldKey(String id, String fieldName) {
        return KEY_PREFIX + "_" + id + "_" + fieldName;
    }
}
