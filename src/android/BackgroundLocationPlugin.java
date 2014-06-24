package com.justinbaumgartner.cordova.geolocationservice;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class GeoLocationService extends CordovaPlugin {

	@Override
	public void initialize(CordovaInterface cordova, CordovaWebView webView) {
		super.initialize(cordova, webView);
	}
	
	@Override
	public void execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
		if (action.equals("test")) {
			Log.d("test called");
			return true;
		}
		else if (action.equals("deviceready")) {
			Log.d("deviceready called")
			return true;
		}
		return false;
	}
}