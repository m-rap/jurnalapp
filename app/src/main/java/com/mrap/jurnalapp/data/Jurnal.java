package com.mrap.jurnalapp.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.SparseArray;

public class Jurnal extends JnlData {
    int id = -1;
    String judul = "";
    int tipeCover = 0;
    int tipeBg = 0;
    JurnalStyle style = null;
    SparseArray<JnlAktivitas> aktivitases = new SparseArray<>();

    SQLiteDatabase dbAktivitas = null;
    SQLiteDatabase dbAttr = null;

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
            aktivitases.put(jnlAktivitas.id, jnlAktivitas);
        } while (c.moveToNext());
        c.close();
    }

    public void loadStyle() {
        style = new JurnalStyle();
        if (tipeCover == -1) {
            JurnalStyle.JurnalStyleCoverImg s = new JurnalStyle.JurnalStyleCoverImg();
            Cursor c = dbAttr.rawQuery("SELECT * FROM attr_val_blob WHERE attr_key = 'coverimg'", null);
            if (c.moveToFirst()) {
                byte[] img = c.getBlob(c.getColumnIndex("attr_val_blob"));
                s.img = BitmapFactory.decodeByteArray(img, 0, img.length);
            }
            c.close();
            style.coverStyle = s;
        } else {
            JurnalStyle.JurnalStyleCoverRes s = new JurnalStyle.JurnalStyleCoverRes();
            s.res = tipeCover;
        }
        if (tipeBg == -1) {
            JurnalStyle.JurnalStyleBgImg s = new JurnalStyle.JurnalStyleBgImg();
            Cursor c = dbAttr.rawQuery("SELECT * FROM attr_val_blob WHERE attr_key = 'bgimg'", null);
            if (c.moveToFirst()) {
                byte[] img = c.getBlob(c.getColumnIndex("attr_val_blob"));
                s.img = BitmapFactory.decodeByteArray(img, 0, img.length);
            }
            c.close();
            style.bg = s;
        } else {
            JurnalStyle.JurnalStyleBgColor bg = new JurnalStyle.JurnalStyleBgColor();
            Cursor c = dbAttr.rawQuery("SELECT * FROM attr_val_text WHERE attr_key = 'bgimg'", null);
            if (c.moveToFirst()) {
                bg.color = c.getString(c.getColumnIndex("attr_val_text"));
            }
            c.close();
        }
    }

    @Override
    public void openChildrenDbs(DbFactory dbFactory) {
        dbAktivitas = dbFactory.getDbAktivitas(id);
        dbAttr = dbFactory.getDbAttr(new int[] {id});
    }

    @Override
    public void closeChildrenDbs() {
        dbAktivitas.close();
        dbAttr.close();
    }
}
