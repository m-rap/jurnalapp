package com.mrap.jurnalapp;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

import androidx.annotation.Nullable;

public class DateTimeField extends LinearLayout {
    private Date date;

    public DateTimeField(Context context) {
        this(context, null);
    }

    public DateTimeField(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        ViewGroup viewDateTime = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.view_datetimefield, null);
        addView(viewDateTime);

        setDate(new Date());

        setValidateEvent(viewDateTime, R.id.txtDate, Calendar.DATE, 1, 31);
        setValidateEvent(viewDateTime, R.id.txtMon, Calendar.MONTH, 0, 11);
        setValidateEvent(viewDateTime, R.id.txtYear, Calendar.YEAR, 0, 9999);
        setValidateEvent(viewDateTime, R.id.txtHour, Calendar.HOUR_OF_DAY, 0, 59);
        setValidateEvent(viewDateTime, R.id.txtMin, Calendar.MINUTE, 0, 59);
        setValidateEvent(viewDateTime, R.id.txtSec, Calendar.SECOND, 0, 59);

        setArrowsOnClick(viewDateTime, 1, 31, Calendar.DATE, R.id.btnUpDate, R.id.btnDownDate, R.id.txtDate);
        setArrowsOnClick(viewDateTime, 0, 11, Calendar.MONTH, R.id.btnUpMon, R.id.btnDownMon, R.id.txtMon);
        setArrowsOnClick(viewDateTime, 0, 9999, Calendar.YEAR, R.id.btnUpYear, R.id.btnDownYear, R.id.txtYear);
        setArrowsOnClick(viewDateTime, 0, 59, Calendar.HOUR_OF_DAY, R.id.btnUpHour, R.id.btnDownHour, R.id.txtHour);
        setArrowsOnClick(viewDateTime, 0, 59, Calendar.MINUTE, R.id.btnUpMin, R.id.btnDownMin, R.id.txtMin);
        setArrowsOnClick(viewDateTime, 0, 59, Calendar.SECOND, R.id.btnUpSec, R.id.btnDownSec, R.id.txtSec);
    }

    @Override
    public void setOrientation(int orientation) {
        super.setOrientation(orientation);

        LinearLayout viewDateTime = (LinearLayout)getChildAt(0);
        if (viewDateTime != null) {
            viewDateTime.setOrientation(orientation);
        }
    }

    private void setValidateEvent(ViewGroup viewDateTime, int txtId, int mode, int min, int max) {
        EditText textView = viewDateTime.findViewById(txtId);
        textView.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    validateEditText((EditText) v, min, max, mode);
                }
            }
        });
    }

    private void setArrowsOnClick(ViewGroup viewDateTime, int min, int max, int mode, int btnUpId, int btnDownId, int txtId) {
        ImageView imageView = viewDateTime.findViewById(btnUpId);
        imageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                increaseField(min, max, mode, txtId);
            }
        });

        imageView = viewDateTime.findViewById(btnDownId);
        imageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                decreaseField(min, max, mode, txtId);
            }
        });
    }

    private void increaseField(int min, int max, int mode, int txtId) {
        ViewGroup viewDateTime = (ViewGroup)getChildAt(0);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int value = calendar.get(mode) + 1;
        if (value > max) {
            value = min;
        }
        calendar.set(mode, value);
        date = calendar.getTime();

        setTextValue(max, mode, txtId, viewDateTime, value);
    }

    private void decreaseField(int min, int max, int mode, int txtId) {
        ViewGroup viewDateTime = (ViewGroup)getChildAt(0);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int value = calendar.get(mode) - 1;
        if (value < min) {
            value = max;
        }
        calendar.set(mode, value);
        date = calendar.getTime();

        setTextValue(max, mode, txtId, viewDateTime, value);
    }

    private void setTextValue(int max, int mode, int txtId, ViewGroup viewDateTime, int value) {
        EditText textView1 = viewDateTime.findViewById(txtId);

        setTextValue(max, mode, value, textView1);
    }

    private void setTextValue(int max, int mode, int value, EditText textView1) {
        int displayValue;
        if (mode == Calendar.MONTH) {
            displayValue = value + 1;
        } else {
            displayValue = value;
        }

        if (max <= 99) {
            textView1.setText(String.format("%02d", displayValue));
        } else if (max <= 9999) {
            textView1.setText(String.format("%04d", displayValue));
        }
    }

    private void validateEditText(EditText t, int min, int max, int mode) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int value = calendar.get(mode);
        int displayValue;
        try {
            value = Integer.parseInt(t.getText().toString()) - 1;
            if (value < min || value > max) {
                value = calendar.get(mode);
            } else {
                calendar.set(mode, value);
                date = calendar.getTime();
            }
        } catch (Exception ex) { }

        setTextValue(max, mode, value, t);
    }

    public void setDate(Date date) {
        this.date = date;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        View viewDateTime = getChildAt(0);
        EditText textView = viewDateTime.findViewById(R.id.txtDate);
        textView.setText(String.format("%02d", calendar.get(Calendar.DATE)));

        textView = viewDateTime.findViewById(R.id.txtMon);
        textView.setText(String.format("%02d", calendar.get(Calendar.MONTH) + 1));

        textView = viewDateTime.findViewById(R.id.txtYear);
        textView.setText(String.format("%04d", calendar.get(Calendar.YEAR)));

        textView = viewDateTime.findViewById(R.id.txtHour);
        textView.setText(String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY)));

        textView = viewDateTime.findViewById(R.id.txtMin);
        textView.setText(String.format("%02d", calendar.get(Calendar.MINUTE)));

        textView = viewDateTime.findViewById(R.id.txtSec);
        textView.setText(String.format("%02d", calendar.get(Calendar.SECOND)));
    }

    public Date getDate() throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int dayOfMon = calendar.get(Calendar.DATE), mon = calendar.get(Calendar.MONTH),
                year = calendar.get(Calendar.YEAR), hour = calendar.get(Calendar.HOUR_OF_DAY),
                min = calendar.get(Calendar.MINUTE), sec = calendar.get(Calendar.SECOND);
        try {
            View viewDateTime = getChildAt(0);
            EditText textView = viewDateTime.findViewById(R.id.txtDate);
            dayOfMon = Integer.parseInt(textView.getText().toString());

            textView = viewDateTime.findViewById(R.id.txtMon);
            mon = Integer.parseInt(textView.getText().toString()) - 1;

            textView = viewDateTime.findViewById(R.id.txtYear);
            year = Integer.parseInt(textView.getText().toString());

            textView = viewDateTime.findViewById(R.id.txtHour);
            hour = Integer.parseInt(textView.getText().toString());

            textView = viewDateTime.findViewById(R.id.txtMin);
            min = Integer.parseInt(textView.getText().toString());

            textView = viewDateTime.findViewById(R.id.txtSec);
            sec = Integer.parseInt(textView.getText().toString());

            calendar.set(year, mon, dayOfMon, hour, min, sec);
            this.date = calendar.getTime();

            return this.date;
        } catch (Exception ex) {
            calendar.set(year, mon, dayOfMon, hour, min, sec);
            this.date = calendar.getTime();

            setDate(this.date);
            throw ex;
        }
    }
}
