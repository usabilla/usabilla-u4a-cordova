package com.usabilla;

import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.usabilla.sdk.ubform.Usabilla;
import com.usabilla.sdk.ubform.UbConstants;
import com.usabilla.sdk.ubform.sdk.entity.FeedbackResult;
import uk.co.reallysmall.cordova.plugin.fragment.CordovaFragment;

public class MainActivity extends AppCompatActivity {

    private IntentFilter closeCampaignFilter = new IntentFilter(UbConstants.INTENT_CLOSE_CAMPAIGN);
    private BroadcastReceiver receiverCampaignClosed;

    public CordovaFragment currentFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final FakeR fakeR = new FakeR(this);
        setTheme(fakeR.getId("style", "Theme.AppCompat.Light.NoActionBar"));

        setUpBroadcastReceivers();

        //Set status bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.BLACK);
        }

        currentFragment = new CordovaFragment();
        final FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(android.R.id.content, currentFragment);
        ft.commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver(receiverCampaignClosed, closeCampaignFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiverCampaignClosed);
    }

    @Override
    protected void onResume() {
        super.onResume();
        currentFragment.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (currentFragment != null) {
            currentFragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        currentFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void setUpBroadcastReceivers() {
        receiverCampaignClosed = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                final FeedbackResult res = intent.getParcelableExtra(FeedbackResult.INTENT_FEEDBACK_RESULT_CAMPAIGN);
                String feedbackInfo = "Rating " + res.getRating() + "\n";
                feedbackInfo += "Abandoned page " + res.getAbandonedPageIndex() + "\n";
                feedbackInfo += "Is sent " + res.isSent();
                Toast.makeText(getApplicationContext(), feedbackInfo, Toast.LENGTH_SHORT).show();
            }
        };
    }
}
