package com.mrap.jurnalapp;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class FlowLayout extends LinearLayout {

    private static final String TAG = "FlowLayout";
    SparseArray<View> contents = new SparseArray<>();
    private int maxWidth = 100;

    public FlowLayout(Context context) {
        this(context, null);
    }

    public FlowLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measuredWidth = getMeasuredWidth();
        if (maxWidth != measuredWidth) {
            maxWidth = getMeasuredWidth();
//            Log.d(TAG, "maxWidth " + maxWidth);
        }
    }

    public void setContents(SparseArray<View> contents) {
        this.contents = contents;

        for (int i = 0; i < getChildCount(); i++) {
            ViewGroup linearLayout = (ViewGroup)getChildAt(i);
            //Log.d(TAG, "remove content add line " + i);
            linearLayout.removeAllViews();
        }

//        Log.d(TAG, "remove lines");
        removeAllViews();

        ArrayList<LinearLayout> linearLayouts = new ArrayList<>();
        LinearLayout linearLayout = addLine();
        linearLayouts.add(linearLayout);

        int nContent = contents.size();
        int currLineWidth = 0;
        for (int i = 0; i < nContent; i++) {
            View content = contents.valueAt(i);

            int contentWidth = 0;

            content.measure(0, 0);
            ViewGroup.LayoutParams lp = content.getLayoutParams();
            if (lp != null && lp instanceof LinearLayout.LayoutParams) {
                LinearLayout.LayoutParams llp = (LinearLayout.LayoutParams)lp;
                llp.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                llp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                contentWidth += llp.leftMargin;
                contentWidth += llp.rightMargin;
                content.setLayoutParams(llp);
                //Log.d(TAG, "has lp " + llp.leftMargin + " " + llp.rightMargin);
            } else {
                lp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                content.setLayoutParams(lp);
                //Log.d(TAG, "has no lp");
            }
            contentWidth += content.getMeasuredWidth();

//            Log.d(TAG, "currLineWidth " + i + " " + currLineWidth + " " + maxWidth + " " + linearLayouts.size());

            if (currLineWidth + contentWidth >= maxWidth) {
//                linearLayout.measure(0, 0);
//                Log.d(TAG, "line child count " + linearLayout.getChildCount() + " " + linearLayout.getMeasuredWidth() +
//                        " " + linearLayout.getMeasuredHeight());
//                Log.d(TAG, "line child count " + linearLayout.getChildCount());
                linearLayout = addLine();
                linearLayouts.add(linearLayout);
                currLineWidth = contentWidth;
            } else {
                currLineWidth += contentWidth;
            }
            linearLayout.addView(content);
        }

        for (LinearLayout l : linearLayouts) {
            addView(l);
        }

//        invalidate();

//        linearLayout.measure(0, 0);
//        Log.d(TAG, "line child count " + linearLayout.getChildCount() + " " + linearLayout.getMeasuredWidth() +
//                " " + linearLayout.getMeasuredHeight());
//        Log.d(TAG, "line child count " + linearLayout.getChildCount());

//        selfMeasure = true;
//        measure(0, 0);

//        Log.d(TAG, "child count " + getChildCount());
    }

    private LinearLayout addLine() {
        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(HORIZONTAL);
        linearLayout.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//        linearLayout.setLayoutParams(new LayoutParams(100, 100));
//        linearLayout.setBackgroundColor(Color.parseColor("#ff0000"));
//        linearLayout.setVisibility(VISIBLE);
//        addView(linearLayout);
        return linearLayout;
    }
}
