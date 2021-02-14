package com.mrap.jurnalapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;

import com.mrap.jurnalapp.data.Album;
import com.mrap.jurnalapp.data.DbFactory;
import com.mrap.jurnalapp.data.Jurnal;

public class TambahJurnalActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_tambahjurnal);
    }

    public void onClickSimpan(View view) {
        EditText editText = findViewById(R.id.txtJudul);
        Jurnal jurnal = new Jurnal();
        jurnal.judul = editText.getText().toString();
//        jurnal.

        DbFactory dbFactory = new DbFactory(this, getExternalFilesDir(null).getPath());
        Album album = new Album();
        album.openChildrenDbs(dbFactory);
        album.saveJurnal(jurnal);
        album.closeChildrenDbs();

        finish();
    }
}
