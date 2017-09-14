package com.cga;

import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;
import android.util.Log;
import android.content.Intent;
import com.cga.UsabillaActivity;

public class Usabilla extends CordovaPlugin {
    public static String TAG = "Usabilla";
    private CallbackContext callbackContext;
    @Override
    public boolean execute(String action, JSONArray data, CallbackContext callbackContext) throws JSONException {
        this.callbackContext = callbackContext;
        Intent intent = null;

        Log.d(TAG, "testing");
        Log.d(TAG, action);
        if (action.equals("feedback")) {
            String formId = data.getString(0);
            intent = new Intent(cordova.getActivity(), UsabillaActivity.class);
            intent.putExtra("FORM_ID", formId);

            if (this.cordova != null) {
              this.cordova.startActivityForResult((CordovaPlugin) this, intent, 0);
            }
            return true;

        } else {
            return false;

        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
      if(resultCode == 5){
        this.callbackContext.error("GENERAL_ERROR");
      } else {
        this.callbackContext.success();  
      }
    }
}
