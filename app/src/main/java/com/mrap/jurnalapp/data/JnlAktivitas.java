package com.mrap.jurnalapp.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.util.SparseArray;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class JnlAktivitas extends JnlData {
    private static final String TAG = "JnlAktivitas";
    Jurnal owner = null;

    public int id;
    public String nama = null;
    public SparseArray<AktivitasItem> aktivitasItems = new SparseArray<>();
    public boolean isOnGoing = false;

    private SQLiteDatabase dbAktivitasItem = null;
    private DbFactory dbFactory = null;

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
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        do {
            AktivitasItem item = new AktivitasItem();
            item.id = c.getInt(idxId);
            long tgl = c.getLong(idxTanggal);
            item.tanggal = new Date(tgl * 1000);
            item.judul = c.getString(idxJudul);
            item.owner = this;
            aktivitasItems.put(item.id, item);
        } while (c.moveToNext());
        c.close();
    }

    public void tambahAktivitasItem(String judul, String note, Date tanggal) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("aktitem_tanggal", tanggal.getTime() / 1000);
        contentValues.put("aktitem_judul", judul);
        contentValues.put("aktitem_note", note);
        AktivitasItem aktivitasItem = new AktivitasItem();
        aktivitasItem.id = (int)dbAktivitasItem.insert("aktivitas_item", null, contentValues);
        aktivitasItem.judul = judul;
        aktivitasItem.note = note;
        aktivitasItem.owner = this;
//        aktivitasItem.openChildrenDbs(dbFactory);
//        aktivitasItem.loadPics();
//        aktivitasItem.closeChildrenDbs();
        aktivitasItems.put(aktivitasItem.id, aktivitasItem);
        Log.d(TAG, "tambah aktivitas " + aktivitasItem.id + " " + judul + " " + tanggal + " length " + aktivitasItems.size());
    }

    public void editAktivitasItem(int id, String judul, String note, Date tanggal) {
        try {
            dbAktivitasItem.execSQL("UPDATE aktivitas_item SET aktitem_judul = ?, aktitem_note = ?, aktitem_tanggal = ? WHERE aktitem_id = ?", new String[]{
                    judul, note, (tanggal.getTime() / 1000) + "", id + ""
            });
            AktivitasItem aktivitasItem = aktivitasItems.get(id);
            if (aktivitasItem == null) {
                aktivitasItem = new AktivitasItem();
                aktivitasItems.put(id, aktivitasItem);
            }
            aktivitasItem.judul = judul;
            aktivitasItem.note = note;
            aktivitasItem.tanggal = tanggal;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void hapusAktivitasItem(int id) {
        try {
            dbAktivitasItem.execSQL("DELETE FROM aktivitas_item WHERE aktitem_id = ?", new String[]{
                    id + ""
            });
            aktivitasItems.remove(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void hapusAktivitasItemTerakhir() {
        Cursor c = dbAktivitasItem.rawQuery("SELECT * FROM aktivitas_item ORDER BY aktitem_tanggal DESC LIMIT 1", null);
        if (!c.moveToFirst()) {
            c.close();
            return;
        }

        int idIdx = c.getColumnIndex("aktitem_id");
        int aktItemId = c.getInt(idIdx);

        c.close();

        hapusAktivitasItem(aktItemId);
    }

    public int getSortedAktItemsByDate(ArrayList<AktivitasItem> items, SparseArray<AktivitasItem> aktivitasItems) {
        int nAktItem = aktivitasItems.size();
        //Log.d(TAG, "nAktItem " + nAktItem);
        for (int i = 0; i < nAktItem; i++) {
            items.add(aktivitasItems.valueAt(i));
        }
        Collections.sort(items, new Comparator<AktivitasItem>() {
            @Override
            public int compare(AktivitasItem o1, AktivitasItem o2) {
                return o1.tanggal.getTime() < o2.tanggal.getTime() ? -1 : 1;
            }
        });
        return nAktItem;
    }

    @Override
    public void openChildrenDbs(DbFactory dbFactory) {
        this.dbFactory = dbFactory;
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
