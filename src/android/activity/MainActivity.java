package com.usabilla;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import io.cordova.hellocordova.R;
import uk.co.reallysmall.cordova.plugin.fragment.CordovaFragment;

public class MainActivity extends AppCompatActivity {

    public CordovaFragment currentFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final FakeR fakeR = new FakeR(this);
        setTheme(fakeR.getId("style", "Theme.AppCompat.Light.NoActionBar"));
        setContentView(fakeR.getId("layout", "usabilla_activity"));

        //Set status bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.BLACK);
        }

        currentFragment = new CordovaFragment();
        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.mainContainer, currentFragment);
        ft.commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        currentFragment.onResume();
    }
	
	@Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        currentFragment.onNewIntent(intent);
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
}
