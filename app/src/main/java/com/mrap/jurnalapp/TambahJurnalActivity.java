package com.mrap.jurnalapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.mrap.jurnalapp.data.Album;
import com.mrap.jurnalapp.data.DbFactory;
import com.mrap.jurnalapp.data.Jurnal;
import com.mrap.jurnalapp.data.JurnalStyle;

public class TambahJurnalActivity extends JnlActivity {

    private static final String TAG = "TambahJurnalActivity";
    int selectedCover = 0;

    Jurnal jurnal1 = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_tambahjurnal);

        Intent intent = getIntent();
        int jurnalId = intent.getIntExtra("id", -1);

        Util util = new Util(this);
        int margin = (int)util.convertDipToPix(5);

        JurnalStyle.Util jsUtil = new JurnalStyle.Util();
        SparseArray<Integer> typeResMap = jsUtil.getTypeResMap();
        SparseArray<View> viewCoverMap = new SparseArray<>();

        for (int i = 0; i < typeResMap.size(); i++) {
            int key = typeResMap.keyAt(i);

            JurnalStyle.JurnalStyleCoverTipe js = new JurnalStyle.JurnalStyleCoverTipe();
            js.tipe = key;

            ConstraintLayout viewIcon = (ConstraintLayout)LayoutInflater.from(this).inflate(R.layout.view_jurnalicon, null);
            ImageView viewCover = viewIcon.findViewById(R.id.imgCover);
            js.render(viewCover);
            TextView textView = viewIcon.findViewById(R.id.jnlic_txtJudul);
            textView.setVisibility(View.GONE);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.topMargin = lp.rightMargin = lp.bottomMargin = lp.leftMargin = margin;
            viewIcon.setLayoutParams(lp);

            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.addView(viewIcon);
            if (key == 0) {
                linearLayout.setBackgroundResource(R.drawable.bounding_box);
            }
            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changeSelectedCover(key, viewCoverMap);
                }
            });

            viewCoverMap.put(key, linearLayout);
        }

        if (jurnalId != -1) {
            DbFactory dbFactory = new DbFactory(this, getExternalFilesDir(null).getPath());
            Album album = new Album();
            album.openChildrenDbs(dbFactory);
            jurnal1 = album.getJurnal(jurnalId);
            album.closeChildrenDbs();

            TextView textView = findViewById(R.id.tjnl_pageTitle);
            textView.setText(R.string.editJurnal);

            textView = findViewById(R.id.tjnl_txtJudul);
            textView.setText(jurnal1.judul);
        }

        FlowLayout flowLayout = findViewById(R.id.tjnl_panePickCover);
        getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                flowLayout.setContents(viewCoverMap);

                if (jurnalId != -1) {
                    if (jurnal1.style.coverStyle instanceof JurnalStyle.JurnalStyleCoverTipe) {
                        JurnalStyle.JurnalStyleCoverTipe jTipe = (JurnalStyle.JurnalStyleCoverTipe) jurnal1.style.coverStyle;
                        changeSelectedCover(jTipe.tipe, viewCoverMap);
                    }
                    if (jurnal1.style.bg instanceof JurnalStyle.JurnalStyleBgColor) {
                        ColorPickerView colorPickerView = findViewById(R.id.tjnl_colorPicker);
                        JurnalStyle.JurnalStyleBgColor jBg = (JurnalStyle.JurnalStyleBgColor) jurnal1.style.bg;
                        colorPickerView.setCurrentColor(Color.parseColor(jBg.color));
                    }
                }
            }
        });
    }

    private void changeSelectedCover(int key, SparseArray<View> viewCoverMap) {
        selectedCover = key;
        for (int j = 0; j < viewCoverMap.size(); j++) {
            int key2 = viewCoverMap.keyAt(j);
            if (selectedCover == key2) {
                viewCoverMap.get(key2).setBackgroundResource(R.drawable.bounding_box);
            } else {
                viewCoverMap.get(key2).setBackground(null);
            }
        }
    }

    public void onClickSimpan(View view) {
        boolean isEdit = jurnal1 != null;

        if (jurnal1 == null) {
            jurnal1 = new Jurnal();
        }

        EditText editText = findViewById(R.id.tjnl_txtJudul);

        jurnal1.judul = editText.getText().toString();

        ColorPickerView colorPickerView = findViewById(R.id.tjnl_colorPicker);
        //Log.d(TAG, "colorPicker " + colorPickerView);

        int pickedColor = colorPickerView.getCurrentColor();
        String colorStr = String.format("#%02x%02x%02x", Color.red(pickedColor), Color.green(pickedColor), Color.blue(pickedColor));
        //Log.d(TAG, "picked color " + pickedColor + " " + colorStr);

        JurnalStyle.JurnalStyleBgColor jurnalStyleBgColor = new JurnalStyle.JurnalStyleBgColor();
        jurnalStyleBgColor.color = colorStr;
        jurnal1.style.bg = jurnalStyleBgColor;
        jurnal1.tipeBg = 0;

        JurnalStyle.JurnalStyleCoverTipe jurnalStyleCoverTipe = new JurnalStyle.JurnalStyleCoverTipe();
        jurnalStyleCoverTipe.tipe = selectedCover;
        jurnal1.tipeCover = selectedCover;

        DbFactory dbFactory = new DbFactory(this, getExternalFilesDir(null).getPath());
        Album album = new Album();
        album.openChildrenDbs(dbFactory);
        if (isEdit) {
            album.editJurnal(jurnal1);
        } else {
            album.saveJurnal(jurnal1);
        }
        album.closeChildrenDbs();

        finish();
    }
}
