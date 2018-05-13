package com.usabilla;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;

import com.usabilla.sdk.ubform.Usabilla;
import com.usabilla.sdk.ubform.UsabillaFormCallback;
import com.usabilla.sdk.ubform.sdk.form.FormClient;

public class UsabillaActivity extends AppCompatActivity implements UsabillaFormCallback{
    protected FakeR fakeR;

    private void setUpBroadcastReceivers() {
        BroadcastReceiver mCloser, mPlayStore;

        mCloser = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                UsabillaActivity.this.setResult(RESULT_OK, null);
                UsabillaActivity.this.finish();
            }
        };

        mPlayStore = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                final String appPackageName = getApplicationContext().getPackageName();
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException ex) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            }
        };

        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mCloser, new IntentFilter("com.usabilla.closeForm"));
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mPlayStore, new IntentFilter("com.usabilla.redirectToPlayStore"));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        fakeR = new FakeR(this);
        super.onCreate(savedInstanceState);
        setContentView(fakeR.getId("layout", "usabilla_activity"));

        setUpBroadcastReceivers();

        String formId = getIntent().getStringExtra("FORM_ID");
        final Usabilla usabilla = Usabilla.Companion.getInstance(this);
        usabilla.loadFeedbackForm(this, formId, null, null, this);
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
        // The form did not load (internet connection, invalid formId...)
    }

    @Override
    public void mainButtonTextUpdated(String text) {
    }
}