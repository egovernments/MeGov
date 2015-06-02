package com.egov.android.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.egov.android.R;

public class SplashActivity extends BaseActivity implements Runnable {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(this, 2000);
    }

    @Override
    public void run() {
        startActivity(new Intent(this, RegionActivity.class));
        finish();
    }
}