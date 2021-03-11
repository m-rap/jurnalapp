package com.mrap.jurnalapp.data;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.util.SparseArray;
import android.widget.ImageView;

import com.mrap.jurnalapp.R;

public class JurnalStyle {
    final static String TAG = "JurnalStyle";

    public JurnalStyleBg bg = new JurnalStyleBgColor();
    public JurnalStyleCover coverStyle = new JurnalStyleCover();

    public static abstract class JurnalStyleBg {
        public abstract void render(ImageView iv);
    }

    public static class JurnalStyleBgColor extends JurnalStyleBg {
        public String color = "#ffffff";

        @Override
        public void render(ImageView iv) {
            iv.setImageDrawable(null);
            iv.setBackgroundColor(Color.parseColor(color));
        }
    }

    public static class JurnalStyleBgImg extends JurnalStyleBg {
        public Bitmap img = null;

        @Override
        public void render(ImageView iv) {
            iv.setBackgroundColor(Color.parseColor("#00000000"));
            iv.setImageBitmap(img);
        }
    }

    public static class JurnalStyleCover {
        public void render(ImageView iv) { Log.d(TAG, "cover not set, render nothing"); }
    }


    public static class Util {
        public SparseArray<Integer> getTypeResMap() {
            SparseArray<Integer> typeResMap = new SparseArray<>();
            typeResMap.put(0, R.drawable.jurnal_cover);
            typeResMap.put(1, R.drawable.jurnal_cover_2);
            return typeResMap;
        }
    }

    public static class JurnalStyleCoverTipe extends JurnalStyleCover {
        public int tipe;
        private final SparseArray<Integer> typeResMap;

        public JurnalStyleCoverTipe() {
            typeResMap = new Util().getTypeResMap();
        }

        @Override
        public void render(ImageView iv) {
            int res = typeResMap.get(tipe);
            iv.setImageDrawable(null);
            iv.setImageResource(res);

//            Log.d(TAG, "render setimageres to " + res);
        }
    }

    public static class JurnalStyleCoverImg extends JurnalStyleCover {
        public Bitmap img = null;

        @Override
        public void render(ImageView iv) {
            iv.setBackgroundColor(Color.parseColor("#00000000"));
            iv.setImageBitmap(img);
        }
    }
}
