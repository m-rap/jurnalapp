package com.mrap.jurnalapp.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.SparseArray;

import java.util.Date;

public class JnlAktivitas extends JnlData {
    Jurnal owner = null;

    int id;
    String nama = null;
    SparseArray<AktivitasItem> aktivitasItems = new SparseArray<>();
    boolean isOnGoing = false;

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

    @Override
    public void openChildrenDbs(DbFactory dbFactory) {
        dbAktivitasItem = dbFactory.getDbAktivitasItem(owner.id, id);
    }

    @Override
    public void closeChildrenDbs() {
        dbAktivitasItem.close();
    }
}
