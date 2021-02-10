package com.mrap.jurnalapp.data;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.widget.ImageView;

public class JurnalStyle {
    JurnalStyleBg bg = new JurnalStyleBgColor();
    JurnalStyleCover coverStyle;

    public static abstract class JurnalStyleBg {
        public abstract void render(ImageView iv);
    }

    public static class JurnalStyleBgColor extends JurnalStyleBg {
        String color = "#000";

        @Override
        public void render(ImageView iv) {
            iv.setImageDrawable(null);
            iv.setBackgroundColor(Color.parseColor(color));
        }
    }

    public static class JurnalStyleBgImg extends JurnalStyleBg {
        Bitmap img;

        @Override
        public void render(ImageView iv) {
            iv.setBackgroundColor(Color.parseColor("#00000000"));
            iv.setImageBitmap(img);
        }
    }

    public static abstract class JurnalStyleCover {
        public abstract void render(ImageView iv);
    }

    public static class JurnalStyleCoverRes extends JurnalStyleCover {
        int res;
        @Override
        public void render(ImageView iv) {
            iv.setImageDrawable(null);
            iv.setImageResource(res);
        }
    }

    public static class JurnalStyleCoverImg extends JurnalStyleCover {
        Bitmap img;
        @Override
        public void render(ImageView iv) {
            iv.setBackgroundColor(Color.parseColor("#00000000"));
            iv.setImageBitmap(img);
        }
    }
}
