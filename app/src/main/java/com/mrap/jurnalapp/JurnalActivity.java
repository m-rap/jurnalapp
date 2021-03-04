package com.mrap.jurnalapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mrap.jurnalapp.data.Album;
import com.mrap.jurnalapp.data.DbFactory;
import com.mrap.jurnalapp.data.JnlAktivitas;
import com.mrap.jurnalapp.data.Jurnal;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

public class JurnalActivity extends Activity {

    DbFactory dbFactory = null;
    Jurnal jurnal = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_jurnal);

        dbFactory = new DbFactory(this, getExternalFilesDir(null).getPath());

        Intent intent = getIntent();
        int jurnalId = intent.getIntExtra("id", -1);
        Album album = new Album();
        album.openChildrenDbs(dbFactory);
        jurnal = album.getJurnal(jurnalId);
        album.closeChildrenDbs();

        if (jurnal == null) {
            return;
        }

        TextView textView = findViewById(R.id.txtJudul3);
        textView.setText(jurnal.judul);
    }

    @Override
    protected void onResume() {
        super.onResume();

        jurnal.openChildrenDbs(dbFactory);
        jurnal.loadAktivitas(dbFactory);

        LinearLayout viewOnGoing = findViewById(R.id.viewOnGoing);
        LinearLayout viewListAktivitas = findViewById(R.id.viewListAktivitas);

        viewListAktivitas.removeAllViews();
        Util util = new Util(this);
        int topMargin = (int)util.convertDipToPix(10);

        for (int i = 0, onGoingCount = 0, nOnGoingCount = 0; i < jurnal.aktivitases.size(); i++) {
            JnlAktivitas jnlAktivitas = jurnal.aktivitases.valueAt(i);

            ConstraintLayout root = (ConstraintLayout) LayoutInflater.from(this).inflate(R.layout.view_aktivitas, null);
            TextView txtNama = root.findViewById(R.id.txtNama);
            txtNama.setText(jnlAktivitas.nama);

            if (jnlAktivitas.isOnGoing) {
                if (onGoingCount > 0) {
                    ViewGroup.LayoutParams oldLp = root.getLayoutParams();
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(oldLp.width, oldLp.height);
                    lp.topMargin = topMargin;
                    root.setLayoutParams(lp);
                }
                viewOnGoing.addView(root);

                onGoingCount++;
            } else {
                if (nOnGoingCount > 0) {
                    ViewGroup.LayoutParams oldLp = root.getLayoutParams();
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(oldLp.width, oldLp.height);
                    lp.topMargin = topMargin;
                    root.setLayoutParams(lp);
                }
                viewListAktivitas.addView(root);

                nOnGoingCount++;
            }
        }

        getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                ViewGroup viewOnGoingWrap = findViewById(R.id.viewOnGoingWrap);
                int hPx = viewOnGoingWrap.getHeight();
                int maxHPx = (int)util.convertDipToPix(200);
                if (hPx > maxHPx) {
                    ViewGroup.LayoutParams lp = viewOnGoingWrap.getLayoutParams();
                    lp.height = maxHPx;
                    viewOnGoingWrap.setLayoutParams(lp);
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

        jurnal.closeChildrenDbs();
    }

    public void onClickTambahAktivitas(View view) {

    }
}
