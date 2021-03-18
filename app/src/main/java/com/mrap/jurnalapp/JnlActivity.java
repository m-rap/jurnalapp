package com.mrap.jurnalapp;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;

public class JnlActivity extends Activity {
    @Override
    protected void attachBaseContext(Context base) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Util util = new Util(base);
            super.attachBaseContext(util.loadLocale(null));
        } else {
            super.attachBaseContext(base);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            Util util = new Util(this);
            util.loadLocale(null);
        }
    }
}
