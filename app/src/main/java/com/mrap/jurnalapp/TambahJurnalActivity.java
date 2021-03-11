package com.mrap.jurnalapp;

import android.app.Activity;
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

public class TambahJurnalActivity extends Activity {

    private static final String TAG = "TambahJurnalActivity";
    int selectedCover = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_tambahjurnal);

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
            });

            viewCoverMap.put(key, linearLayout);
        }

        FlowLayout flowLayout = findViewById(R.id.tjnl_panePickCover);
        getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                flowLayout.setContents(viewCoverMap);
            }
        });
    }

    public void onClickSimpan(View view) {
        EditText editText = findViewById(R.id.tjnl_txtJudul);
        Jurnal jurnal = new Jurnal();
        jurnal.judul = editText.getText().toString();

        ColorPickerView colorPickerView = findViewById(R.id.tjnl_colorPicker);
        //Log.d(TAG, "colorPicker " + colorPickerView);

        int pickedColor = colorPickerView.getCurrentColor();
        String colorStr = String.format("#%02x%02x%02x", Color.red(pickedColor), Color.green(pickedColor), Color.blue(pickedColor));
        //Log.d(TAG, "picked color " + pickedColor + " " + colorStr);

        JurnalStyle.JurnalStyleBgColor jurnalStyleBgColor = new JurnalStyle.JurnalStyleBgColor();
        jurnalStyleBgColor.color = colorStr;
        jurnal.style.bg = jurnalStyleBgColor;
        jurnal.tipeBg = 0;

        JurnalStyle.JurnalStyleCoverTipe jurnalStyleCoverTipe = new JurnalStyle.JurnalStyleCoverTipe();
        jurnalStyleCoverTipe.tipe = selectedCover;
        jurnal.tipeCover = selectedCover;

        DbFactory dbFactory = new DbFactory(this, getExternalFilesDir(null).getPath());
        Album album = new Album();
        album.openChildrenDbs(dbFactory);
        album.saveJurnal(jurnal);
        album.closeChildrenDbs();

        finish();
    }
}
