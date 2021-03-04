package com.mrap.jurnalapp.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.SparseArray;

import java.util.Date;

public class JnlAktivitas extends JnlData {
    Jurnal owner = null;

    public int id;
    public String nama = null;
    public SparseArray<AktivitasItem> aktivitasItems = new SparseArray<>();
    public boolean isOnGoing = false;

    private SQLiteDatabase dbAktivitasItem = null;

    public void loadAktivitasItems(DbFactory dbFactory) {
        Cursor c = dbAktivitasItem.rawQuery("SELECT * FROM aktivitas_item", null);
        if (!c.moveToFirst()) {
            c.close();
            return;
        }
        aktivitasItems.clear();
        int idxId = c.getColumnIndex("aktitem_id");
        int idxTanggal = c.getColumnIndex("aktitem_tanggal");
        int idxJudul = c.getColumnIndex("aktitem_judul");
        do {
            AktivitasItem item = new AktivitasItem();
            item.id = c.getInt(idxId);
            item.tanggal = new Date(c.getInt(idxTanggal));
            item.judul = c.getString(idxJudul);
            aktivitasItems.put(id, item);
        } while (c.moveToNext());
        c.close();
    }

    public void tambahAktivitasItem(String judul, Date tanggal) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("aktitem_tanggal", tanggal.getTime());
        contentValues.put("aktitem_judul", judul);
        AktivitasItem aktivitasItem = new AktivitasItem();
        aktivitasItem.id = (int)dbAktivitasItem.insert("aktivitas_item", null, contentValues);
        aktivitasItem.judul = judul;
        aktivitasItem.owner = this;
        aktivitasItems.put(aktivitasItem.id, aktivitasItem);
    }

    @Override
    public void openChildrenDbs(DbFactory dbFactory) {
        dbAktivitasItem = dbFactory.getDbAktivitasItem(owner.id, id);
    }

    @Override
    public void closeChildrenDbs() {
        if (dbAktivitasItem != null) {
            dbAktivitasItem.close();
        }
        for (int i = 0; i < aktivitasItems.size(); i++) {
            aktivitasItems.valueAt(i).closeChildrenDbs();
        }
    }
}
