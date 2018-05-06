package com.cga;

import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;

import com.usabilla.sdk.ubform.UsabillaReadyCallback;

import org.json.JSONObject;

import java.util.Iterator;

public class Usabilla extends CordovaPlugin {
    public static String TAG = "Usabilla";
    private CallbackContext callbackContext;

    public void populateIntentValues(Intent intent, JSONObject dataObj) throws JSONException {
        for (Iterator<String> it = dataObj.keys(); it.hasNext(); ) {
            String key = it.next();
            Object value = dataObj.get(key);
            if (value instanceof String) {
                intent.putExtra(key, (String) value);
            } else if (value instanceof Boolean) {
                intent.putExtra(key, (Boolean) value);
            }
        }
    }

    @Override
    public boolean execute(String action, JSONArray data, CallbackContext callbackContext) throws JSONException {
        this.callbackContext = callbackContext;

        if (action.equals("feedback")) {
            Intent intent = new Intent(cordova.getActivity(), UsabillaActivity.class);

            JSONObject dataObj = (JSONObject) data.get(0);
            this.populateIntentValues(intent, dataObj);

            if (this.cordova != null) {
                this.cordova.startActivityForResult( this, intent, 0);
            }
            return true;

        } else if (action.equals("feedback")) {
            this.resetCampaign();
            return true;
        } else {
            return false;

        }
    }

    public void resetCampaign() {
        final com.usabilla.sdk.ubform.Usabilla usabilla = com.usabilla.sdk.ubform.Usabilla.Companion.getInstance(this.cordova.getActivity());

        usabilla.resetCampaignData(this.cordova.getActivity(), new UsabillaReadyCallback() {
            @Override
            public void onUsabillaInitialized() {
                Usabilla.this.onActivityResult(0, Activity.RESULT_OK, null);
            }
        });

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 5) {
            this.callbackContext.error("GENERAL_ERROR");
        } else {
            try {
                JSONObject result = new JSONObject();
                result.put("completed", resultCode != Activity.RESULT_CANCELED);
                this.callbackContext.success(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            this.callbackContext.success();
        }
    }
}
