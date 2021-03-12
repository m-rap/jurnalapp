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
    private Movie movie = null;

    float x = 0;
    float y = 0;
    float scale = 1;

    public GifView(Context context) {
        super(context);
    }

    public GifView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setRes(int res) {
        movie = Movie.decodeStream(getContext().getResources().openRawResource(res));
    }

    public void setResPos(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void setResScale(float scale) {
        this.scale = scale;
    }
    Paint p = new Paint();

    @Override
    protected void onDraw(Canvas canvas) {
        if (movie != null) {
            Log.d("GifView", x + " " + y + " " + scale);
            canvas.save();
            canvas.translate(x, y);
            canvas.scale(scale, scale);
            movie.setTime(
                    (int) SystemClock.uptimeMillis() % movie.duration());
            movie.draw(canvas, 0, 0);
            p.setColor(Color.BLACK);
            p.setStrokeWidth(2);
            canvas.drawRect(0, 0, 20, 20, p);
            canvas.restore();
            invalidate();
        }
    }
}
