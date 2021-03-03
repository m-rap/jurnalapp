package com.mrap.jurnalapp;

import android.content.Context;

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
}
