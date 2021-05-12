package com.mrap.jurnalapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Movie;
import android.graphics.Paint;
import android.util.AttributeSet;
//import android.util.Log;
import android.util.Log;
import android.widget.LinearLayout;
//import android.widget.TextView;

import com.mrap.jurnalapp.data.AktivitasItem;
import com.mrap.jurnalapp.data.JnlAktivitas;

import java.util.ArrayList;

public class AktivitasBar extends LinearLayout {
    private static final String TAG = "AktivitasBar";
    private final Paint red;
    private final Paint blue;
    private final Paint black;
    public JnlAktivitas aktivitas = null;
    GifNoView[] progressGifs = new GifNoView[] { new GifNoView() };
    private int gifCount = 1;
    Movie progressMov = null;
    private long maxTimespan = -1;

    public AktivitasBar(Context context) {
        this(context, null);
    }

    public AktivitasBar(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        
        red = new Paint();
        black = new Paint();
        blue = new Paint();
        red.setColor(Color.parseColor("#ff0000"));
        black.setColor(Color.parseColor("#000000"));
        blue.setColor(Color.parseColor("#0000ff"));
    }

    public void setProgressMov(Movie m) {
        progressMov = m;
        progressGifs[0].setMovie(progressMov);

        createGifPool(5);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measuredWidth = getMeasuredWidth();

        int gifWidth = progressGifs[0].getGifWidth();
        if (gifWidth > 0) {
            gifCount = measuredWidth / gifWidth;
            if (measuredWidth % gifWidth != 0) {
                gifCount++;
            }
        } else {
            gifCount = 1;
        }

//        Log.d(TAG, "onMeasure " + measuredWidth / gifWidth + " " + measuredWidth % gifWidth + " " + gifCount);

        if (gifCount == 0) {
            return;
        }

        if (gifCount > progressGifs.length) {
            createGifPool(gifCount);
        }
    }

    private void createGifPool(int count) {
        GifNoView temp = progressGifs[0];
        int gifWidth = temp.getGifWidth();
        Movie movie = temp.getMovie();

        progressGifs = new GifNoView[count];
        progressGifs[0] = temp;

        int x = gifWidth;
        for (int i = 1; i < count; i++, x += gifWidth) {
            progressGifs[i] = new GifNoView();
            progressGifs[i].setMovie(movie);
            progressGifs[i].setResPos(x, 0);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //Log.d(TAG, "onDraw");
        if (aktivitas == null || aktivitas.isOnGoing) {
            for (int i = 0; i < gifCount; i++) {
                progressGifs[i].onDraw(canvas);
            }
            invalidate();
            return;
        }
        int nAktItem = aktivitas.aktivitasItems.size();
        //Log.d(TAG, "n = " + nAktItem);
        if (nAktItem < 2) {
            for (int i = 0; i < gifCount; i++) {
                progressGifs[i].onDraw(canvas);
            }
            invalidate();
            return;
        }
//        ArrayList<AktivitasItem> items = new ArrayList<>();
//        aktivitas.getSortedAktItemsByDate(items, aktivitas.aktivitasItems);

        ArrayList<AktivitasItem> items = aktivitas.getStortedAktItemsByDate();

        Util util = new Util(getContext());
        float poinRadius = (int)util.convertDipToPix(5);
        long startTime = items.get(0).tanggal.getTime();
        long endTime = items.get(nAktItem - 1).tanggal.getTime();
        long timeLength = endTime - startTime;
        float lineWidth = canvas.getWidth() - poinRadius * 2;
        if (maxTimespan != -1) {
            lineWidth = timeLength * lineWidth / maxTimespan;
        }
        float yStart = canvas.getHeight() / 2 - poinRadius;
        canvas.translate(0, yStart);
        canvas.drawLine(poinRadius, poinRadius, poinRadius + lineWidth, poinRadius, black);
        for (int i = 0; i < nAktItem; i++) {
            long t = items.get(i).tanggal.getTime() - startTime;
            float x = t * lineWidth / timeLength;
            canvas.drawCircle(x + poinRadius, poinRadius, poinRadius, blue);
        }
    }

    public void setMaxTimespan(long maxTimespan) {
        this.maxTimespan = maxTimespan;
    }
}
