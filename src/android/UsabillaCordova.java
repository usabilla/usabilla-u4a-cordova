package com.usabilla;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.usabilla.sdk.ubform.UbConstants;
import com.usabilla.sdk.ubform.Usabilla;
import com.usabilla.sdk.ubform.UsabillaFormCallback;
import com.usabilla.sdk.ubform.UsabillaReadyCallback;
import com.usabilla.sdk.ubform.sdk.entity.FeedbackResult;
import com.usabilla.sdk.ubform.sdk.form.FormClient;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


public class UsabillaCordova extends CordovaPlugin implements UsabillaReadyCallback, UsabillaFormCallback {

    private static final String FORM_IDs = "FORM_IDs";
    private static final String DEBUG_ENABLED = "DEBUG_ENABLED";
    public static final String FRAGMENT_TAG = "passive form";
    private static final String APP_ID = "APP_ID";
    private static final String EVENT_NAME = "EVENT_NAME";
    private static final String FORM_ID = "FORM_ID";
    private static final String MASKS = "MASKS";
    private static final String MASK_CHAR = "MASK_CHAR";
    private static final String CUSTOM_VARS = "CUSTOM_VARS";
    private static final String KEY_RATING = "rating";
    private static final String KEY_ABANDONED_PAGE_INDEX = "abandonedPageIndex";
    private static final String KEY_SENT = "sent";
    private static final String KEY_ERROR_MSG = "error";
    public Fragment passiveFormFragment;
    private IntentFilter closeCampaignFilter = new IntentFilter(UbConstants.INTENT_CLOSE_CAMPAIGN);
    private IntentFilter closeFormFilter = new IntentFilter(UbConstants.INTENT_CLOSE_FORM);
    private CallbackContext callbackContext;
    private BroadcastReceiver receiverCampaignClosed = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                final JSONObject result = prepareResult(intent, FeedbackResult.INTENT_FEEDBACK_RESULT_CAMPAIGN);
                callbackContext.success(result);
            }
        }
    };
    private BroadcastReceiver receiverFormClosed = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                final JSONObject result = prepareResult(intent, FeedbackResult.INTENT_FEEDBACK_RESULT);
                callbackContext.success(result);
            }

            final Activity activity = ((MainActivity) cordova.getActivity());
            if (activity instanceof FragmentActivity) {
                final FragmentManager supportFragmentManager = ((FragmentActivity) activity).getSupportFragmentManager();
                final Fragment fragment = supportFragmentManager.findFragmentByTag(FRAGMENT_TAG);

                if (fragment != null) {
                    supportFragmentManager.beginTransaction().remove(fragment).commit();
                }
            }
        }
    };
    private String appId;
    private String formId;
    private Usabilla usabilla = Usabilla.INSTANCE;

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(cordova.getActivity()).unregisterReceiver(receiverCampaignClosed);
        LocalBroadcastManager.getInstance(cordova.getActivity()).unregisterReceiver(receiverFormClosed);
    }

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
            case "preloadFeedbackForms":
                final JSONObject formObj = (JSONObject) data.get(0);
                preloadFeedbackForms((JSONArray) formObj.getJSONArray(FORM_IDs));
                return true;
            case "removeCachedForms":
                removeCachedForms();
                return true;
            case "setDebugEnabled":
                final JSONObject debugObj = (JSONObject) data.get(0);
                setDebugEnabled((Boolean) debugObj.get(DEBUG_ENABLED));
                return true;
            default:
                return false;
        }
    }

    private JSONObject getResult(Intent intent, String feedbackResultType) {
        final FeedbackResult res = intent.getParcelableExtra(feedbackResultType);
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
            final JSONObject result = prepareResult(data, FeedbackResult.INTENT_FEEDBACK_RESULT);
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

    @Override
    public void formLoadSuccess(FormClient form) {
        passiveFormFragment = form.getFragment();
        final Activity activity = ((MainActivity) cordova.getActivity());
        if (activity instanceof FragmentActivity && form != null) {
            ((FragmentActivity) activity).getSupportFragmentManager()
                    .beginTransaction()
                    .replace(android.R.id.content, passiveFormFragment, FRAGMENT_TAG)
                    .commit();
            passiveFormFragment = null;
        }
    }

    @Override
    public void formLoadFail() {
        final JSONObject result =  new JSONObject();
        try {
            result.put(KEY_ERROR_MSG, "The form could not be loaded");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        callbackContext.error(result);
    }

    @Override
    public void mainButtonTextUpdated(String text) {
        // To fill with handling required when the main button text changes
    }

    private void initialize(HashMap<String, Object> customVars, String appId) {
        usabilla.initialize(cordova.getActivity(), appId, null, this);
        usabilla.setCustomVariables(customVars);
        LocalBroadcastManager.getInstance(cordova.getActivity()).registerReceiver(receiverFormClosed, closeFormFilter);
        LocalBroadcastManager.getInstance(cordova.getActivity()).registerReceiver(receiverCampaignClosed, closeCampaignFilter);
    }

    @Override
    public void onUsabillaInitialized() {
        usabilla.updateFragmentManager(((FragmentActivity) cordova.getActivity()).getSupportFragmentManager());
        UsabillaCordova.this.onActivityResult(0, Activity.RESULT_OK, null);
    }

    private void loadForm(JSONObject data, boolean withScreenshot) throws JSONException {
        parseOptions(data);
        Bitmap screenshot = null;
        if (withScreenshot) {
            screenshot = usabilla.takeScreenshot(cordova.getActivity());
        }
        usabilla.loadFeedbackForm(formId, screenshot, null, this);
    }

    private void resetCampaignData() {
        usabilla.resetCampaignData(cordova.getActivity(), this);
    }

    private void sendEvent(String eventName) {
        usabilla.sendEvent(cordova.getActivity(), eventName);
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
                    Log.d("CUSTOM_VARS", value.toString());
                } else {
                    customVars.put(key, value);
                }
            }
        }
        return customVars;
    }

    private void dismiss() {
        if (usabilla.dismiss(cordova.getActivity())) {
            callbackContext.success();
            return;
        }
        callbackContext.error("No forms to dismiss");
    }

    private void preloadFeedbackForms(JSONArray formIDs) throws JSONException {
        List<String> formIdLists = new ArrayList<>();
        for (int i = 0; i < formIDs.length(); i++) {
            formIdLists.add(formIDs.getString(i));
        }
        usabilla.preloadFeedbackForms(formIdLists);
        callbackContext.success();
    }

    private void removeCachedForms() {
        usabilla.removeCachedForms();
        callbackContext.success();
    }

    private void setDebugEnabled(Boolean debugEnabled) {
        usabilla.setDebugEnabled(debugEnabled);
        callbackContext.success();
    }

    private void setDataMasking(JSONObject data) throws JSONException {
        final JSONArray masks = data.getJSONArray(MASKS);
        final String maskCharacter = data.getString(MASK_CHAR);
        List<String> maskList = new ArrayList<>();

        for (int i = 0; i < masks.length(); i++) {
            maskList.add(masks.getString(i));
        }

        usabilla.setDataMasking(maskList, maskCharacter.charAt(0));
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

    private JSONObject prepareResult(Intent intent, String feedbackResultType) {
        final JSONObject result = new JSONObject();
        final JSONObject resultData = new JSONObject();
        try {
            String res = (feedbackResultType == FeedbackResult.INTENT_FEEDBACK_RESULT) ? "results" : "result";
            resultData.put(res, getResult(intent, feedbackResultType));
            result.put("completed", resultData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }
}
