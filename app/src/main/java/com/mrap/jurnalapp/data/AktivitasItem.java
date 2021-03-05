package com.mrap.jurnalapp.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.SparseArray;

import java.util.Date;

public class AktivitasItem extends JnlData {
    public JnlAktivitas owner = null;

    public int id = -1;
    public Date tanggal = null;
    public String judul = "";
    public String note = "";
    public SparseArray<Bitmap> pics = new SparseArray<>();

    private SQLiteDatabase dbAttr = null;

    public void loadPics() {
        pics.clear();

        Cursor c = dbAttr.rawQuery("SELECT attr_val_blob FROM attr WHERE attr_key = 'pic'", null);
        if (c.moveToFirst()) {
            int idxBlob = c.getColumnIndex("attr_val_blob");
            int idxId = c.getColumnIndex("attr_id");
            do {
                byte[] bs = c.getBlob(idxBlob);
                Bitmap bmp = BitmapFactory.decodeByteArray(bs, 0, bs.length);
                pics.put(c.getInt(idxId), bmp);
            } while (c.moveToNext());
        }
        c.close();
    }

    @Override
    public void openChildrenDbs(DbFactory dbFactory) {
        dbAttr = dbFactory.getDbAttr(new int[] {owner.owner.id, owner.id, id});
    }

    @Override
    public void closeChildrenDbs() {
        if (dbAttr != null) {
            dbAttr.close();
        }
    }
}
