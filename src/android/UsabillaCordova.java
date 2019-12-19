package com.usabilla;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.support.v4.content.LocalBroadcastManager;
import com.usabilla.sdk.ubform.UbConstants;
import com.usabilla.sdk.ubform.Usabilla;
import com.usabilla.sdk.ubform.UsabillaReadyCallback;
import com.usabilla.sdk.ubform.sdk.entity.FeedbackResult;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class UsabillaCordova extends CordovaPlugin implements UsabillaReadyCallback {

    private static final String APP_ID = "APP_ID";
    private static final String EVENT_NAME = "EVENT_NAME";
    private static final String FORM_ID = "FORM_ID";
    private static final String SCREENSHOT_NAME = "screenshot";
    private static final String MASKS = "MASKS";
    private static final String MASK_CHAR = "MASK_CHAR";
    private static final String CUSTOM_VARS = "CUSTOM_VARS";

    private static final String KEY_RATING = "rating";
    private static final String KEY_ABANDONED_PAGE_INDEX = "abandonedPageIndex";
    private static final String KEY_SENT = "sent";

    private IntentFilter closeCampaignFilter = new IntentFilter(UbConstants.INTENT_CLOSE_CAMPAIGN);
    private BroadcastReceiver receiverCampaignClosed = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
           if (intent != null) {
               final JSONObject result = prepareResult(intent);
               callbackContext.success(result);
           }
        }
    };

    private CallbackContext callbackContext;
    private String appId;
    private String formId;

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
            case "dismiss":
                dismiss();
                return true;
            case "setDataMasking":
                setDataMasking(data.getJSONObject(0));
                return true;
            case "getDefaultDataMasks":
                getDefaultDataMasks();
                return true;
            default:
                return false;
        }
    }

    private JSONObject getResult(Intent intent) {
        final FeedbackResult res = intent.getParcelableExtra(FeedbackResult.INTENT_FEEDBACK_RESULT);
        final JSONObject result = new JSONObject();
        try {
            result.put(KEY_RATING, res.getRating());
            result.put(KEY_ABANDONED_PAGE_INDEX, res.getAbandonedPageIndex());
            result.put(KEY_SENT, res.isSent());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 5) {
            callbackContext.error("GENERAL_ERROR");
            return;
        }
        if (data != null) {
            final JSONObject result = prepareResult(data);
            callbackContext.success(result);
        } else {
            final JSONObject result = new JSONObject();
            try {
                result.put("completed", resultCode != Activity.RESULT_CANCELED);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            callbackContext.success(result);
        }
    }

    private void initialize(HashMap<String, Object> customVars, String appId) {
        Usabilla.INSTANCE.initialize(cordova.getActivity(), appId, null, this);
        Usabilla.INSTANCE.setCustomVariables(customVars);
        LocalBroadcastManager.getInstance(cordova.getActivity()).registerReceiver(receiverCampaignClosed, closeCampaignFilter);
    }

    @Override
    public void onUsabillaInitialized() {
        Usabilla.INSTANCE.updateFragmentManager(((FragmentActivity) cordova.getActivity()).getSupportFragmentManager());
        UsabillaCordova.this.onActivityResult(0, Activity.RESULT_OK, null);
    }

    private void loadForm(JSONObject data, boolean withScreenshot) throws JSONException {
        final Intent intent = new Intent(cordova.getActivity(), UsabillaActivity.class);
        parseOptions(data);
        if (formId != null) {
            intent.putExtra(FORM_ID, formId);
        }
        if (withScreenshot) {
            final Bitmap screenshot = Usabilla.INSTANCE.takeScreenshot(cordova.getActivity());
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
        Usabilla.INSTANCE.resetCampaignData(cordova.getActivity(), this);
    }

    private void sendEvent(String eventName) {
        Usabilla.INSTANCE.sendEvent(cordova.getActivity(), eventName);
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
                } else if ((CUSTOM_VARS).equals(key)) {
                    Log.d("CUSTOM_VARS",value.toString());
                } else {
                    customVars.put(key, value.toString());
                }
            }
        }
        return customVars;
    }

    private void dismiss() {
        if (Usabilla.INSTANCE.dismiss(cordova.getActivity())) {
            callbackContext.success();
            return;
        }
        callbackContext.error("No forms to dismiss");
    }

    private void setDataMasking(JSONObject data) throws JSONException {
        final JSONArray masks = data.getJSONArray(MASKS);
        final String maskCharacter = data.getString(MASK_CHAR);
        List<String> maskList = new ArrayList<>();

        for (int i = 0; i < masks.length(); i++) {
            maskList.add(masks.getString(i));
        }

        Usabilla.INSTANCE.setDataMasking(maskList, maskCharacter.charAt(0));
    }

    private void getDefaultDataMasks() {
        try {
            final JSONObject result = new JSONObject();
            result.put("completed", UbConstants.getDefaultDataMasks());
            callbackContext.success(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private JSONObject prepareResult(Intent intent) {
        final JSONObject result = new JSONObject();
        final JSONObject resultData = new JSONObject();
        try {
            resultData.put("results", getResult(intent));
            result.put("completed", resultData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }
}
