package com.mrap.jurnalapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Movie;
import android.graphics.Paint;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

public class GifView extends View {
    private GifNoView gifNoView = new GifNoView();

    public GifView(Context context) {
        super(context);
    }

    public GifView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public GifNoView getGifNoView() {
        return gifNoView;
    }

    protected void onDraw(Canvas canvas) {
        gifNoView.onDraw(canvas);
        invalidate();
    }
}
