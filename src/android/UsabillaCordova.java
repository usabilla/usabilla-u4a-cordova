package com.usabilla;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.FragmentActivity;

import com.usabilla.sdk.ubform.Usabilla;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Iterator;

public class UsabillaCordova extends CordovaPlugin {

    private static final String APP_ID = "APP_ID";
    private static final String EVENT_NAME = "EVENT_NAME";
    private static final String FORM_ID = "FORM_ID";
    private static final String SCREENSHOT_NAME = "screenshot";

    private CallbackContext callbackContext;
    private String appId;
    private String formId;
    private Usabilla usabilla;

    @Override
    public boolean execute(String action, JSONArray data, CallbackContext callbackContext) throws JSONException {
        this.callbackContext = callbackContext;
        switch (action) {
            case "initialize":
                final HashMap<String, Object> customVars = parseOptions((JSONObject) data.get(0));
                initialize(customVars, appId);
                return true;
            case "loadFeedbackForm":
                loadForm((JSONObject) data.get(0), false);
                return true;
            case "loadFeedbackFormWithCurrentViewScreenshot":
                loadForm((JSONObject) data.get(0), true);
                return true;
            case "resetCampaignData":
                resetCampaignData();
                return true;
            case "sendEvent":
                final JSONObject dataObj = (JSONObject) data.get(0);
                sendEvent((String) dataObj.get(EVENT_NAME));
                return true;
            default:
                return false;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 5) {
            callbackContext.error("GENERAL_ERROR");
        } else {
            try {
                JSONObject result = new JSONObject();
                result.put("completed", resultCode != Activity.RESULT_CANCELED);
                callbackContext.success(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            callbackContext.success();
        }
    }

    private void initialize(HashMap<String, Object> customVars, String appId) {
        usabilla = Usabilla.Companion.getInstance(cordova.getActivity());
        usabilla.initialize(cordova.getActivity(), appId, () -> {
            usabilla.updateFragmentManager(((FragmentActivity) cordova.getActivity()).getSupportFragmentManager());
            UsabillaCordova.this.onActivityResult(0, Activity.RESULT_OK, null);
        });
        usabilla.setCustomVariables(customVars);
    }

    private void loadForm(JSONObject data, boolean withScreenshot) throws JSONException {
        final Intent intent = new Intent(cordova.getActivity(), UsabillaActivity.class);
        parseOptions(data);
        if (formId != null) {
            intent.putExtra(FORM_ID, formId);
        }
        if (withScreenshot) {
            final Bitmap screenshot = usabilla.takeScreenshot(cordova.getActivity());
            if (screenshot != null) {
                try (FileOutputStream out = cordova.getContext().openFileOutput(SCREENSHOT_NAME, Context.MODE_PRIVATE)) {
                    screenshot.compress(Bitmap.CompressFormat.PNG, 100, out);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        cordova.startActivityForResult(this, intent, 0);
    }

    private void resetCampaignData() {
        usabilla.resetCampaignData(cordova.getActivity(), () -> UsabillaCordova.this.onActivityResult(0, Activity.RESULT_OK, null));
    }

    private void sendEvent(String eventName) {
        usabilla.sendEvent(cordova.getActivity(), eventName);
        this.onActivityResult(0, Activity.RESULT_OK, null);
    }

    private HashMap<String, Object> parseOptions(JSONObject dataObj) throws JSONException {
        final HashMap<String, Object> customVars = new HashMap<>();
        if (dataObj != null) {
            for (Iterator<String> it = dataObj.keys(); it.hasNext(); ) {
                final String key = it.next();
                final Object value = dataObj.get(key);
                if (FORM_ID.equals(key)) {
                    formId = (String) value;
                } else if ((APP_ID).equals(key)) {
                    appId = (String) value;
                } else {
                    customVars.put(key, value);
                }
            }
        }
        return customVars;
    }
}
