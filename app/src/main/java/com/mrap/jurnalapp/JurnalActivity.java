package com.mrap.jurnalapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mrap.jurnalapp.data.AktivitasItem;
import com.mrap.jurnalapp.data.Album;
import com.mrap.jurnalapp.data.DbFactory;
import com.mrap.jurnalapp.data.JnlAktivitas;
import com.mrap.jurnalapp.data.Jurnal;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

public class JurnalActivity extends Activity {

    private static final String TAG = "JurnalActivity";
    DbFactory dbFactory = null;
    Jurnal jurnal = null;

    SparseArray<View> aktivitasViews = new SparseArray<>();

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

        refresh();
    }

    private SimpleDateFormat createSdf() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    private void refresh() {
        jurnal.loadAktivitas(dbFactory);

        LinearLayout viewOnGoing = findViewById(R.id.viewOnGoing);
        LinearLayout viewListAktivitas = findViewById(R.id.viewListAktivitas);

        viewOnGoing.removeAllViews();
        viewListAktivitas.removeAllViews();

        Util util = new Util(this);
        int margin = (int)util.convertDipToPix(10);
        int width = util.getDisplaySize().x - 2 * margin;

        SimpleDateFormat sdf = createSdf();

        for (int i = 0, onGoingCount = 0, nOnGoingCount = 0; i < jurnal.aktivitases.size(); i++) {
            JnlAktivitas jnlAktivitas = jurnal.aktivitases.valueAt(i);
            jnlAktivitas.openChildrenDbs(dbFactory);
            jnlAktivitas.loadAktivitasItems(dbFactory);
            jnlAktivitas.closeChildrenDbs();

            ConstraintLayout root = (ConstraintLayout) LayoutInflater.from(this).inflate(R.layout.view_aktivitas, null);

            TextView textView = root.findViewById(R.id.txtNama);
            textView.setText(jnlAktivitas.nama);

            textView = root.findViewById(R.id.txtTanggalMulai);
            textView.setText(sdf.format(jnlAktivitas.aktivitasItems.valueAt(0).tanggal));

            int nAktItem = jnlAktivitas.aktivitasItems.size();
            AktivitasItem itemTerbaru = jnlAktivitas.aktivitasItems.valueAt(nAktItem - 1);

            textView = root.findViewById(R.id.txtMomen);
            textView.setText(itemTerbaru.judul);

            textView = root.findViewById(R.id.txtTanggalMomen);
            textView.setText(sdf.format(itemTerbaru.tanggal));

            AktivitasBar aktivitasBar = root.findViewById(R.id.viewBar);
            aktivitasBar.aktivitas = jnlAktivitas;
            aktivitasBar.invalidate();

            if (jnlAktivitas.isOnGoing) {
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT);
                lp.topMargin = lp.rightMargin = lp.bottomMargin = lp.leftMargin = margin;
                root.setLayoutParams(lp);
                viewOnGoing.addView(root);

                onGoingCount++;
            } else {
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT);
                lp.topMargin = lp.rightMargin = lp.bottomMargin = lp.leftMargin = margin;
                root.setLayoutParams(lp);
                viewListAktivitas.addView(root);

                nOnGoingCount++;
            }

            aktivitasViews.put(jnlAktivitas.id, root);
            registerForContextMenu(root);
        }

        getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
//                for (int i = 0; i < aktivitasViews.size(); i++) {
//                    aktivitasViews.valueAt(i).findViewById(R.id.viewBar).invalidate();
//                }
//                ViewGroup viewOnGoingWrap = findViewById(R.id.viewOnGoingWrap);
//                int hPx = viewOnGoingWrap.getHeight();
//                int maxHPx = (int)util.convertDipToPix(200);
//                if (hPx > maxHPx) {
//                    ViewGroup.LayoutParams lp = viewOnGoingWrap.getLayoutParams();
//                    lp.height = maxHPx;
//                    viewOnGoingWrap.setLayoutParams(lp);
//                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

        jurnal.closeChildrenDbs();
    }

    public void onClickTambahAktivitas(View view) {
        ConstraintLayout parent = findViewById(R.id.jnl_root);

        ConstraintLayout bgLayout = new ConstraintLayout(this);
        bgLayout.setId(View.generateViewId());
        bgLayout.setBackgroundColor(Color.parseColor("#AA000000"));
        parent.addView(bgLayout);
        ConstraintLayout.LayoutParams lp = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        bgLayout.setLayoutParams(lp);
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(parent);
        constraintSet.connect(bgLayout.getId(), ConstraintSet.TOP, parent.getId(), ConstraintSet.TOP);
        constraintSet.connect(bgLayout.getId(), ConstraintSet.LEFT, parent.getId(), ConstraintSet.LEFT);
        constraintSet.applyTo(parent);

        JurnalActivity that = this;

        Util util = new Util(that);
        int margin = (int)util.convertDipToPix(10);

        ConstraintLayout layoutTambahAktivitas = (ConstraintLayout)LayoutInflater.from(that).inflate(R.layout.layout_tambahaktivitas, null);
        layoutTambahAktivitas.setId(View.generateViewId());
        ConstraintLayout.LayoutParams lp2 = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_CONSTRAINT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp2.topMargin = lp2.rightMargin = lp2.bottomMargin = lp2.leftMargin = margin;
        layoutTambahAktivitas.setLayoutParams(lp2);
        bgLayout.addView(layoutTambahAktivitas);
        ConstraintSet constraintSet2 = new ConstraintSet();
        constraintSet2.clone(bgLayout);
        constraintSet2.connect(layoutTambahAktivitas.getId(), ConstraintSet.TOP, bgLayout.getId(), ConstraintSet.TOP);
        constraintSet2.connect(layoutTambahAktivitas.getId(), ConstraintSet.LEFT, bgLayout.getId(), ConstraintSet.LEFT);
        constraintSet2.connect(layoutTambahAktivitas.getId(), ConstraintSet.RIGHT, bgLayout.getId(), ConstraintSet.RIGHT);
        constraintSet2.connect(layoutTambahAktivitas.getId(), ConstraintSet.BOTTOM, bgLayout.getId(), ConstraintSet.BOTTOM);
        constraintSet2.applyTo(bgLayout);

        SimpleDateFormat sdf = createSdf();
        TextView textView = layoutTambahAktivitas.findViewById(R.id.takt_txtWaktu);
        textView.setText(sdf.format(new Date()));

        Button button = layoutTambahAktivitas.findViewById(R.id.takt_btnBatal);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parent.removeView(bgLayout);
            }
        });

        button = layoutTambahAktivitas.findViewById(R.id.takt_btnTambah);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView textView = layoutTambahAktivitas.findViewById(R.id.takt_txtNama);
                String nama = textView.getText().toString();
                textView = layoutTambahAktivitas.findViewById(R.id.takt_txtWaktu);
                String waktu = textView.getText().toString();
                try {
                    Date date = sdf.parse(waktu);
                    jurnal.tambahAktivitas(nama, date);
                    refresh();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                parent.removeView(bgLayout);
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        int aktId = aktivitasViews.keyAt(aktivitasViews.indexOfValue(v));
        JnlAktivitas aktivitas = jurnal.aktivitases.get(aktId);
        menu.setHeaderTitle("Menu");
        if (aktivitas != null) {
            if (aktivitas.isOnGoing) {
                menu.add(0, aktId, 0, "Selesai");
                menu.add(0, aktId, 0, "Catat momen");
            } else {
                menu.add(0, aktId, 0, "Hapus");
            }
        }
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        boolean res = super.onContextItemSelected(item);
        if (item.getTitle().equals("Selesai")) {
            jurnal.akhiriAktivitas(item.getItemId(), new Date());
            refresh();
        }
        if (item.getTitle().equals("Hapus")) {
            jurnal.hapusAktivitas(item.getItemId());
            refresh();
        }
        return res;
    }
}
