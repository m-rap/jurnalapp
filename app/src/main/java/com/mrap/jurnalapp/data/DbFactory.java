package com.mrap.jurnalapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbFactory {
    private static final String TAG = "DbFactory";
    private Context context;
    private String rootPath;

    public DbFactory(Context context, String rootPath) {
        this.context = context;
        this.rootPath = rootPath;
    }

    public SQLiteDatabase getDbJurnal() {
        SQLiteOpenHelper sqLiteOpenHelper = new SQLiteOpenHelper(context, rootPath + "/jurnal.db", null, 1) {
            @Override
            public void onCreate(SQLiteDatabase db) {
                db.execSQL("" +
                        "CREATE TABLE IF NOT EXISTS jurnal (" +
                        "jurnal_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "jurnal_judul TEXT," +
                        "jurnal_tipecover INTEGER," +
                        "jurnal_tipebg INTEGER" +
                        ")");
            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            }
        };
        return sqLiteOpenHelper.getWritableDatabase();
    }

    public SQLiteDatabase getDbAktivitas(int jurnalId) {
        SQLiteOpenHelper sqLiteOpenHelper = new SQLiteOpenHelper(context, rootPath + "/" + jurnalId + "/aktivitas.db", null, 1) {
            @Override
            public void onCreate(SQLiteDatabase db) {
                db.execSQL("" +
                        "CREATE TABLE IF NOT EXISTS aktivitas (" +
                        "aktivitas_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "aktivitas_nama TEXT," +
                        "aktivitas_isongoing INTEGER" +
                        ")");
            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            }
        };
        return sqLiteOpenHelper.getWritableDatabase();
    }

    public SQLiteDatabase getDbAktivitasItem(int jnlId, int aktId) {
        SQLiteOpenHelper sqLiteOpenHelper = new SQLiteOpenHelper(context, rootPath + "/" + jnlId + "/" + aktId + "/aktivitas_item.db", null, 1) {
            @Override
            public void onCreate(SQLiteDatabase db) {
                db.execSQL("" +
                        "CREATE TABLE IF NOT EXISTS aktivitas_item (" +
                        "aktitem_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "aktitem_tanggal INTEGER," +
                        "aktitem_judul TEXT" +
                        ")");
            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            }
        };
        return sqLiteOpenHelper.getWritableDatabase();
    }

    public SQLiteDatabase getDbAttr(int[] ids) {
        String idsPath = "";
        for (int i = 0; i < ids.length; i++) {
            idsPath += ids[i];
            if (i < ids.length - 1) {
                idsPath += "/";
            }
        }

        String createSql = "" +
                "CREATE TABLE IF NOT EXISTS attr (" +
                "attr_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "attr_tipe INTEGER," +
                "attr_key TEXT," +
                "attr_val_text TEXT," +
                "attr_val_num NUMBER," +
                "attr_val_blob BLOB" +
                ")";
        String path = rootPath + "/" + idsPath + "/attr.db";

        SQLiteOpenHelper sqLiteOpenHelper = new SQLiteOpenHelper(context,
                path,
                null, 1) {
            @Override
            public void onCreate(SQLiteDatabase db) {
                Log.d(TAG, "creating attr table " + path + "\n" + createSql);
                db.execSQL(createSql);
            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            }
        };
        return sqLiteOpenHelper.getWritableDatabase();
    }
}
