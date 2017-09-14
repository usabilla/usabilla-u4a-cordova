package com.cga;

import com.cga.usabilla.FakeR;
import android.widget.Button;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import android.app.Activity;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import com.usabilla.sdk.ubform.UBFormClient;
import com.usabilla.sdk.ubform.UBFormInterface;
import com.usabilla.sdk.ubform.controllers.Form;
import com.usabilla.sdk.ubform.utils.ThemeConfig;
import android.util.Log;
import android.view.View.OnClickListener;
import android.view.View;

public class UsabillaActivity extends AppCompatActivity implements UBFormInterface {
    protected FakeR fakeR;
    private Form form;
    private MenuItem item;
    public static String TAG = "UsabillaActivity";
    protected Button cancelButton;
    protected Button submitButton;

    protected void initButtons() {
      cancelButton = (Button) findViewById(fakeR.getId("id", "cancelButton"));
      cancelButton.setOnClickListener( new OnClickListener() {
        @Override
        public void onClick(View v) {
          setResult(RESULT_OK, null);
          finish();
        }
      });

      submitButton = (Button) findViewById(fakeR.getId("id", "submitButton"));
      submitButton.setOnClickListener( new OnClickListener() {
        @Override
        public void onClick(View v) {
          form.navigationButtonPushed();
        }
      });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        fakeR = new FakeR(this);
        super.onCreate(savedInstanceState);
        setContentView(fakeR.getId("layout", "usabilla_activity"));
        initButtons();
        UBFormClient.initClient(getApplicationContext());
        String formId = getIntent().getStringExtra("FORM_ID");
        UBFormClient.loadFeedbackForm(formId, getApplicationContext(), UsabillaActivity.this);
        setUpBroadcastReceivers();
    }

    private void setUpBroadcastReceivers() {
        BroadcastReceiver mCloser;

        mCloser = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                UsabillaActivity.this.setResult(RESULT_OK, null);
                UsabillaActivity.this.finish();
            }
        };

        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mCloser, new IntentFilter("com.usabilla.closeForm"));
    }

    @Override
    public void formLoadedSuccessfully(Form form) {
        this.form = form;
        ThemeConfig themeConfig = form.getThemeConfig();
        //I will use the action bar to hold the navigation button
        form.hideDefaultNavigationButton(true);
        form.hideCancelButton(true);

        getSupportFragmentManager().beginTransaction().add(fakeR.getId("id", "container"), form).commit();
    }

    @Override
    public void formFailedLoading(Form form) {
        setResult(5, null);
        finish();
    }

    @Override
    public void textForMainButtonUpdated(String text) {
        if (item != null) {
          item.setTitle(text);
        }
    }
}