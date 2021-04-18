package com.mrap.jurnalapp;

import android.app.Activity;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.mrap.jurnalapp.data.AktivitasItem;
import com.mrap.jurnalapp.data.JnlAktivitas;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ViewAktivitasFull {
    private static final String TAG = "ViewAktivitasFull";

    public static final int CB_CODE_EDIT = 0;
    public static final int CB_CODE_REMOVE = 1;

    private JnlActivity that;
    private ConstraintLayout root;
    JnlAktivitas jnlAktivitas;
    ModalUtil.Callback callback;
    ConstraintLayout aktiFull;

    public void showModal(JnlActivity that, ConstraintLayout root, JnlAktivitas jnlAktivitas, ModalUtil.Callback callback) {
        this.that = that;
        this.root = root;
        this.jnlAktivitas = jnlAktivitas;
        this.callback = callback;

        ModalUtil modalUtil = new ModalUtil();
        aktiFull = (ConstraintLayout)LayoutInflater.from(that).inflate(R.layout.view_aktivitas_full, null);
        ConstraintLayout bgLayout = modalUtil.createModal(that, root, aktiFull);

        Button button = aktiFull.findViewById(R.id.aktfu_btnBack);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                root.removeView(bgLayout);
                modalUtil.removeModal(that, bgLayout);
            }
        });

        TextView textView = aktiFull.findViewById(R.id.aktfu_nama);
        textView.setText(jnlAktivitas.nama);

//        float density = that.getResources().getDisplayMetrics().scaledDensity;
//        Log.d(TAG, "nama size 14=" + textView.getTextSize() + " " + textView.getTextSize() / density + "=" + textView.getTextSize());

        refreshPaneAktItem();
    }

    public void refreshPaneAktItem() {
        TextView textView;
        TableLayout tableLayout = aktiFull.findViewById(R.id.aktfu_paneAktItem);
        tableLayout.removeAllViews();
//        ArrayList<AktivitasItem> items = new ArrayList<>();
//        int nAktItem = jnlAktivitas.getSortedAktItemsByDate(items, jnlAktivitas.aktivitasItems);

        ArrayList<AktivitasItem> items = jnlAktivitas.getStortedAktItemsByDate();
        int nAktItem = items.size();

//        float density = that.getResources().getDisplayMetrics().scaledDensity;
//
//        String logSize = "size ";
//        for (int i = 10; i < 20; i++) {
//            logSize += i + "=" + i / density + " ";
//        }
//        Log.d(TAG, logSize);

        SimpleDateFormat sdfTgl = new SimpleDateFormat("d MMM");
        SimpleDateFormat sdfJam = new SimpleDateFormat("HH:mm");
        for (int i = 0; i < nAktItem; i++) {
            AktivitasItem aktItem = items.get(i);
            ConstraintLayout viewAktItem = (ConstraintLayout) LayoutInflater.from(that).inflate(R.layout.view_aktitem, null);

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

            View.OnClickListener editListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Object[] params = new Object[] {
                            new ModalUtil.Callback() {
                                @Override
                                public void onCallback(int id, int code, Object[] params) {
                                    refreshPaneAktItem();
                                }
                            }
                    };
                    callback.onCallback(aktItem.id, CB_CODE_EDIT, params);
                }
            };

            TableRow tr = new TableRow(that);
            if (i == 0) {
                tr.addView(createEditRemoveBtn(that, editListener, null));
            } else {
                tr.addView(createEditRemoveBtn(that, editListener, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ViewConfirmation viewConfirmation = new ViewConfirmation();
                        viewConfirmation.showModal(that, root, that.getString(R.string.deleteAktItemConfirmation, aktItem.judul),
                                false, null, new ModalUtil.Callback() {
                                    @Override
                                    public void onCallback(int id, int code, Object[] params) {
                                        if (code == ViewConfirmation.CODE_OK) {
                                            callback.onCallback(aktItem.id, CB_CODE_REMOVE, null);
                                            refreshPaneAktItem();
                                        }
                                    }
                                });
                    }
                }));
            }
            tr.addView(viewAktItem);

            tableLayout.addView(tr);
        }
    }

    private LinearLayout createEditRemoveBtn(Activity that, View.OnClickListener editListener, View.OnClickListener removeListener) {
        LinearLayout linearLayout = new LinearLayout(that);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);

        Util util = new Util(that);

        float density = that.getResources().getDisplayMetrics().scaledDensity;
        float size = 10 * density;

        Button button = new Button(that);
        button.setText("Edit");
        button.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
        button.setOnClickListener(editListener);

        linearLayout.addView(button);

        if (removeListener != null) {
            button = new Button(that);
            button.setText("Hapus");
            button.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
            button.setOnClickListener(removeListener);

            linearLayout.addView(button);
        }

        return linearLayout;
    }
}
