package com.cga;

import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;
import android.util.Log;
import android.content.Intent;
import com.cga.UsabillaActivity;
import org.json.JSONObject;
import java.util.Iterator;

public class Usabilla extends CordovaPlugin {
    public static String TAG = "Usabilla";
    private CallbackContext callbackContext;

    public void populateIntentValues(Intent intent, JSONArray data) {
        for (int i = 1; i < data.length(); i++) {
            try {
                JSONObject jsonObj = data.getJSONObject(i);

                Iterator<String> iter = jsonObj.keys();
                while (iter.hasNext()) {
                    String key = iter.next();
                    try {
                        String value = (String)jsonObj.get(key);
                        if (Boolean.parseBoolean(value)) {
                            intent.putExtra(key, Boolean.valueOf(value));
                        } else {
                            intent.putExtra(key, value);
                        }

                    } catch (JSONException e) {}
                }
            } catch (JSONException e) {}
        }
    }

    @Override
    public boolean execute(String action, JSONArray data, CallbackContext callbackContext) throws JSONException {
        this.callbackContext = callbackContext;
        Intent intent = null;

        if (action.equals("feedback")) {
            intent = new Intent(cordova.getActivity(), UsabillaActivity.class);

            this.populateIntentValues(intent, data);
            String formId = data.getString(0);
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
