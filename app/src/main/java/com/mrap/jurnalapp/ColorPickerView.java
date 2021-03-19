package com.mrap.jurnalapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

// reference: https://stackoverflow.com/questions/6980906/android-color-picker by Robo and JRowan

public class ColorPickerView extends View {
    private static final String TAG = "ColorPickerView";
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
//    private int[] mHueBarColors = new int[258];
    private int[] mHueBarColors = new int[360];
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
        mCurrentHue = hsv[0];

        updateHueColors();
        updateMainColors();

        mCurrentColor = color;

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
        return mHueBarColors[0];
    }

    private void updateHueColors() {

        for (int i = 0; i < 360; i++) {
            mHueBarColors[i] = Color.HSVToColor(new float[] {i, 1, 1});
        }

//        int x = 0;
//        int segmentLength = currWidth / 6;
//        if (currWidth > mHueBarColors.length) {
//            mHueBarColors = new int[currWidth];
//        }
//        for (int i = 0; i < segmentLength; i++, x++) // Red (#f00) to pink (#f0f)
//        {
//            mHueBarColors[x] = Color.rgb(255, 0, (int)(i * 256 / segmentLength));
//        }
//        for (int i = 0; i < segmentLength; i++, x++) // Pink (#f0f) to blue (#00f)
//        {
//            mHueBarColors[x] = Color.rgb(255 - (int)(i * 256 / segmentLength), 0, 255);
//        }
//        for (int i = 0; i < segmentLength; i++, x++) // Blue (#00f) to light blue (#0ff)
//        {
//            mHueBarColors[x] = Color.rgb(0, (int)(i * 256 / segmentLength), 255);
//        }
//        for (int i = 0; i < segmentLength; i++, x++) // Light blue (#0ff) to green (#0f0)
//        {
//            mHueBarColors[x] = Color.rgb(0, 255, 255 - (int)(i * 256 / segmentLength));
//        }
//        for (int i = 0; i < segmentLength; i++, x++) // Green (#0f0) to yellow (#ff0)
//        {
//            mHueBarColors[x] = Color.rgb((int)(i * 256 / segmentLength), 255, 0);
//        }
//        for (int i = 0; i < segmentLength; i++, x++) // Yellow (#ff0) to red (#f00)
//        {
//            mHueBarColors[x] = Color.rgb(255, 255 - (int)(i * 256 / segmentLength), 0);
//        }
//        for (; x < currWidth; x++) {
//            mHueBarColors[x] = Color.rgb(0, 0, 0);
//        }
    }

    // Update the main field colors depending on the current selected hue
    private void updateMainColors() {
        int mainColor = getCurrentMainColor();
        int index = 0;
        int[] topColors = new int[colorPadSize];
        int len = colorPadSize * colorPadSize;
        if (len > mMainColors.length) {
            mMainColors = new int[len];
        }

        for (int y = 0; y < colorPadSize; y++) {
            for (int x = 0; x < colorPadSize; x++) {
                if (y == 0) {
                    mMainColors[index] = Color.rgb(
                            255 - (255 - Color.red(mainColor)) * x / colorPadSize,
                            255 - (255 - Color.green(mainColor)) * x / colorPadSize,
                            255 - (255 - Color.blue(mainColor)) * x / colorPadSize);
                    topColors[x] = mMainColors[index];
                } else {
                    mMainColors[index] = Color.rgb(
                            (colorPadSize - 1 - y) * Color.red(topColors[x]) / colorPadSize,
                            (colorPadSize - 1 - y) * Color.green(topColors[x]) / colorPadSize,
                            (colorPadSize - 1 - y) * Color.blue(topColors[x]) / colorPadSize);
                }

                index++;
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
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

        //updateHueColors();
        updateMainColors();

//        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //int translatedHue = 255 - (int) (mCurrentHue * 255 / 360);
//        int translatedHue = (int)mCurrentHue;
        int translatedHue = (int)(mCurrentHue * currWidth / 360);
        Log.d(TAG, "translated hue " + translatedHue + " " + mCurrentHue + " " + currWidth);

        // Display all the colors of the hue bar with lines
        //for (int x = 0; x < 256; x++) {
        int canvasWidth = canvas.getWidth();
        int canvasHeight = canvas.getHeight();
        for (int x = 0; x < currWidth && x < canvasWidth; x++) {
            // If this is not the current selected hue, display the actual
            // color
            if (translatedHue != x) {
//                mPaint.setColor(mHueBarColors[x]);
                mPaint.setColor(mHueBarColors[x * 360 / currWidth]);
                mPaint.setStrokeWidth(1);
            } else { // else display a slightly larger black line
                Log.d(TAG, "draw black line at x " + x);
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
            mPaint.setStrokeWidth(3);
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

    final static int ACTION_NONE = 0;
    final static int ACTION_HUE = 1;
    final static int ACTION_COLORPAD = 2;

    int currentTouchAction = ACTION_NONE;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        boolean changed = false;

        int action = event.getAction() & MotionEvent.ACTION_MASK;
        if (action == MotionEvent.ACTION_DOWN) {
            if (y > 0 && y < 40) {
                currentTouchAction = ACTION_HUE;
                changed = doActionHue(x);
            }
            if (y >= 50) {
                currentTouchAction = ACTION_COLORPAD;
                changed = doActionColorpad((int) x, (int) y);
            }
        } else if (action == MotionEvent.ACTION_UP) {
            currentTouchAction = ACTION_NONE;
        } else if (action == MotionEvent.ACTION_MOVE) {
            if (currentTouchAction == ACTION_HUE) {
                changed = doActionHue(x);
            } else if (currentTouchAction == ACTION_COLORPAD) {
                changed = doActionColorpad((int) x, (int) y);
            }
        }

        if (changed) {
            invalidate();
        }

        if (mListener != null && changed) {
            mListener.colorChanged("", mCurrentColor);
        }

        return true;
    }

    private boolean doActionColorpad(int x, int y) {
        colorPadX = x;
        if (colorPadX >= colorPadSize) {
            colorPadX = colorPadSize - 1;
        }
        colorPadY = y - 50;
        if (colorPadY >= colorPadSize) {
            colorPadY = colorPadSize - 1;
        }
        int index = colorPadSize * (colorPadY) + colorPadX;
        if (index < 0) {
            index = 0;
        }
        if (index >= mMainColors.length) {
            index = mMainColors.length - 1;
        }
        mCurrentColor = mMainColors[index];
        return true;
    }

    private boolean doActionHue(float x) {
//        mCurrentHue = x;
        mCurrentHue = x * 360 / currWidth;
        updateMainColors();

        int index = colorPadSize * (colorPadY - 1) + colorPadX;
        if (index < 0) {
            index = 0;
        }
        if (index >= mMainColors.length) {
            index = mMainColors.length - 1;
        }
        mCurrentColor = mMainColors[index];
        return true;
    }


    int getCurrentColor() {
        return mCurrentColor;
    }

    void setCurrentColor(int color) {
        mCurrentColor = color;
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
//        mCurrentHue = hsv[0] * currWidth / 360;
        mCurrentHue = hsv[0];

        Log.d(TAG, String.format("current color %d %d %d hsv %f %f %f", Color.red(color), Color.green(color),
                Color.blue(color), hsv[0], hsv[1], hsv[2]));

        updateMainColors();

        int index = 0;
        int len = colorPadSize * colorPadSize;

        for (; index < len; index++) {
            if (mMainColors[index] == mCurrentColor) {
                colorPadY = index / colorPadSize;
                colorPadX = index % colorPadSize;
            }
        }

        invalidate();
    }
}