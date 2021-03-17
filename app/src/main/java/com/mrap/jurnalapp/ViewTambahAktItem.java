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

    public void showModal(Activity that, ConstraintLayout root, AktivitasItem aktItem, ModalUtil.Callback callback) {
        ConstraintLayout layoutTambahAktitem = (ConstraintLayout) LayoutInflater.from(that).inflate(R.layout.layout_tambahaktitem, null);
        ModalUtil modalUtil = new ModalUtil();
        ConstraintLayout bgLayout = modalUtil.createModal(that, root, layoutTambahAktitem);

        Util util = new Util(that);
//        SimpleDateFormat sdf = util.createSdf();

        TextView textView;
        DateTimeField dateTimeField = layoutTambahAktitem.findViewById(R.id.taktit_datetimefield);
        if (aktItem == null) {
//            textView = layoutTambahAktitem.findViewById(R.id.taktit_tanggal);
//            textView.setText(sdf.format(new Date()));

            dateTimeField.setDate(new Date());

        } else {
            textView = layoutTambahAktitem.findViewById(R.id.taktit_txtJudul);
            textView.setText(aktItem.judul);

            textView = layoutTambahAktitem.findViewById(R.id.taktit_note);
            textView.setText(aktItem.note);

//            textView = layoutTambahAktitem.findViewById(R.id.taktit_tanggal);
//            textView.setText(sdf.format(aktItem.tanggal));

            dateTimeField.setDate(aktItem.tanggal);
        }

        Button button = layoutTambahAktitem.findViewById(R.id.taktit_btnBatal);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                root.removeView(bgLayout);
            }
        });

        button = layoutTambahAktitem.findViewById(R.id.taktit_btnSimpan);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView textView1 = layoutTambahAktitem.findViewById(R.id.taktit_txtJudul);
                String judul = textView1.getText().toString();

                textView1 = layoutTambahAktitem.findViewById(R.id.taktit_note);
                String note = textView1.getText().toString();

//                textView1 = layoutTambahAktitem.findViewById(R.id.taktit_tanggal);
                DateTimeField dateTimeField = layoutTambahAktitem.findViewById(R.id.taktit_datetimefield);
                try {
//                    Date tanggal = sdf.parse(textView1.getText().toString());
                    Date tanggal = dateTimeField.getDate();
                    if (aktItem == null) {
                        callback.onCallback(-1, -1, new Object[]{judul, note, tanggal});
                    } else {
                        callback.onCallback(aktItem.id, -1, new Object[]{judul, note, tanggal});
                    }
                    root.removeView(bgLayout);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
