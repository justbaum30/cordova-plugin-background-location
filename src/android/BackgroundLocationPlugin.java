package com.justinbaumgartner.cordova.backgroundlocation;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.widget.Toast;

public class BackgroundLocationPlugin extends CordovaPlugin {
	
	public LocationService service;
	private ServiceConnection connection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			LocationService.LocalBinder binder = (LocationService.LocalBinder)service;
			BackgroundLocationPlugin.this.service = binder.getService();
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
		}
	};
	
	private final static String TAG = "BackgroundLocationPlugin";
	

	@Override
	public void initialize(CordovaInterface cordova, CordovaWebView webView) {
		super.initialize(cordova, webView);
		
		Intent intent = new Intent(cordova.getActivity(), LocationService.class);
		cordova.getActivity().bindService(intent, connection, Context.BIND_AUTO_CREATE);
	}
	
	@Override
	public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
		if (action.equals("test")) {
			Toast.makeText(webView.getContext(), "test called", Toast.LENGTH_SHORT).show();
			return true;
		}
		else if (action.equals("deviceready")) {
			Toast.makeText(webView.getContext(), "device is ready", Toast.LENGTH_SHORT).show();
			return true;
		}
		return false;
	}
	
	@Override
	public void onDestroy() {
		cordova.getActivity().unbindService(connection);
		super.onDestroy();
	}
}