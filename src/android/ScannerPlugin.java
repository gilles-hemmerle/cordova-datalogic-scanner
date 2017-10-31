package com.iabsis.datalogic.alert;
 
import org.json.JSONArray;
import org.json.JSONException;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import android.R;
import android.content.Context;
import android.content.DialogInterface;
import android.app.AlertDialog;
import android.util.Log;
import org.apache.cordova.PluginResult;
import org.json.JSONObject;

import com.datalogic.decode.BarcodeManager;
import com.datalogic.decode.DecodeException;
import com.datalogic.decode.DecodeResult;
import com.datalogic.decode.ReadListener;
import com.datalogic.decode.StartListener;
import com.datalogic.decode.StopListener;
import com.datalogic.decode.TimeoutListener;

public class ScannerPlugin extends CordovaPlugin implements ReadListener, StartListener, StopListener, TimeoutListener {

    private final static String TAG = "BarcodeScanner";
    private static CallbackContext listener;
    private BarcodeManager barcodeManager = null;

    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if("listen".equals(action)){
            try {
                if (barcodeManager == null) {
                    barcodeManager = new BarcodeManager();
                    barcodeManager.addReadListener(this);
                    barcodeManager.addStartListener(this);
                    barcodeManager.addStopListener(this);
                    barcodeManager.addTimeoutListener(this);
                    Log.d(TAG, "Initialisation du BarcodeManager");
                }
                setListener(callbackContext);
            } catch (DecodeException e) {
                e.printStackTrace();
            }
        
            return true;
        } else {
            callbackContext.error("AlertPlugin."+action+" not found !");
            return false;
        }
    }
    
    public static void notifyListener(String eventType, DecodeResult decodeResult){
      if (listener == null) {
        Log.e(TAG, "Must define listener first. Call notificationListener.listen(success,error) first");
        return;
      }

      try  {

        JSONObject json = new JSONObject();
        json.put("type", eventType);
        json.put("barcode", decodeResult == null ? null : decodeResult.getText().trim());
        PluginResult result = new PluginResult(PluginResult.Status.OK, json);

        Log.i(TAG, "Sending notification to listener " + json.toString());
        result.setKeepCallback(true);

        listener.sendPluginResult(result);
      } catch (Exception e){
        Log.e(TAG, "Unable to send notification "+ e);
        listener.error(TAG+". Unable to send message: "+e.getMessage());
      }
    }

    public void setListener(CallbackContext callbackContext) {
      Log.i(TAG, "Attaching callback context listener " + callbackContext);
      listener = callbackContext;

      PluginResult result = new PluginResult(PluginResult.Status.NO_RESULT);
      result.setKeepCallback(true);
      callbackContext.sendPluginResult(result);
    }

    @Override
    public void onScanStarted() {
        Log.d(TAG, "onStart");
        notifyListener("scanStart", null);
    }

    @Override
    public void onScanTimeout() {
        Log.d(TAG, "onTimeout");
        notifyListener("scanTimeout", null);
    }

    @Override
    public void onScanStopped() {
        Log.d(TAG, "onStop" );
        notifyListener("scanStop", null);
    }

    @Override
    public void onRead(DecodeResult decodeResult) {
        Log.d(TAG, "onRead: " + decodeResult.getText());
        notifyListener("read", decodeResult);
    }

}