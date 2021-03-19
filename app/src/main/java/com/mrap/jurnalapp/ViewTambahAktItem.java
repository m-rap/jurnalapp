package com.mrap.jurnalapp;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mrap.jurnalapp.data.AktivitasItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.constraintlayout.widget.ConstraintLayout;

public class ViewTambahAktItem {

    private ConstraintLayout showModalInternal(JnlActivity that, ConstraintLayout root, AktivitasItem aktItem, ModalUtil.Callback callback) {
        ConstraintLayout layoutTambahAktItem = (ConstraintLayout) LayoutInflater.from(that).inflate(R.layout.layout_tambahaktitem, null);
        ModalUtil modalUtil = new ModalUtil();
        ConstraintLayout bgLayout = modalUtil.createModal(that, root, layoutTambahAktItem);

//        Util util = new Util(that);
//        SimpleDateFormat sdf = util.createSdf();

        TextView textView;
        DateTimeField dateTimeField = layoutTambahAktItem.findViewById(R.id.taktit_datetimefield);
        if (aktItem == null) {
//            textView = layoutTambahAktItem.findViewById(R.id.taktit_tanggal);
//            textView.setText(sdf.format(new Date()));

            dateTimeField.setDate(new Date());

        } else {
            textView = layoutTambahAktItem.findViewById(R.id.taktit_txtJudul);
            textView.setText(aktItem.judul);

            textView = layoutTambahAktItem.findViewById(R.id.taktit_note);
            textView.setText(aktItem.note);

//            textView = layoutTambahAktItem.findViewById(R.id.taktit_tanggal);
//            textView.setText(sdf.format(aktItem.tanggal));

            dateTimeField.setDate(aktItem.tanggal);
        }

        Button button = layoutTambahAktItem.findViewById(R.id.taktit_btnBatal);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                root.removeView(bgLayout);
                modalUtil.removeModal(that, bgLayout);
            }
        });

        button = layoutTambahAktItem.findViewById(R.id.taktit_btnSimpan);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView textView1 = layoutTambahAktItem.findViewById(R.id.taktit_txtJudul);
                String judul = textView1.getText().toString();

                textView1 = layoutTambahAktItem.findViewById(R.id.taktit_note);
                String note = textView1.getText().toString();

//                textView1 = layoutTambahAktItem.findViewById(R.id.taktit_tanggal);
                DateTimeField dateTimeField = layoutTambahAktItem.findViewById(R.id.taktit_datetimefield);
                try {
//                    Date tanggal = sdf.parse(textView1.getText().toString());
                    Date tanggal = dateTimeField.getDate();
                    if (aktItem == null) {
                        callback.onCallback(-1, -1, new Object[]{judul, note, tanggal});
                    } else {
                        callback.onCallback(aktItem.id, -1, new Object[]{judul, note, tanggal});
                    }
//                    root.removeView(bgLayout);
                    modalUtil.removeModal(that, bgLayout);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        return layoutTambahAktItem;
    }

    public void showFinishModal(JnlActivity that, ConstraintLayout root, AktivitasItem aktItem, ModalUtil.Callback callback) {
        ConstraintLayout layoutTambahAktitem = showModalInternal(that, root, aktItem, callback);

        TextView textView1 = layoutTambahAktitem.findViewById(R.id.taktit_pagetitle);
        textView1.setText("Selesai");

        textView1 = layoutTambahAktitem.findViewById(R.id.taktit_txtJudul);
        textView1.setText("Selesai");
        textView1.setVisibility(View.GONE);

        textView1 = layoutTambahAktitem.findViewById(R.id.taktit_note);
        textView1.setVisibility(View.GONE);
    }

    public void showModal(JnlActivity that, ConstraintLayout root, AktivitasItem aktItem, ModalUtil.Callback callback) {
        showModalInternal(that, root, aktItem, callback);
    }
}
