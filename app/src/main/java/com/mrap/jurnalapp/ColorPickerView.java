package com.mrap.jurnalapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

// reference: https://stackoverflow.com/questions/6980906/android-color-picker by Robo and JRowan

public class ColorPickerView extends View {
    private int colorPadSize = 0;
    private int currWidth = 0;
    private int currHeight = 0;

    public interface OnColorChangedListener {
        void colorChanged(String key, int color);
    }

    private Paint mPaint;
    private float mCurrentHue = 0;
    private int colorPadX = 0, colorPadY = 0;
    private int mCurrentColor, mDefaultColor;
    private int[] mHueBarColors = new int[258];
    private int[] mMainColors = new int[65536];
    private OnColorChangedListener mListener;

    public ColorPickerView(Context context) {
        this(context, null, null, Color.parseColor("#000000"), Color.parseColor("#000000"));
    }

    public ColorPickerView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, null, Color.parseColor("#000000"), Color.parseColor("#000000"));
    }

    public ColorPickerView(Context c, AttributeSet attributeSet, OnColorChangedListener l, int color,
                    int defaultColor) {
        super(c, attributeSet);
        mListener = l;
        mDefaultColor = defaultColor;

        // Get the current hue from the current color and update the main
        // color field
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        //mCurrentHue = hsv[0];
        mCurrentHue = 0;
        updateMainColors();

        mCurrentColor = color;

        // Initialize the colors of the hue slider bar
        int index = 0;
        for (float i = 0; i < 256; i += 256 / 42) // Red (#f00) to pink
        // (#f0f)
        {
            mHueBarColors[index] = Color.rgb(255, 0, (int) i);
            index++;
        }
        for (float i = 0; i < 256; i += 256 / 42) // Pink (#f0f) to blue
        // (#00f)
        {
            mHueBarColors[index] = Color.rgb(255 - (int) i, 0, 255);
            index++;
        }
        for (float i = 0; i < 256; i += 256 / 42) // Blue (#00f) to light
        // blue (#0ff)
        {
            mHueBarColors[index] = Color.rgb(0, (int) i, 255);
            index++;
        }
        for (float i = 0; i < 256; i += 256 / 42) // Light blue (#0ff) to
        // green (#0f0)
        {
            mHueBarColors[index] = Color.rgb(0, 255, 255 - (int) i);
            index++;
        }
        for (float i = 0; i < 256; i += 256 / 42) // Green (#0f0) to yellow
        // (#ff0)
        {
            mHueBarColors[index] = Color.rgb((int) i, 255, 0);
            index++;
        }
        for (float i = 0; i < 256; i += 256 / 42) // Yellow (#ff0) to red
        // (#f00)
        {
            mHueBarColors[index] = Color.rgb(255, 255 - (int) i, 0);
            index++;
        }

        // Initializes the Paint that will draw the View
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setTextSize(12);
    }

    // Get the current selected color from the hue bar
    private int getCurrentMainColor() {
        if (mCurrentHue >= 0 && mCurrentHue < mHueBarColors.length) {
            return mHueBarColors[(int)mCurrentHue];
        }
//        int translatedHue = 255 - (int) (mCurrentHue * 255 / 360);
//        int index = 0;
//        for (float i = 0; i < 256; i += 256 / 42) {
//            if (index == translatedHue)
//                return Color.rgb(255, 0, (int) i);
//            index++;
//        }
//        for (float i = 0; i < 256; i += 256 / 42) {
//            if (index == translatedHue)
//                return Color.rgb(255 - (int) i, 0, 255);
//            index++;
//        }
//        for (float i = 0; i < 256; i += 256 / 42) {
//            if (index == translatedHue)
//                return Color.rgb(0, (int) i, 255);
//            index++;
//        }
//        for (float i = 0; i < 256; i += 256 / 42) {
//            if (index == translatedHue)
//                return Color.rgb(0, 255, 255 - (int) i);
//            index++;
//        }
//        for (float i = 0; i < 256; i += 256 / 42) {
//            if (index == translatedHue)
//                return Color.rgb((int) i, 255, 0);
//            index++;
//        }
//        for (float i = 0; i < 256; i += 256 / 42) {
//            if (index == translatedHue)
//                return Color.rgb(255, 255 - (int) i, 0);
//            index++;
//        }
        return Color.RED;
    }

    // Update the main field colors depending on the current selected hue
    private void updateMainColors() {
        int mainColor = getCurrentMainColor();
        int index = 0;
        //int[] topColors = new int[256];
        int[] topColors = new int[colorPadSize];
        int len = colorPadSize * colorPadSize;
        if (len > mMainColors.length) {
            mMainColors = new int[len];
        }
        //for (int y = 0; y < 256; y++) {
        for (int y = 0; y < colorPadSize; y++) {
            //for (int x = 0; x < 256; x++) {
            for (int x = 0; x < colorPadSize; x++) {
                if (y == 0) {
//                    mMainColors[index] = Color.rgb(
//                            255 - (255 - Color.red(mainColor)) * x / 255,
//                            255 - (255 - Color.green(mainColor)) * x / 255,
//                            255 - (255 - Color.blue(mainColor)) * x / 255);
                    mMainColors[index] = Color.rgb(
                            255 - (255 - Color.red(mainColor)) * x / colorPadSize,
                            255 - (255 - Color.green(mainColor)) * x / colorPadSize,
                            255 - (255 - Color.blue(mainColor)) * x / colorPadSize);
                    topColors[x] = mMainColors[index];
                } else
//                    mMainColors[index] = Color.rgb(
//                            (255 - y) * Color.red(topColors[x]) / 255,
//                            (255 - y) * Color.green(topColors[x]) / 255,
//                            (255 - y) * Color.blue(topColors[x]) / 255);
                    mMainColors[index] = Color.rgb(
                            (colorPadSize - 1 - y) * Color.red(topColors[x]) / colorPadSize,
                            (colorPadSize - 1 - y) * Color.green(topColors[x]) / colorPadSize,
                            (colorPadSize - 1 - y) * Color.blue(topColors[x]) / colorPadSize);
                index++;
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        setMeasuredDimension(276, 366);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        currWidth = getMeasuredWidth();
        currHeight = getMeasuredHeight();
        colorPadSize = currHeight - 50;
        if (colorPadSize < 0) {
            colorPadSize = 0;
        }
        if (currWidth < colorPadSize) {
            colorPadSize = currWidth;
        }

        int x = 0;
        int segmentLength = currWidth / 6;
        mHueBarColors = new int[currWidth];
        for (int i = 0; i < segmentLength; i++, x++) // Red (#f00) to pink (#f0f)
        {
            mHueBarColors[x] = Color.rgb(255, 0, (int)(i * 256 / segmentLength));
        }
        for (int i = 0; i < segmentLength; i++, x++) // Pink (#f0f) to blue (#00f)
        {
            mHueBarColors[x] = Color.rgb(255 - (int)(i * 256 / segmentLength), 0, 255);
        }
        for (int i = 0; i < segmentLength; i++, x++) // Blue (#00f) to light blue (#0ff)
        {
            mHueBarColors[x] = Color.rgb(0, (int)(i * 256 / segmentLength), 255);
        }
        for (int i = 0; i < segmentLength; i++, x++) // Light blue (#0ff) to green (#0f0)
        {
            mHueBarColors[x] = Color.rgb(0, 255, 255 - (int)(i * 256 / segmentLength));
        }
        for (int i = 0; i < segmentLength; i++, x++) // Green (#0f0) to yellow (#ff0)
        {
            mHueBarColors[x] = Color.rgb((int)(i * 256 / segmentLength), 255, 0);
        }
        for (int i = 0; i < segmentLength; i++, x++) // Yellow (#ff0) to red (#f00)
        {
            mHueBarColors[x] = Color.rgb(255, 255 - (int)(i * 256 / segmentLength), 0);
        }
        for (; x < currWidth; x++) {
            mHueBarColors[x] = Color.rgb(0, 0, 0);
        }

        updateMainColors();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //int translatedHue = 255 - (int) (mCurrentHue * 255 / 360);
        int translatedHue = (int)mCurrentHue;
        // Display all the colors of the hue bar with lines
        //for (int x = 0; x < 256; x++) {
        int canvasWidth = canvas.getWidth();
        int canvasHeight = canvas.getHeight();
        for (int x = 0; x < currWidth && x < canvasWidth; x++) {
            // If this is not the current selected hue, display the actual
            // color
            if (translatedHue != x) {
                mPaint.setColor(mHueBarColors[x]);
                mPaint.setStrokeWidth(1);
            } else // else display a slightly larger black line
            {
                mPaint.setColor(Color.BLACK);
                mPaint.setStrokeWidth(3);
            }
            //canvas.drawLine(x + 10, 0, x + 10, 40, mPaint);
            // canvas.drawLine(0, x+10, 40, x+10, mPaint);
            canvas.drawLine(x, 0, x, 40, mPaint);
        }

        // Display the main field colors using LinearGradient
        //for (int x = 0; x < 256; x++) {
        for (int x = 0; x < colorPadSize; x++) {
            int[] colors = new int[2];
            colors[0] = mMainColors[x];
            colors[1] = Color.BLACK;
//            Shader shader = new LinearGradient(0, 50, 0, 306, colors, null,
//                    Shader.TileMode.REPEAT);
            Shader shader = new LinearGradient(0, 50, 0, colorPadSize + 50, colors, null,
                    Shader.TileMode.REPEAT);
            mPaint.setShader(shader);
//            canvas.drawLine(x + 10, 50, x + 10, 306, mPaint);
            canvas.drawLine(x, 50, x, colorPadSize + 50, mPaint);
        }
        mPaint.setShader(null);

        // Display the circle around the currently selected color in the
        // main field
        //if (colorPadX != 0 && colorPadY != 0) {
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setColor(Color.BLACK);
            canvas.drawCircle(colorPadX, colorPadY + 50, 10, mPaint);
        //}

        // Draw a 'button' with the currently selected color
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mCurrentColor);
//        canvas.drawRect(10, 316, 138, 356, mPaint);
        canvas.drawRect(canvasWidth - 40, canvasHeight - 40, canvasWidth, canvasHeight, mPaint);

        // Set the text color according to the brightness of the color
        if (Color.red(mCurrentColor) + Color.green(mCurrentColor)
                + Color.blue(mCurrentColor) < 384)
            mPaint.setColor(Color.WHITE);
        else
            mPaint.setColor(Color.BLACK);
//        canvas.drawText(
//                getResources()
//                        .getString(R.string.settings_bg_color_confirm), 74,
//                340, mPaint);

        // Draw a 'button' with the default color
//        mPaint.setStyle(Paint.Style.FILL);
//        mPaint.setColor(mDefaultColor);
//        canvas.drawRect(138, 316, 266, 356, mPaint);

        // Set the text color according to the brightness of the color
        if (Color.red(mDefaultColor) + Color.green(mDefaultColor)
                + Color.blue(mDefaultColor) < 384)
            mPaint.setColor(Color.WHITE);
        else
            mPaint.setColor(Color.BLACK);
//        canvas.drawText(
//                getResources().getString(
//                        R.string.settings_default_color_confirm), 202, 340,
//                mPaint);
    }

//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        setMeasuredDimension(276, 366);
//    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() != MotionEvent.ACTION_DOWN)
            return true;
        float x = event.getX();
        float y = event.getY();

        boolean changed = false;

        // If the touch event is located in the hue bar
        //if (x > 10 && x < 266 && y > 0 && y < 40) {
        if (y > 0 && y < 40) {
            // Update the main field colors
            //mCurrentHue = (255 - x) * 360 / 255;
            mCurrentHue = x;
            updateMainColors();

            // Update the current selected color
            //int transX = mCurrentX - 10;
            //int transY = mCurrentY - 60;
            //int index = 256 * (transY - 1) + transX;
            int index = colorPadSize * (colorPadY - 1) + colorPadX;
            if (index > 0 && index < mMainColors.length)
                mCurrentColor = mMainColors[index];

            // Force the redraw of the dialog
            //invalidate();

            changed = true;
        }

        // If the touch event is located in the main field
        //if (x > 10 && x < 266 && y > 50 && y < 306) {
        if (y >= 50) {
            colorPadX = (int) x;
            if (colorPadX >= colorPadSize) {
                colorPadX = colorPadSize - 1;
            }
            colorPadY = (int) y - 50;
            if (colorPadY >= colorPadSize) {
                colorPadY = colorPadSize - 1;
            }
            int index = colorPadSize * (colorPadY) + colorPadX;
            if (index > 0 && index < mMainColors.length) {
                // Update the current color
                mCurrentColor = mMainColors[index];
                // Force the redraw of the dialog
                //invalidate();

                changed = true;
            }
        }

        if (changed) {
            invalidate();
        }

        // If the touch event is located in the left button, notify the
        // listener with the current color
        //if (mListener != null && x > 10 && x < 138 && y > 316 && y < 356)
        if (mListener != null && changed) {
            mListener.colorChanged("", mCurrentColor);
        }

        // If the touch event is located in the right button, notify the
        // listener with the default color
//        if (mListener != null && x > 138 && x < 266 && y > 316 && y < 356)
//            mListener.colorChanged("", mDefaultColor);

        return true;
    }

    int getCurrentColor() {
        return mCurrentColor;
    }
}