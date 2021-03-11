package com.mrap.jurnalapp.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.util.SparseArray;

import java.io.ByteArrayOutputStream;
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

    public JnlAktivitas getAktivitas(int id) {
        Cursor c = dbAktivitas.rawQuery("SELECT * FROM aktivitas WHERE aktivitas_id = ?", new String[] {id + ""});
        if (!c.moveToFirst()) {
            c.close();
            return null;
        }
        int idxId = c.getColumnIndex("aktivitas_id");
        int idxNama = c.getColumnIndex("aktivitas_nama");
        int idxIsOnGoing = c.getColumnIndex("aktivitas_isongoing");
        JnlAktivitas jnlAktivitas = new JnlAktivitas();
        jnlAktivitas.id = c.getInt(idxId);
        jnlAktivitas.nama = c.getString(idxNama);
        jnlAktivitas.isOnGoing = c.getInt(idxIsOnGoing) == 1;
        jnlAktivitas.owner = this;
        aktivitases.put(jnlAktivitas.id, jnlAktivitas);
        c.close();
        return jnlAktivitas;
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
                JurnalStyle.JurnalStyleCoverTipe s2 = new JurnalStyle.JurnalStyleCoverTipe();
                s2.tipe = 0;
                style.coverStyle = s2;
            }
        } else {
            JurnalStyle.JurnalStyleCoverTipe s = new JurnalStyle.JurnalStyleCoverTipe();
            s.tipe = tipeCover;
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
            Cursor c = dbAttr.rawQuery("SELECT attr_val_text FROM attr WHERE attr_key = 'bgcolor'", null);
            if (c.moveToFirst()) {
                bg.color = c.getString(c.getColumnIndex("attr_val_text"));
            }
            c.close();
            style.bg = bg;
        }
    }

    public void saveStyle() {
        if (tipeCover == -1) {
            JurnalStyle.JurnalStyleCoverImg s = (JurnalStyle.JurnalStyleCoverImg)style.coverStyle;

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            s.img.compress(Bitmap.CompressFormat.PNG, 0, baos);
            byte[] imgBytes = baos.toByteArray();

            dbAttr.execSQL("DELETE FROM attr WHERE attr_key = 'coverimg'");
            ContentValues contentValues = new ContentValues();
            contentValues.put("attr_key", "coverimg");
            contentValues.put("attr_val_blob", imgBytes);
            dbAttr.insert("attr", null, contentValues);
        }

        if (tipeBg == -1) {
            JurnalStyle.JurnalStyleBgImg s = (JurnalStyle.JurnalStyleBgImg)style.bg;

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            s.img.compress(Bitmap.CompressFormat.PNG, 0, baos);
            byte[] imgBytes = baos.toByteArray();

            dbAttr.execSQL("DELETE FROM attr WHERE attr_key = 'bgimg'");
            ContentValues contentValues = new ContentValues();
            contentValues.put("attr_key", "bgimg");
            contentValues.put("attr_val_blob", imgBytes);
            dbAttr.insert("attr", null, contentValues);
        } else {
            JurnalStyle.JurnalStyleBgColor bg = (JurnalStyle.JurnalStyleBgColor)style.bg;

            dbAttr.execSQL("DELETE FROM attr WHERE attr_key = 'bgcolor'");
            ContentValues contentValues = new ContentValues();
            contentValues.put("attr_key", "bgcolor");
            contentValues.put("attr_val_text", bg.color);
            dbAttr.insert("attr", null, contentValues);
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
        jnlAktivitas.tambahAktivitasItem("Mulai", "", tglMulai);
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

    public void akhiriAktivitas(int itemId, Date tgl) {
        dbAktivitas.execSQL("UPDATE aktivitas SET aktivitas_isongoing = ? WHERE aktivitas_id = ?", new String[] {"0", itemId + ""});
        JnlAktivitas aktivitas = aktivitases.get(itemId);
        if (aktivitas == null) {
            aktivitas = getAktivitas(itemId);
            //aktivitases.put(itemId, aktivitas);
        }
        aktivitas.isOnGoing = false;
        aktivitas.openChildrenDbs(dbFactory);
        aktivitas.tambahAktivitasItem("Selesai", "", tgl);
        aktivitas.closeChildrenDbs();
        aktivitases.put(itemId, aktivitas);
    }

    public void hapusAktivitas(int itemId) {
        dbAktivitas.execSQL("DELETE FROM aktivitas WHERE aktivitas_id=?", new String[] {itemId + ""});
        aktivitases.remove(itemId);
    }
}
