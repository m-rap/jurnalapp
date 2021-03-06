package com.mrap.jurnalapp;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.mrap.jurnalapp.data.AktivitasItem;
import com.mrap.jurnalapp.data.JnlAktivitas;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ViewAktivitasFull {
    private static final String TAG = "ViewAktivitasFull";

    public void showModal(Activity that, ConstraintLayout root, JnlAktivitas jnlAktivitas) {
        ModalUtil modalUtil = new ModalUtil();
        ConstraintLayout aktiFull = (ConstraintLayout)LayoutInflater.from(that).inflate(R.layout.view_aktivitas_full, null);
        ConstraintLayout bgLayout = modalUtil.createModal(that, root, aktiFull);

        Button button = aktiFull.findViewById(R.id.aktfu_btnBack);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                root.removeView(bgLayout);
            }
        });

        TextView textView = aktiFull.findViewById(R.id.aktfu_nama);
        textView.setText(jnlAktivitas.nama);

        LinearLayout linearLayout = aktiFull.findViewById(R.id.aktfu_paneAktItem);
        ArrayList<AktivitasItem> items = new ArrayList<>();
        int nAktItem = jnlAktivitas.aktivitasItems.size();
        Log.d(TAG, "nAktItem " + nAktItem);
        for (int i = 0; i < nAktItem; i++) {
            items.add(jnlAktivitas.aktivitasItems.valueAt(i));
        }
        Collections.sort(items, new Comparator<AktivitasItem>() {
            @Override
            public int compare(AktivitasItem o1, AktivitasItem o2) {
                return o1.tanggal.getTime() < o2.tanggal.getTime() ? -1 : 1;
            }
        });
        SimpleDateFormat sdfTgl = new SimpleDateFormat("d MMM");
        SimpleDateFormat sdfJam = new SimpleDateFormat("HH:mm");
        for (int i = 0; i < nAktItem; i++) {
            AktivitasItem aktItem = items.get(i);
            ConstraintLayout viewAktItem = (ConstraintLayout)LayoutInflater.from(that).inflate(R.layout.view_aktitem, null);

            textView = viewAktItem.findViewById(R.id.aktit_jam);
            textView.setText(sdfJam.format(aktItem.tanggal));

            textView = viewAktItem.findViewById(R.id.aktit_tanggal);
            textView.setText(sdfTgl.format(aktItem.tanggal));

            textView = viewAktItem.findViewById(R.id.aktit_judul);
            textView.setText(aktItem.judul);

            textView = viewAktItem.findViewById(R.id.aktit_note);
            if (aktItem.note.isEmpty()) {
                textView.setVisibility(View.GONE);
            } else {
                textView.setText(aktItem.judul);
            }

            linearLayout.addView(viewAktItem);
        }
    }
}
