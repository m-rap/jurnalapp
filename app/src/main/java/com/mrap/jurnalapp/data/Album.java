package com.mrap.jurnalapp.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.SparseArray;

public class Album extends JnlData {
    public SparseArray<Jurnal> jurnals = new SparseArray<>();

    private SQLiteDatabase dbJurnal = null;

    public void loadJurnals() {
        Cursor c = dbJurnal.rawQuery("SELECT * FROM jurnal", null);
        if (!c.moveToFirst()) {
            c.close();
            return;
        }
        jurnals.clear();
        Jurnal jurnal = new Jurnal();
        int idxId = c.getColumnIndex("jurnal_id");
        int idxJudul = c.getColumnIndex("jurnal_judul");
        int idxTipeCover = c.getColumnIndex("jurnal_tipecover");
        int idxTipeBg = c.getColumnIndex("jurnal_tipebg");
        do {
            jurnal.judul = c.getString(idxJudul);
            jurnal.id = c.getInt(idxId);
            jurnal.tipeCover = c.getInt(idxTipeCover);
            jurnal.tipeBg = c.getInt(idxTipeBg);
            jurnals.put(jurnal.id, jurnal);
        } while (c.moveToNext());
        c.close();
    }

    public Jurnal getJurnal(int id) {
        if (jurnals.indexOfKey(id) >= 0) {
            return jurnals.get(id);
        }

        Cursor c = dbJurnal.rawQuery("SELECT * FROM jurnal WHERE jurnal_id = ?", new String[] {id + ""});
        if (!c.moveToFirst()) {
            c.close();
            return null;
        }
        Jurnal jurnal = new Jurnal();
        int idxId = c.getColumnIndex("jurnal_id");
        int idxJudul = c.getColumnIndex("jurnal_judul");
        int idxTipeCover = c.getColumnIndex("jurnal_tipecover");
        int idxTipeBg = c.getColumnIndex("jurnal_tipebg");
        jurnal.judul = c.getString(idxJudul);
        jurnal.id = c.getInt(idxId);
        jurnal.tipeCover = c.getInt(idxTipeCover);
        jurnal.tipeBg = c.getInt(idxTipeBg);
        jurnals.put(jurnal.id, jurnal);
        c.close();

        return jurnal;
    }

    public void saveJurnal(Jurnal jurnal) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("jurnal_judul", jurnal.judul);
        jurnal.id = (int)dbJurnal.insert("jurnal", null, contentValues);
        jurnals.put(jurnal.id, jurnal);
    }

    @Override
    public void openChildrenDbs(DbFactory dbFactory) {
        dbJurnal = dbFactory.getDbJurnal();
    }

    @Override
    public void closeChildrenDbs() {
        if (dbJurnal != null) {
            dbJurnal.close();
        }
        for (int i = 0; i < jurnals.size(); i++) {
            jurnals.valueAt(i).closeChildrenDbs();
        }
    }
}
