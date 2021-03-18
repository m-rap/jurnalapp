package com.mrap.jurnalapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Insets;
import android.graphics.Point;
import android.os.Build;
import android.util.Log;
import android.view.WindowInsets;
import android.view.WindowMetrics;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class Util {
    private static final String TAG = "Util";
    private Context context;

    public Util(Context context) {
        this.context = context;
    }

    public float convertPixtoDip(int pixel){
        float scale = context.getResources().getDisplayMetrics().density;
        return (float)pixel/scale;
    }

    public float convertDipToPix(int dip){
        float scale = context.getResources().getDisplayMetrics().density;
        return (float)dip * scale;
    }

    Point getDisplaySize() {
        Point size = new Point();
        Activity activity = (Activity)context;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            WindowMetrics windowMetrics = activity.getWindowManager().getCurrentWindowMetrics();
//            Insets insets = windowMetrics.getWindowInsets()
//                    .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars());
//            size.x = windowMetrics.getBounds().width() - insets.left - insets.right;
//            size.y = windowMetrics.getBounds().width() - insets.top - insets.bottom;
//            if (size.x < 0) size.x *= -1;
//            if (size.y < 0) size.y *= -1;
//            return size;
//        }
        activity.getWindowManager().getDefaultDisplay().getSize(size);
        return size;
    }

    public void loadLocale(Configuration config) {
        try {
            FileInputStream fis = new FileInputStream(context.getExternalFilesDir(null) + "/lang.txt");
            byte[] buff = new byte[1048];
            int len = fis.read(buff, 0, buff.length);
            String languageCode = new String(buff, 0, len);
            applyLocale(languageCode, config);
            fis.close();
            Log.d(TAG, "locale loaded " + languageCode);
        } catch (Exception e) {
            saveLocale("in", config);
        }
    }

    public void saveLocale(String languageCode, Configuration config) {
        applyLocale(languageCode, config);
        try {
            PrintWriter printWriter = new PrintWriter(context.getExternalFilesDir(null) + "/lang.txt");
            printWriter.print(languageCode);
            printWriter.close();
            Log.d(TAG, "locale saved " + languageCode);
        } catch (FileNotFoundException e) { }

    }

    private void applyLocale(String languageCode, Configuration config) {
        Log.d(TAG, "apply locale " + languageCode);
        Locale locale = new Locale(languageCode);
        //Locale.setDefault(locale);
        Resources resources = context.getResources();
        if (config == null)
            config = resources.getConfiguration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Log.d(TAG, "setLocale " + locale);
            config.setLocale(locale);
        } else{
            Log.d(TAG, "locale = " + locale);
            config.locale = locale;
        }
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N){
            context.getApplicationContext().createConfigurationContext(config);
        } else {
            resources.updateConfiguration(config, resources.getDisplayMetrics());
        }
    }
}
