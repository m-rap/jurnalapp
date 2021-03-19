package com.mrap.jurnalapp;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import androidx.annotation.Nullable;

public class JnlActivity extends Activity {
    private static final String TAG = "JnlActivity";
    ArrayList<View> modals = new ArrayList<>();
    ArrayList<ViewGroup> roots = new ArrayList<>();

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

    @Override
    public void onBackPressed() {
        if (modals.size() > 0) {
            int i = modals.size() - 1;
            ModalUtil modalUtil = new ModalUtil();
            modalUtil.removeModal(this, i);
        } else {
            super.onBackPressed();
        }
    }
}
