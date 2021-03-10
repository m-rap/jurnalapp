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
import java.util.ArrayList;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.viewpager.widget.ViewPager;

public class JurnalActivity extends Activity {

    private static final String TAG = "JurnalActivity";
    DbFactory dbFactory = null;
    Jurnal jurnal = null;

    SparseArray<View> aktivitasViews = new SparseArray<>();
    SparseArray<View> aktivitasViewsOnGoing = new SparseArray<>();

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

    private void refresh() {
        jurnal.loadAktivitas(dbFactory);

        //LinearLayout viewOnGoing = findViewById(R.id.viewOnGoing);
        LinearLayout viewListAktivitas = findViewById(R.id.viewListAktivitas);

        //viewOnGoing.removeAllViews();
        viewListAktivitas.removeAllViews();
        aktivitasViews.clear();
        aktivitasViewsOnGoing.clear();

        Util util = new Util(this);
        int margin = (int)util.convertDipToPix(10);
        int width = util.getDisplaySize().x - 2 * margin;

        //SimpleDateFormat sdf = util.createSdf();

        for (int i = 0, onGoingCount = 0, nOnGoingCount = 0; i < jurnal.aktivitases.size(); i++) {
            JnlAktivitas jnlAktivitas = jurnal.aktivitases.valueAt(i);
            ConstraintLayout aktivitasRoot = (ConstraintLayout) LayoutInflater.from(this).inflate(R.layout.view_aktivitas, null);

            refreshAktivitasView(jnlAktivitas, aktivitasRoot);

            if (jnlAktivitas.isOnGoing) {
//                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT);
//                lp.topMargin = lp.rightMargin = lp.bottomMargin = lp.leftMargin = margin;
//                aktivitasRoot.setLayoutParams(lp);
//                viewOnGoing.addView(aktivitasRoot);
                aktivitasViewsOnGoing.put(jnlAktivitas.id, aktivitasRoot);

                onGoingCount++;
            } else {
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT);
                lp.topMargin = lp.rightMargin = lp.bottomMargin = lp.leftMargin = margin;
                aktivitasRoot.setLayoutParams(lp);
                viewListAktivitas.addView(aktivitasRoot);

                nOnGoingCount++;
            }

            aktivitasViews.put(jnlAktivitas.id, aktivitasRoot);
            registerForContextMenu(aktivitasRoot);
            aktivitasRoot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openContextMenu(aktivitasRoot);
                }
            });
        }

        refreshOnGoingPane();
    }

    private void refreshOnGoingPane() {
        ViewPager viewOnGoing2 = findViewById(R.id.viewOnGoing2);
        viewOnGoing2.setAdapter(new JnlPagerAdapter(this, aktivitasViewsOnGoing));

        ConstraintLayout viewOnGoingWrap2 = findViewById(R.id.viewOnGoingWrap2);
        if (aktivitasViewsOnGoing.size() > 0) {
            View aktivitasRoot = aktivitasViewsOnGoing.valueAt(0);
            aktivitasRoot.measure(0, 0);
            int height = aktivitasRoot.getMeasuredHeight();

//            TextView textView = findViewById(R.id.jnl_txtOnGoingPagerIndicator);
            //textView.measure(0, 0);
            //int height2 = textView.getMeasuredHeight();
            //textView.setVisibility(View.VISIBLE);

            viewOnGoing2.getLayoutParams().height = height;
            viewOnGoingWrap2.setVisibility(View.VISIBLE);

//            ConstraintSet constraintSet = new ConstraintSet();
//            constraintSet.clone(viewOnGoingWrap2);
//            constraintSet.connect(textView.getId(), ConstraintSet.BOTTOM, viewOnGoingWrap2.getId(), ConstraintSet.BOTTOM);
//            constraintSet.connect(textView.getId(), ConstraintSet.RIGHT, viewOnGoingWrap2.getId(), ConstraintSet.RIGHT);
//            constraintSet.applyTo(viewOnGoingWrap2);

        } else {
            viewOnGoing2.getLayoutParams().height = 0;
            viewOnGoingWrap2.setVisibility(View.GONE);
        }
    }

    private void refreshAktivitasView(JnlAktivitas jnlAktivitas, ConstraintLayout aktivitasRoot) {
        Util util = new Util(this);
        SimpleDateFormat sdf = util.createSdf();

        jnlAktivitas.openChildrenDbs(dbFactory);
        jnlAktivitas.loadAktivitasItems(dbFactory);
        jnlAktivitas.closeChildrenDbs();

        TextView textView = aktivitasRoot.findViewById(R.id.txtNama);
        textView.setText(jnlAktivitas.nama);

        textView = aktivitasRoot.findViewById(R.id.txtTanggalMulai);
        textView.setText(sdf.format(jnlAktivitas.aktivitasItems.valueAt(0).tanggal));

        ArrayList<AktivitasItem> items = new ArrayList<>();
        int nAktItem = jnlAktivitas.getSortedAktItemsByDate(items, jnlAktivitas.aktivitasItems);

        AktivitasItem itemTerbaru = items.get(nAktItem - 1);

        AktivitasBar aktivitasBar = aktivitasRoot.findViewById(R.id.viewBar);

        if (jnlAktivitas.isOnGoing) {
            textView = aktivitasRoot.findViewById(R.id.txtMomen);
            textView.setText(itemTerbaru.judul);

            textView = aktivitasRoot.findViewById(R.id.txtTanggalMomen);
            textView.setText(sdf.format(itemTerbaru.tanggal));
        } else {
            aktivitasBar.setBackgroundColor(Color.WHITE);
            aktivitasRoot.findViewById(R.id.akt_txtMomenTitle).setVisibility(View.GONE);
            aktivitasRoot.findViewById(R.id.txtMomen).setVisibility(View.GONE);
            aktivitasRoot.findViewById(R.id.txtTanggalMomen).setVisibility(View.GONE);
        }

        aktivitasBar.aktivitas = jnlAktivitas;
        aktivitasBar.invalidate();
    }

    @Override
    protected void onPause() {
        super.onPause();

        jurnal.closeChildrenDbs();
    }

    public void onClickTambahAktivitas(View view) {
        JurnalActivity that = this;
        ConstraintLayout parent = findViewById(R.id.jnl_root);
        ConstraintLayout layoutTambahAktivitas = (ConstraintLayout)LayoutInflater.from(that).inflate(R.layout.layout_tambahaktivitas, null);

        ModalUtil modalUtil = new ModalUtil();
        ConstraintLayout bgLayout = modalUtil.createModal(that, parent, layoutTambahAktivitas);

        Util util = new Util(that);
        SimpleDateFormat sdf = util.createSdf();
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
                menu.add(0, aktId, 0, "Catat momen");
                menu.add(0, aktId, 0, "Atur");
                menu.add(0, aktId, 0, "Selesai");
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
        } else if (item.getTitle().equals("Hapus")) {
            ViewConfirmation viewConfirmation = new ViewConfirmation();
            ConstraintLayout root = findViewById(R.id.jnl_root);
            JnlAktivitas aktivitas = jurnal.aktivitases.get(item.getItemId());

            viewConfirmation.showModal(this, root, "Apakah Anda yakin menghapus aktivitas " + aktivitas.nama + "?",
                    false, new ModalUtil.Callback() {
                        @Override
                        public void onCallback(int id, int code, Object[] params) {
                            if (code == ViewConfirmation.CODE_OK) {
                                jurnal.hapusAktivitas(aktivitas.id);
                                View v = aktivitasViews.get(aktivitas.id);
                                aktivitasViews.remove(aktivitas.id);

                                if (aktivitas.isOnGoing) {
//                                    LinearLayout view = findViewById(R.id.viewOnGoing);
//                                    view.removeView(v);

                                    aktivitasViewsOnGoing.remove(aktivitas.id);
                                    refreshOnGoingPane();
                                } else {
                                    LinearLayout view = findViewById(R.id.viewListAktivitas);
                                    view.removeView(v);
                                }
                            }
                        }
                    });

        } else if (item.getTitle().equals("Catat momen")) {
            JnlAktivitas aktivitas = jurnal.aktivitases.get(item.getItemId());

            ConstraintLayout root = findViewById(R.id.jnl_root);
            ViewTambahAktItem viewTambahAktItem = new ViewTambahAktItem();
            viewTambahAktItem.showModal(this, root, null, new ModalUtil.Callback() {
                @Override
                public void onCallback(int id, int code, Object[] params) {
                    String judul = (String)params[0];
                    String note = (String)params[1];
                    Date tanggal = (Date)params[2];
                    aktivitas.openChildrenDbs(dbFactory);
                    aktivitas.tambahAktivitasItem(judul, note, tanggal);
                    aktivitas.closeChildrenDbs();

                    ConstraintLayout aktView = (ConstraintLayout)aktivitasViews.get(aktivitas.id);
                    refreshAktivitasView(aktivitas, aktView);
                }
            });

        } else if (item.getTitle().equals("Atur")) {
            JnlAktivitas aktivitas = jurnal.aktivitases.get(item.getItemId());
            ViewAktivitasFull viewAktivitasFull = new ViewAktivitasFull();
            ConstraintLayout root = findViewById(R.id.jnl_root);
            JurnalActivity that = this;
            viewAktivitasFull.showModal(this, root, aktivitas, new ModalUtil.Callback() {
                @Override
                public void onCallback(int id, int code, Object[] params1) {
                    if (code == ViewAktivitasFull.CB_CODE_EDIT) {
                        ViewTambahAktItem viewTambahAktItem = new ViewTambahAktItem();
                        AktivitasItem aktItem = aktivitas.aktivitasItems.get(id);
                        if (aktItem == null) {
                            return;
                        }
                        viewTambahAktItem.showModal(that, root, aktItem, new ModalUtil.Callback() {
                            @Override
                            public void onCallback(int id, int code, Object[] params2) {
                                String judul = (String)params2[0];
                                String note = (String)params2[1];
                                Date tanggal = (Date)params2[2];
                                aktivitas.openChildrenDbs(dbFactory);
                                aktivitas.editAktivitasItem(id, judul, note, tanggal);
                                aktivitas.closeChildrenDbs();

                                if (params1 != null && params1.length > 0) {
                                    ModalUtil.Callback p1Callback = (ModalUtil.Callback)params1[0];
                                    p1Callback.onCallback(-1, -1, null);
                                }

                                ConstraintLayout aktView = (ConstraintLayout)aktivitasViews.get(aktivitas.id);
                                refreshAktivitasView(aktivitas, aktView);
                            }
                        });
                    } else if (code == ViewAktivitasFull.CB_CODE_REMOVE) {
                        aktivitas.openChildrenDbs(dbFactory);
                        aktivitas.hapusAktivitasItem(id);
                        aktivitas.closeChildrenDbs();

                        ConstraintLayout aktView = (ConstraintLayout)aktivitasViews.get(aktivitas.id);
                        refreshAktivitasView(aktivitas, aktView);
                    }
                }
            });
        }
        return res;
    }
}
