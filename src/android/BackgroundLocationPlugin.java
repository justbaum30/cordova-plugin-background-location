package com.justinbaumgartner.cordova.backgroundlocation;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

import android.content.Intent;
import android.widget.Toast;

public class BackgroundLocationPlugin extends CordovaPlugin {
	
	private final static String TAG = "BackgroundLocationPlugin";
	
	@Override
	public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
		if (action.equals("test")) {
			Toast.makeText(cordova.getActivity(), "test called", Toast.LENGTH_SHORT).show();
			Intent serviceIntent = new Intent(cordova.getActivity(), LocationService.class);
			serviceIntent.putExtra(LocationService.IS_INITIALIZATION_KEY, true);
			cordova.getActivity().startService(serviceIntent);
			return true;
		}
		else if (action.equals("deviceready")) {
			Toast.makeText(cordova.getActivity(), "device is ready", Toast.LENGTH_SHORT).show();
			return true;
		}
		return false;
	}
}