package com.mrap.jurnalapp;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.flexbox.FlexLine;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.flexbox.JustifyContent;
import com.mrap.jurnalapp.data.Jurnal;
import com.mrap.jurnalapp.data.JurnalStyle;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

public class HomeActivity extends Activity {
    private static final String TAG = "HomeActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_home);
        FlexboxLayout layout = findViewById(R.id.jurnal_container);
        layout.setJustifyContent(JustifyContent.SPACE_BETWEEN);
        layout.removeAllViews();

        Jurnal jurnal = new Jurnal();
        jurnal.style = new JurnalStyle();
        jurnal.style.bg = new JurnalStyle.JurnalStyleBgColor();

        ConstraintLayout cl = null;
        for (int i = 0; i < 50; i++) {
            ConstraintLayout jnlView = createJurnalIcon(jurnal);
            if (i == 0) {
                cl = jnlView;
            }
            FlexboxLayout.LayoutParams lp = new FlexboxLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.topMargin = 10;
            lp.leftMargin = 10;
            lp.rightMargin = 10;
            lp.bottomMargin = 10;
            jnlView.setLayoutParams(lp);
            layout.addView(jnlView);
        }

        final ConstraintLayout fcl = cl;

        getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                List<FlexLine> flexLines = layout.getFlexLines();
                Iterator<FlexLine> it = flexLines.iterator();
                if (flexLines.size() > 1) {
                    FlexLine flexLine = it.next();
                    Log.d(TAG,  "fi " + flexLine.getFirstIndex() + " c " + flexLine.getItemCount() +
                            " ms " + flexLine.getMainSize() + " vs " + fcl.getWidth() + " ps " + layout.getWidth() +
                            " gr " + flexLine.getTotalFlexGrow() + " sr " + flexLine.getTotalFlexShrink() +
                            " cs " + flexLine.getCrossSize());
                    float targetMargin = (float)(layout.getWidth() - flexLine.getMainSize()) / (flexLine.getItemCount() - 1);
                    Log.d(TAG, "targetMargin " + targetMargin);
                    layout.setJustifyContent(JustifyContent.FLEX_START);
                    int lineItemCount = flexLine.getItemCount();
                    int childCount = layout.getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        if ((i + 1) % lineItemCount == 0) {
                            continue;
                        }
                        View v = layout.getChildAt(i);
                        FlexboxLayout.LayoutParams lp = (FlexboxLayout.LayoutParams)v.getLayoutParams();
                        lp.rightMargin += targetMargin;
                    }
                }
            }
        });
    }

    public ConstraintLayout createJurnalIcon(Jurnal jurnal) {
        ConstraintLayout root = (ConstraintLayout)LayoutInflater.from(this).inflate(R.layout.view_jurnalicon, null);
        ImageView ivBg = root.findViewById(R.id.imgBg);
        ImageView ivCover = root.findViewById(R.id.imgCover);
        jurnal.style.bg.render(ivBg);
        jurnal.style.bg.render(ivCover);
        return root;
    }
}
