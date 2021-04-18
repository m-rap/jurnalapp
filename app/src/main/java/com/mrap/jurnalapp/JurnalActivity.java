package com.mrap.jurnalapp;

import android.animation.TimeAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Movie;
import android.os.Bundle;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.ViewPager;

public class JurnalActivity extends JnlActivity {

    private static final String TAG = "JurnalActivity";
    DbFactory dbFactory = null;
    Jurnal jurnal = null;

    SparseArray<View> aktivitasViews = new SparseArray<>();
    SparseArray<View> aktivitasViewsOnGoing = new SparseArray<>();
    SparseArray<JnlAktivitas> aktivitasOnGoing = new SparseArray<>();

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
        jurnal.loadAktivitas();

        //LinearLayout viewOnGoing = findViewById(R.id.viewOnGoing);
        LinearLayout viewListAktivitas = findViewById(R.id.viewListAktivitas);

        //viewOnGoing.removeAllViews();
        viewListAktivitas.removeAllViews();
        aktivitasViews.clear();
        aktivitasViewsOnGoing.clear();
        aktivitasOnGoing.clear();

        Util util = new Util(this);
        int margin = (int)util.convertDipToPix(10);
        int width = util.getDisplaySize().x - 2 * margin;

        //SimpleDateFormat sdf = util.createSdf();
        Movie progressMov = Movie.decodeStream(getResources().openRawResource(R.raw.progress_orange));

        for (int i = 0, onGoingCount = 0, nOnGoingCount = 0; i < jurnal.aktivitases.size(); i++) {
            JnlAktivitas jnlAktivitas = jurnal.aktivitases.valueAt(i);
            ConstraintLayout aktivitasRoot = (ConstraintLayout) LayoutInflater.from(this).inflate(R.layout.view_aktivitas, null);

            refreshAktivitasView(jnlAktivitas, aktivitasRoot, progressMov);

            if (jnlAktivitas.isOnGoing) {
//                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT);
//                lp.topMargin = lp.rightMargin = lp.bottomMargin = lp.leftMargin = margin;
//                aktivitasRoot.setLayoutParams(lp);
//                viewOnGoing.addView(aktivitasRoot);
                aktivitasViewsOnGoing.put(jnlAktivitas.id, aktivitasRoot);
                aktivitasOnGoing.put(jnlAktivitas.id, jnlAktivitas);

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

    TimeAnimator stopwatchAnim = null;
    TextView lblStopwatch = null;
    int ongoingAktIndex = -1;

    private void refreshOnGoingPane() {
        stopwatchAnim = new TimeAnimator();
        stopwatchAnim.setTimeListener(new TimeAnimator.TimeListener() {
            @Override
            public void onTimeUpdate(TimeAnimator animation, long totalTime, long deltaTime) {
                if (ongoingAktIndex == -1 || lblStopwatch == null) {
                    return;
                }

                JnlAktivitas jnlAktivitas = aktivitasOnGoing.valueAt(ongoingAktIndex);
                ArrayList<AktivitasItem> items = jnlAktivitas.getStortedAktItemsByDate();

                if (items == null || items.size() == 0) {
                    return;
                }

                Date started = items.get(0).tanggal;
                long msElapsed = Calendar.getInstance().getTime().getTime() - started.getTime();

                long sec = msElapsed / 1000;
                long ms = msElapsed % 1000;
                long min = sec / 60;
                sec = sec % 60;
                long hour = min / 60;
                min = min % 60;
                long day = hour / 24;
                hour = hour % 24;
                long week = day / 7;
                day = day % 7;

                String text;
                if (hour > 0) {
                    text = String.format("%02d:%02d:%02d", hour, min, sec);
                } else {
                    text = String.format("%02d:%02d.%03d", min, sec, ms);
                }
                if (day > 0) {
                    text = day + " day " + text;
                }
                if (week > 0) {
                    text = week + " week " + text;
                }

                lblStopwatch.setText(text);
            }
        });

        ViewPager viewOnGoing2 = findViewById(R.id.viewOnGoing2);

        TextView pagerIndicator = findViewById(R.id.jnl_txtOnGoingPagerIndicator);
        pagerIndicator.setText(getString(R.string.pagerIndicator, 1, aktivitasViewsOnGoing.size()));

        viewOnGoing2.clearOnPageChangeListeners();
        viewOnGoing2.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                pagerIndicator.setText(getString(R.string.pagerIndicator, position + 1, aktivitasViewsOnGoing.size()));
                ongoingAktIndex = position;
                lblStopwatch = aktivitasViewsOnGoing.valueAt(ongoingAktIndex).findViewById(R.id.akt_stopwatch);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

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

            ongoingAktIndex = 0;
            lblStopwatch = aktivitasRoot.findViewById(R.id.akt_stopwatch);
            stopwatchAnim.start();
        } else {
            stopwatchAnim.end();
            viewOnGoing2.getLayoutParams().height = 0;
            viewOnGoingWrap2.setVisibility(View.GONE);
        }
    }

    private void refreshAktivitasView(JnlAktivitas jnlAktivitas, ConstraintLayout aktivitasRoot, Movie progressMov) {
        Util util = new Util(this);
        SimpleDateFormat sdf = new SimpleDateFormat(getResources().getString(R.string.sdf1));

        jnlAktivitas.openChildrenDbs(dbFactory);
        jnlAktivitas.loadAktivitasItems();
        jnlAktivitas.closeChildrenDbs();

        TextView textView = aktivitasRoot.findViewById(R.id.txtNama);
        textView.setText(jnlAktivitas.nama);

//        ArrayList<AktivitasItem> items = new ArrayList<>();
//        int nAktItem = jnlAktivitas.getSortedAktItemsByDate(items, jnlAktivitas.aktivitasItems);

        ArrayList<AktivitasItem> items = jnlAktivitas.getStortedAktItemsByDate();
        int nAktItem = items.size();

        textView = aktivitasRoot.findViewById(R.id.txtTanggalMulai);
        textView.setText(sdf.format(items.get(0).tanggal));

        AktivitasItem itemTerbaru = items.get(nAktItem - 1);

        AktivitasBar aktivitasBar = aktivitasRoot.findViewById(R.id.viewBar);
        if (progressMov != null) {
            aktivitasBar.setProgressMov(progressMov);
        }

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
            aktivitasRoot.findViewById(R.id.akt_stopwatch).setVisibility(View.GONE);
        }

        aktivitasBar.aktivitas = jnlAktivitas;
        aktivitasBar.invalidate();

//        GifView gifView = aktivitasRoot.findViewById(R.id.viewBar2);
//        gifView.setRes(R.drawable.loading_bar);
//        gifView.getGifNoView().setRes(this, R.drawable.progress_orange);
//        gifView.invalidate();
//        x = -239.0f; y = -182.0f;
//        scale = 3.4f;
//        gifView.getGifNoView().setResPos(x, y);
//        gifView.getGifNoView().setResScale(scale);
    }

    float x = 0;
    float y = 0;
    float scale = 1;

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

//        Util util = new Util(that);
//        SimpleDateFormat sdf = util.createSdf();
//        TextView textView = layoutTambahAktivitas.findViewById(R.id.takt_txtWaktu);
//        textView.setText(sdf.format(new Date()));

        DateTimeField dateTimeField = layoutTambahAktivitas.findViewById(R.id.takt_txtWaktu);
        dateTimeField.setDate(new Date());

        Button button = layoutTambahAktivitas.findViewById(R.id.takt_btnBatal);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                parent.removeView(bgLayout);
                modalUtil.removeModal(that, bgLayout);
            }
        });

        button = layoutTambahAktivitas.findViewById(R.id.takt_btnTambah);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView textView = layoutTambahAktivitas.findViewById(R.id.takt_txtNama);
                String nama = textView.getText().toString();
//                textView = layoutTambahAktivitas.findViewById(R.id.takt_txtWaktu);
//                String waktu = textView.getText().toString();
                DateTimeField dateTimeField = layoutTambahAktivitas.findViewById(R.id.takt_txtWaktu);
                try {
//                    Date date = sdf.parse(waktu);
                    Date date = dateTimeField.getDate();
                    jurnal.tambahAktivitas(nama, date);
                    refresh();
                } catch (Exception e) {
                    e.printStackTrace();
                }
//                parent.removeView(bgLayout);
                modalUtil.removeModal(that, bgLayout);
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
                menu.add(0, aktId, 0, "Atur");
                menu.add(0, aktId, 0, "Lanjutkan lagi");
                menu.add(0, aktId, 0, "Hapus");
            }
        }
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        boolean res = super.onContextItemSelected(item);
        if (item.getTitle().equals("Selesai")) {
            ConstraintLayout root = findViewById(R.id.jnl_root);
            ViewTambahAktItem viewTambahAktItem = new ViewTambahAktItem();
            viewTambahAktItem.showFinishModal(this, root, null, new ModalUtil.Callback() {
                @Override
                public void onCallback(int id, int code, Object[] params) {
                    Date tanggal = (Date)params[2];

                    jurnal.akhiriAktivitas(item.getItemId(), tanggal);
                    refresh();
                }
            });
        } else if (item.getTitle().equals("Hapus")) {
            ViewConfirmation viewConfirmation = new ViewConfirmation();
            ConstraintLayout root = findViewById(R.id.jnl_root);
            JnlAktivitas aktivitas = jurnal.aktivitases.get(item.getItemId());

            viewConfirmation.showModal(this, root, getString(R.string.deleteJnlAktConfirmation, aktivitas.nama),
                    false, null, new ModalUtil.Callback() {
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
                    refreshAktivitasView(aktivitas, aktView, null);
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
                                refreshAktivitasView(aktivitas, aktView, null);
                            }
                        });
                    } else if (code == ViewAktivitasFull.CB_CODE_REMOVE) {
                        aktivitas.openChildrenDbs(dbFactory);
                        aktivitas.hapusAktivitasItem(id);
                        aktivitas.closeChildrenDbs();

                        ConstraintLayout aktView = (ConstraintLayout)aktivitasViews.get(aktivitas.id);
                        refreshAktivitasView(aktivitas, aktView, null);
                    }
                }
            });
        } else if (item.getTitle().equals("Lanjutkan lagi")) {
            jurnal.lanjutkanLagiAktivitas(item.getItemId());
            refresh();
        }
        return res;
    }
}
