package com.mrap.jurnalapp;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;

import com.mrap.jurnalapp.data.Album;
import com.mrap.jurnalapp.data.DbFactory;
import com.mrap.jurnalapp.data.Jurnal;
import com.mrap.jurnalapp.data.JurnalStyle;

public class TambahJurnalActivity extends Activity {

    private static final String TAG = "TambahJurnalActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_tambahjurnal);
    }

    public void onClickSimpan(View view) {
        EditText editText = findViewById(R.id.tjnl_txtJudul);
        Jurnal jurnal = new Jurnal();
        jurnal.judul = editText.getText().toString();

        ColorPickerView colorPickerView = findViewById(R.id.tjnl_colorPicker);
        //Log.d(TAG, "colorPicker " + colorPickerView);

        int pickedColor = colorPickerView.getCurrentColor();
        String colorStr = String.format("#%02x%02x%02x", Color.red(pickedColor), Color.green(pickedColor), Color.blue(pickedColor));
        Log.d(TAG, "picked color " + pickedColor + " " + colorStr);

        JurnalStyle.JurnalStyleBgColor jurnalStyleBgColor = new JurnalStyle.JurnalStyleBgColor();
        jurnalStyleBgColor.color = colorStr;
        jurnal.style.bg = jurnalStyleBgColor;
        jurnal.tipeBg = 0;

        DbFactory dbFactory = new DbFactory(this, getExternalFilesDir(null).getPath());
        Album album = new Album();
        album.openChildrenDbs(dbFactory);
        album.saveJurnal(jurnal);
        album.closeChildrenDbs();

        finish();
    }
}
