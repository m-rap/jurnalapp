package com.mrap.jurnalapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Insets;
import android.graphics.Point;
import android.os.Build;
import android.view.WindowInsets;
import android.view.WindowMetrics;

public class Util {
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
}
