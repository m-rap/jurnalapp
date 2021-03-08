package com.mrap.jurnalapp;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

public class ModalUtil {

    public static interface Callback {
        public void onCallback(int id, int code, Object[] params);
    }

    public ConstraintLayout createModal(Activity that, ConstraintLayout root, ConstraintLayout content) {
        ConstraintLayout bgLayout = new ConstraintLayout(that);
        bgLayout.setId(View.generateViewId());
        bgLayout.setBackgroundColor(Color.parseColor("#AA000000"));
        root.addView(bgLayout);
        ConstraintLayout.LayoutParams lp = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        bgLayout.setLayoutParams(lp);
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(root);
        constraintSet.connect(bgLayout.getId(), ConstraintSet.TOP, root.getId(), ConstraintSet.TOP);
        constraintSet.connect(bgLayout.getId(), ConstraintSet.LEFT, root.getId(), ConstraintSet.LEFT);
        constraintSet.applyTo(root);

        Util util = new Util(that);
        int margin = (int)util.convertDipToPix(10);

        content.setId(View.generateViewId());
        ConstraintLayout.LayoutParams lp2 = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_CONSTRAINT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp2.topMargin = lp2.rightMargin = lp2.bottomMargin = lp2.leftMargin = margin;
        content.setLayoutParams(lp2);
        bgLayout.addView(content);
        ConstraintSet constraintSet2 = new ConstraintSet();
        constraintSet2.clone(bgLayout);
        constraintSet2.connect(content.getId(), ConstraintSet.TOP, bgLayout.getId(), ConstraintSet.TOP);
        constraintSet2.connect(content.getId(), ConstraintSet.LEFT, bgLayout.getId(), ConstraintSet.LEFT);
        constraintSet2.connect(content.getId(), ConstraintSet.RIGHT, bgLayout.getId(), ConstraintSet.RIGHT);
        constraintSet2.connect(content.getId(), ConstraintSet.BOTTOM, bgLayout.getId(), ConstraintSet.BOTTOM);
        constraintSet2.applyTo(bgLayout);
        return bgLayout;
    }
}
