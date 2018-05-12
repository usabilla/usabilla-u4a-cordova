package com.cga;

import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;

import com.usabilla.sdk.ubform.Usabilla;
import com.usabilla.sdk.ubform.UsabillaReadyCallback;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

public class UsabillaCordova extends CordovaPlugin {
    public static String TAG = "Usabilla";
    private CallbackContext callbackContext;
    private Usabilla usabilla;
    private boolean appInit = false;
    private String formId;
    private String appId;

    public HashMap<String, Object> parseOptions(JSONObject dataObj) throws JSONException {
        if (dataObj == null) {
            new HashMap<String, Object>();
        }

        HashMap<String, Object> customVars = new HashMap<String, Object>();
        for (Iterator<String> it = dataObj.keys(); it.hasNext(); ) {
            String key = it.next();
            Object value = dataObj.get(key);

            if ("FORM_ID".equals(key)) {
                this.formId = (String)value;
            } else if (("APP_ID").equals(key)) {
                this.appId = (String)value;
            } else {
                customVars.put(key, value);
            }
        }
        return customVars;
    }


    @Override
    public boolean execute(String action, JSONArray data, CallbackContext callbackContext) throws JSONException {
        this.callbackContext = callbackContext;

        if (action.equals("feedback")) {
            Intent intent = new Intent(cordova.getActivity(), UsabillaActivity.class);
            intent.putExtra("FORM_ID", this.formId);

            if (this.cordova != null) {
                this.cordova.startActivityForResult( this, intent, 0);
            }
            return true;

        } else if (action.equals("initApp")) {
            HashMap<String, Object> customVars = this.parseOptions((JSONObject) data.get(0));
            this.initApp(customVars);
            return true;
        } else if (action.equals("resetCampaing")) {
            this.resetCampaign();
            return true;
        } else if (action.equals("sendEvent")) {
            JSONObject dataObj = (JSONObject) data.get(0);
            this.sendEvent((String)dataObj.get("EVENT_ID"));
            return true;
        } else {
            return false;
        }
    }

    public void initApp(HashMap<String, Object> customVars) {
        usabilla = Usabilla.Companion.getInstance(cordova.getActivity());
        usabilla.initialize(this.cordova.getActivity(), this.appId, new UsabillaReadyCallback() {
            @Override
            public void onUsabillaInitialized() {
                usabilla.updateFragmentManager(((FragmentActivity)UsabillaCordova.this.cordova.getActivity()).getSupportFragmentManager());
                UsabillaCordova.this.onActivityResult(0, Activity.RESULT_OK, null);
            }
        });
        usabilla.setCustomVariables(customVars);
    }

    public void sendEvent(String eventName) {
        usabilla.sendEvent(this.cordova.getActivity(), eventName);
    }

    public void resetCampaign() {
        usabilla.resetCampaignData(this.cordova.getActivity(), new UsabillaReadyCallback() {
            @Override
            public void onUsabillaInitialized() {
                UsabillaCordova.this.onActivityResult(0, Activity.RESULT_OK, null);
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
