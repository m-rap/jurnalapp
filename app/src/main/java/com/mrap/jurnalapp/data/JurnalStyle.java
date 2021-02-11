package com.mrap.jurnalapp.data;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.widget.ImageView;

public class JurnalStyle {
    public JurnalStyleBg bg = new JurnalStyleBgColor();
    public JurnalStyleCover coverStyle = new JurnalStyleCover();

    public static abstract class JurnalStyleBg {
        public abstract void render(ImageView iv);
    }

    public static class JurnalStyleBgColor extends JurnalStyleBg {
        public String color = "#000000";

        @Override
        public void render(ImageView iv) {
            iv.setImageDrawable(null);
            iv.setBackgroundColor(Color.parseColor(color));
        }
    }

    public static class JurnalStyleBgImg extends JurnalStyleBg {
        public Bitmap img;

        @Override
        public void render(ImageView iv) {
            iv.setBackgroundColor(Color.parseColor("#00000000"));
            iv.setImageBitmap(img);
        }
    }

    public static class JurnalStyleCover {
        public void render(ImageView iv) {}
    }

    public static class JurnalStyleCoverRes extends JurnalStyleCover {
        public int res;

        @Override
        public void render(ImageView iv) {
            iv.setImageDrawable(null);
            iv.setImageResource(res);
        }
    }

    public static class JurnalStyleCoverImg extends JurnalStyleCover {
        public Bitmap img;

        @Override
        public void render(ImageView iv) {
            iv.setBackgroundColor(Color.parseColor("#00000000"));
            iv.setImageBitmap(img);
        }
    }
}
