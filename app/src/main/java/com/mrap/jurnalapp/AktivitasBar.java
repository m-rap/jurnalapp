package com.mrap.jurnalapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import com.mrap.jurnalapp.data.AktivitasItem;
import com.mrap.jurnalapp.data.JnlAktivitas;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class AktivitasBar extends TextView {
    private static final String TAG = "AktivitasBar";
    public JnlAktivitas aktivitas = null;

    public AktivitasBar(Context context) {
        super(context);
    }

    public AktivitasBar(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //Log.d(TAG, "onDraw");
        if (aktivitas == null || aktivitas.isOnGoing) {
            super.onDraw(canvas);
            return;
        }
        int nAktItem = aktivitas.aktivitasItems.size();
        //Log.d(TAG, "n = " + nAktItem);
        if (nAktItem < 2) {
            super.onDraw(canvas);
            return;
        }
        ArrayList<AktivitasItem> items = new ArrayList<>();
        for (int i = 0; i < nAktItem; i++) {
            items.add(aktivitas.aktivitasItems.valueAt(i));
        }
        Collections.sort(items, new Comparator<AktivitasItem>() {
            @Override
            public int compare(AktivitasItem o1, AktivitasItem o2) {
                return o1.tanggal.getTime() < o2.tanggal.getTime() ? -1 : 1;
            }
        });
        Util util = new Util(getContext());
        float poinRadius = (int)util.convertDipToPix(5);
        long startTime = items.get(0).tanggal.getTime();
        long endTime = items.get(nAktItem - 1).tanggal.getTime();
        long timeLength = endTime - startTime;
        float lineWidth = canvas.getWidth() - poinRadius * 2;
        float yStart = canvas.getHeight() / 2 - poinRadius;
        canvas.translate(0, yStart);
        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#000000"));
        Paint blue = new Paint();
        blue.setColor(Color.parseColor("#0000FF"));
        canvas.drawLine(poinRadius, poinRadius, poinRadius + lineWidth, poinRadius, paint);
        for (int i = 0; i < nAktItem; i++) {
            long t = items.get(i).tanggal.getTime() - startTime;
            float x = t * lineWidth / timeLength;
            canvas.drawCircle(x + poinRadius, poinRadius, poinRadius, blue);
        }
    }
}
