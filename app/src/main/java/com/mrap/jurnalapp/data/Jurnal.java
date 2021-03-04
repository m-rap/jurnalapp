package com.mrap.jurnalapp.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.util.SparseArray;

import com.mrap.jurnalapp.R;

import java.util.Date;

public class Jurnal extends JnlData {
    final static String TAG = "Jurnal";

    public int id = -1;
    public String judul = "";
    public int tipeCover = 0;
    public int tipeBg = 0;
    public JurnalStyle style = new JurnalStyle();
    public SparseArray<JnlAktivitas> aktivitases = new SparseArray<>();

    private SQLiteDatabase dbAktivitas = null;
    private SQLiteDatabase dbAttr = null;
    private DbFactory dbFactory = null;

    public void loadAktivitas(DbFactory dbFactory) {
        Cursor c = dbAktivitas.rawQuery("SELECT * FROM aktivitas", null);
        if (!c.moveToFirst()) {
            c.close();
            return;
        }
        aktivitases.clear();
        int idxId = c.getColumnIndex("aktivitas_id");
        int idxNama = c.getColumnIndex("aktivitas_nama");
        int idxIsOnGoing = c.getColumnIndex("aktivitas_isongoing");
        do {
            JnlAktivitas jnlAktivitas = new JnlAktivitas();
            jnlAktivitas.id = c.getInt(idxId);
            jnlAktivitas.nama = c.getString(idxNama);
            jnlAktivitas.isOnGoing = c.getInt(idxIsOnGoing) == 1;
            jnlAktivitas.owner = this;
            aktivitases.put(jnlAktivitas.id, jnlAktivitas);
        } while (c.moveToNext());
        c.close();
    }

    public void loadStyle() {
        Log.d(TAG, "loadStyle tipeCover " + tipeCover);
        style = new JurnalStyle();
        if (tipeCover == -1) {
            JurnalStyle.JurnalStyleCoverImg s = new JurnalStyle.JurnalStyleCoverImg();
            Cursor c = dbAttr.rawQuery("SELECT attr_val_blob FROM attr WHERE attr_key = 'coverimg'", null);
            if (c.moveToFirst()) {
                byte[] img = c.getBlob(c.getColumnIndex("attr_val_blob"));
                s.img = BitmapFactory.decodeByteArray(img, 0, img.length);
            }
            c.close();
            if (s.img != null) {
                style.coverStyle = s;
            } else {
                JurnalStyle.JurnalStyleCoverRes s2 = new JurnalStyle.JurnalStyleCoverRes();
                s2.res = R.drawable.jurnal_cover;
                style.coverStyle = s2;
            }
        } else {
            JurnalStyle.JurnalStyleCoverRes s = new JurnalStyle.JurnalStyleCoverRes();
            if (tipeCover == 0) {
                s.res = R.drawable.jurnal_cover;
            }
            style.coverStyle = s;
        }
        if (tipeBg == -1) {
            JurnalStyle.JurnalStyleBgImg s = new JurnalStyle.JurnalStyleBgImg();
            Cursor c = dbAttr.rawQuery("SELECT attr_val_blob FROM attr WHERE attr_key = 'bgimg'", null);
            if (c.moveToFirst()) {
                byte[] img = c.getBlob(c.getColumnIndex("attr_val_blob"));
                s.img = BitmapFactory.decodeByteArray(img, 0, img.length);
            }
            c.close();
            style.bg = s;
        } else {
            JurnalStyle.JurnalStyleBgColor bg = new JurnalStyle.JurnalStyleBgColor();
            Cursor c = dbAttr.rawQuery("SELECT attr_val_text FROM attr WHERE attr_key = 'bgimg'", null);
            if (c.moveToFirst()) {
                bg.color = c.getString(c.getColumnIndex("attr_val_text"));
            }
            c.close();
        }
    }

    public void tambahAktivitas(String nama, Date tglMulai) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("aktivitas_nama", nama);
        contentValues.put("aktivitas_isongoing", true);
        JnlAktivitas jnlAktivitas = new JnlAktivitas();
        jnlAktivitas.id = (int)dbAktivitas.insert("aktivitas", null, contentValues);
        jnlAktivitas.nama = nama;
        jnlAktivitas.owner = this;
        jnlAktivitas.openChildrenDbs(dbFactory);
        jnlAktivitas.tambahAktivitasItem("Mulai", tglMulai);
        jnlAktivitas.closeChildrenDbs();
        aktivitases.put(jnlAktivitas.id, jnlAktivitas);
    }

    @Override
    public void openChildrenDbs(DbFactory dbFactory) {
        this.dbFactory = dbFactory;
        dbAktivitas = dbFactory.getDbAktivitas(id);
        dbAttr = dbFactory.getDbAttr(new int[] {id});
    }

    @Override
    public void closeChildrenDbs() {
        if (dbAktivitas != null) {
            dbAktivitas.close();
        }
        if (dbAttr != null) {
            dbAttr.close();
        }
        for (int i = 0; i < aktivitases.size(); i++) {
            aktivitases.valueAt(i).closeChildrenDbs();
        }
    }
}
