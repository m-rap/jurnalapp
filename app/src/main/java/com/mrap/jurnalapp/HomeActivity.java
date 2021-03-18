package com.mrap.jurnalapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

//import com.google.android.flexbox.FlexLine;
//import com.google.android.flexbox.FlexboxLayout;
import com.mrap.jurnalapp.data.Album;
import com.mrap.jurnalapp.data.DbFactory;
import com.mrap.jurnalapp.data.Jurnal;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

public class HomeActivity extends JnlActivity {
    private static final String TAG = "HomeActivity";

    Album album = null;
    DbFactory dbFactory = null;

    SparseArray<View> jnlViewMap = new SparseArray<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_home);

        dbFactory = new DbFactory(this, getExternalFilesDir(null).getPath());
        album = new Album();

        TextView textView = findViewById(R.id.home_txtLang);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openContextMenu(textView);
            }
        });
        registerForContextMenu(textView);

        Locale current = getResources().getConfiguration().locale;
        Log.d(TAG, "current locale " + current + " " + current.getLanguage() + " " + current.getCountry());
    }

    @Override
    protected void onResume() {
        super.onResume();

        album.openChildrenDbs(dbFactory);
        album.loadJurnals();

        FlowLayout layout = findViewById(R.id.jurnal_container);
        jnlViewMap.clear();

        Util util = new Util(this);
        int margin = (int)util.convertDipToPix(10);

        for (int i = 0; i < album.jurnals.size(); i++) {
            Jurnal jurnal = album.jurnals.valueAt(i);
            jurnal.openChildrenDbs(dbFactory);
            jurnal.loadStyle();
            ConstraintLayout jnlView = createJurnalIcon(jurnal);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.topMargin = margin;
            lp.leftMargin = margin;
            lp.rightMargin = margin;
            lp.bottomMargin = margin;
            jnlView.setLayoutParams(lp);
            jnlViewMap.put(jurnal.id, jnlView);

            HomeActivity that = this;
            jnlView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(that, JurnalActivity.class);
                    intent.putExtra("id", jurnal.id);
                    startActivity(intent);
                }
            });

            registerForContextMenu(jnlView);
        }

        getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                layout.setContents(jnlViewMap);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        album.closeChildrenDbs();
    }

    public ConstraintLayout createJurnalIcon(Jurnal jurnal) {
        ConstraintLayout iconRoot = (ConstraintLayout)LayoutInflater.from(this).inflate(R.layout.view_jurnalicon, null);
        ImageView ivBg = iconRoot.findViewById(R.id.imgBg);
        ImageView ivCover = iconRoot.findViewById(R.id.imgCover);
        jurnal.style.bg.render(ivBg);
        jurnal.style.coverStyle.render(ivCover);

        TextView textView = iconRoot.findViewById(R.id.jnlic_txtJudul);
        textView.setText(jurnal.judul);

        return iconRoot;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        TextView textView = findViewById(R.id.home_txtLang);
        if (v == textView) {
            menu.setHeaderTitle(getString(R.string.chooseLang));
            menu.add(1, 0, 0, "id");
            menu.add(1, 1, 0, "en");
            return;
        }
        int jurnalId = jnlViewMap.keyAt(jnlViewMap.indexOfValue(v));
        menu.setHeaderTitle("Menu " + album.jurnals.get(jurnalId).judul);
        menu.add(0, jurnalId, 0, R.string.menu_edit);
        menu.add(0, jurnalId, 0, R.string.menu_delete);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        boolean res = super.onContextItemSelected(item);

        if (item.getGroupId() == 1) {
            Util util = new Util(this);
            int id = item.getItemId();
            if (id == 0) {
                util.saveLocale("in", null);
                recreate();
            } else if (id == 1) {
                util.saveLocale("en", null);
                recreate();
            }
            return res;
        }

        int id = item.getItemId();
        Log.d(TAG, "context menu item " + item.getTitle() + " " + id);
        if (item.getTitle().equals(getString(R.string.menu_delete))) {
            ViewConfirmation viewConfirmation = new ViewConfirmation();
            ConstraintLayout root = findViewById(R.id.home_root);
            viewConfirmation.showModal(this, root, getString(R.string.deleteJurnalConfirmation, album.jurnals.get(id).judul),
                    true, getString(R.string.yesDeleteJurnal), new ModalUtil.Callback() {
                @Override
                public void onCallback(int callbackId, int code, Object[] params) {
                    if (code == ViewConfirmation.CODE_OK) {
                        deleteJurnal(id);
                    }
                }
            });
        } else if (item.getTitle().equals(getString(R.string.menu_edit))) {
            Intent intent = new Intent(this, TambahJurnalActivity.class);
            intent.putExtra("id", id);
            startActivity(intent);
        }
        return res;
    }

    public void deleteJurnal(int id) {
        if (album.deleteJurnal(id)) {
//            FlexboxLayout layout = findViewById(R.id.jurnal_container);
            View v = jnlViewMap.get(id);
            if (v != null) {
//                layout.removeView(v);
            }
            jnlViewMap.remove(id);

            getWindow().getDecorView().post(new Runnable() {
                @Override
                public void run() {
//                    consistentSpaceBetweenUntilLastLine(layout);
                    FlowLayout layout = findViewById(R.id.jurnal_container);
                    layout.setContents(jnlViewMap);
                }
            });
        }
    }

    public void onClickTambahJurnal(View view) {
        Intent intent = new Intent(this, TambahJurnalActivity.class);
        intent.putExtra("id", -1);
        startActivity(intent);
    }
}
