package com.mrap.jurnalapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class GifView extends View {
    private Movie movie = null;

    public GifView(Context context) {
        super(context);
    }

    public GifView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setRes(int res) {
        movie = Movie.decodeStream(getContext().getResources().openRawResource(res));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (movie != null) {
            movie.setTime(
                    (int) SystemClock.uptimeMillis() % movie.duration());
            movie.draw(canvas, 0, 0);
            invalidate();
        }
    }
}
