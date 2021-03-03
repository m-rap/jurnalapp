package com.mrap.jurnalapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.flexbox.FlexLine;
import com.google.android.flexbox.FlexboxLayout;
import com.mrap.jurnalapp.data.Album;
import com.mrap.jurnalapp.data.DbFactory;
import com.mrap.jurnalapp.data.Jurnal;

import java.util.Iterator;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

public class HomeActivity extends Activity {
    private static final String TAG = "HomeActivity";

    Album album = null;
    DbFactory dbFactory = null;

    SparseArray<View> jnlViewMap = new SparseArray<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_home);

        dbFactory = new DbFactory(this, getExternalFilesDir(null).getPath());
        album = new Album();
    }

    @Override
    protected void onResume() {
        super.onResume();

        album.openChildrenDbs(dbFactory);
        album.loadJurnals();

        FlexboxLayout layout = findViewById(R.id.jurnal_container);
        layout.removeAllViews();
        jnlViewMap.clear();

        Util util = new Util(this);
        int margin = (int)util.convertDipToPix(10);

        ConstraintLayout cl = null;
        for (int i = 0; i < album.jurnals.size(); i++) {
            Jurnal jurnal = album.jurnals.valueAt(i);
            jurnal.openChildrenDbs(dbFactory);
            jurnal.loadStyle();
            ConstraintLayout jnlView = createJurnalIcon(jurnal);
            if (i == 0) {
                cl = jnlView;
            }
            FlexboxLayout.LayoutParams lp = new FlexboxLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.topMargin = margin;
            lp.leftMargin = margin;
            lp.rightMargin = margin;
            lp.bottomMargin = margin;
            jnlView.setLayoutParams(lp);
            layout.addView(jnlView);
            jnlViewMap.put(jurnal.id, jnlView);

            HomeActivity that = this;
            jnlView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(that, JurnalActivity.class);
                    intent.putExtra("id", jurnal.id);
                    startActivity(intent);
                }
            });

            registerForContextMenu(jnlView);
        }

        final ConstraintLayout fcl = cl;

        getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                consistentSpaceBetweenUntilLastLine(layout, fcl);
            }
        });
    }

    public void consistentSpaceBetweenUntilLastLine(FlexboxLayout layout, final ConstraintLayout owner) {
        List<FlexLine> flexLines = layout.getFlexLines();
        Iterator<FlexLine> it = flexLines.iterator();
        if (flexLines.size() > 1) {
            FlexLine flexLine = it.next();
//            Log.d(TAG,  "fi " + flexLine.getFirstIndex() + " c " + flexLine.getItemCount() +
//                    " ms " + flexLine.getMainSize() + " vs " + owner.getWidth() + " ps " + layout.getWidth() +
//                    " gr " + flexLine.getTotalFlexGrow() + " sr " + flexLine.getTotalFlexShrink() +
//                    " cs " + flexLine.getCrossSize());
            float targetMargin = (float)(layout.getWidth() - flexLine.getMainSize()) / (flexLine.getItemCount() - 1);
            int lineItemCount = flexLine.getItemCount();
            int childCount = layout.getChildCount();
//            Log.d(TAG, "targetMargin " + targetMargin + " lineItemCount " + lineItemCount + " childCount " + childCount);
            for (int i = 0; i < childCount; i++) {
                if ((i + 1) % lineItemCount == 0) {
                    continue;
                }
                View v = layout.getChildAt(i);
                FlexboxLayout.LayoutParams lp = (FlexboxLayout.LayoutParams)v.getLayoutParams();
                lp.rightMargin += targetMargin;
                v.setLayoutParams(lp);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        album.closeChildrenDbs();
    }

    public ConstraintLayout createJurnalIcon(Jurnal jurnal) {
        ConstraintLayout root = (ConstraintLayout)LayoutInflater.from(this).inflate(R.layout.view_jurnalicon, null);
        ImageView ivBg = root.findViewById(R.id.imgBg);
        ImageView ivCover = root.findViewById(R.id.imgCover);
        jurnal.style.bg.render(ivBg);
        jurnal.style.coverStyle.render(ivCover);
        TextView textView = root.findViewById(R.id.txtJudul2);
        textView.setText(jurnal.judul);

        return root;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        int jurnalId = jnlViewMap.keyAt(jnlViewMap.indexOfValue(v));
        menu.setHeaderTitle("Menu " + album.jurnals.get(jurnalId).judul);
        menu.add("Hapus");
    }

    public void onClickTambahJurnal(View view) {
        Intent intent = new Intent(this, TambahJurnalActivity.class);
        startActivity(intent);
    }
}
