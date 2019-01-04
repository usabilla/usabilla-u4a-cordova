package com.usabilla;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.usabilla.sdk.ubform.Usabilla;
import com.usabilla.sdk.ubform.UbConstants;
import com.usabilla.sdk.ubform.UsabillaFormCallback;
import com.usabilla.sdk.ubform.sdk.form.FormClient;

import java.io.FileInputStream;
import java.io.IOException;

public class UsabillaActivity extends AppCompatActivity implements UsabillaFormCallback {

    private static final String FORM_ID = "FORM_ID";
    private static final String SCREENSHOT_NAME = "screenshot";

    private IntentFilter closeFormFilter = new IntentFilter(UbConstants.INTENT_CLOSE_FORM);
    private BroadcastReceiver receiverFormClosed;

    protected FakeR fakeR;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        fakeR = new FakeR(this);
        super.onCreate(savedInstanceState);
        setContentView(fakeR.getId("layout", "usabilla_activity"));
        setUpBroadcastReceivers();
        final String formId = getIntent().getStringExtra(FORM_ID);
        Bitmap screenshot = null;
        try (FileInputStream in = openFileInput(SCREENSHOT_NAME)) {
            screenshot = BitmapFactory.decodeStream(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Usabilla.INSTANCE.loadFeedbackForm(formId, screenshot, null, this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver(receiverFormClosed, closeFormFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiverFormClosed);
    }

    @Override
    public void formLoadSuccess(FormClient form) {
        getSupportFragmentManager()
                .beginTransaction()
                .add(fakeR.getId("id", "container"), form.getFragment())
                .commit();
    }

    @Override
    public void formLoadFail() {
        // To fill with handling required when the form fails to load
    }

    @Override
    public void mainButtonTextUpdated(String text) {
        // To fill with handling required when the main button text changes
    }

    private void setUpBroadcastReceivers() {
        receiverFormClosed = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                UsabillaActivity.this.setResult(RESULT_OK, null);
                UsabillaActivity.this.finish();
                Toast.makeText(getApplicationContext(), "closed form", Toast.LENGTH_SHORT).show();
            }
        };
    }
}
