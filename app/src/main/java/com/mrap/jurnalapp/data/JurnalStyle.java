package com.mrap.jurnalapp.data;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
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

    public static class JurnalStyleCoverRes extends JurnalStyleCover {
        public int res;

        @Override
        public void render(ImageView iv) {
            iv.setImageDrawable(null);
            iv.setImageResource(res);

            Log.d(TAG, "render setimageres to " + res + " " + R.drawable.jurnal_cover);
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
