package com.usabilla;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.usabilla.FakeR;
import com.usabilla.sdk.ubform.Usabilla;
import com.webileapps.fragments.CordovaFragment;

public class MainActivity extends AppCompatActivity
{
    private static final String TAG = "MainActivity";

    public CordovaFragment currentFragment;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        FakeR fakeR = new FakeR(this);
        this.setTheme(fakeR.getId("style", "Theme.AppCompat.Light.NoActionBar"));

        //Set status bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.BLACK);
        }

        currentFragment = new CordovaFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(android.R.id.content, currentFragment);
        ft.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        final Usabilla usabilla = Usabilla.Companion.getInstance(this);
        usabilla.updateFragmentManager(getSupportFragmentManager());
        currentFragment.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (this.currentFragment != null) {
            this.currentFragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        currentFragment.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        currentFragment.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentFragment.onStart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        currentFragment.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[],
                                           int[] grantResults) {
        currentFragment.onRequestPermissionsResult(requestCode,permissions,grantResults);
    }
}
