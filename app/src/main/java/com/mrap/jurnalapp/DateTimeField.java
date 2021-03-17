package com.mrap.jurnalapp;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

public class DateTimeField extends LinearLayout {
    private Date date;

    public DateTimeField(Context context) {
        this(context, null);
    }

    public DateTimeField(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        ConstraintLayout viewDateTime = (ConstraintLayout)LayoutInflater.from(context).inflate(R.layout.view_datetimefield, null);
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

    private void setValidateEvent(ConstraintLayout viewDateTime, int txtId, int mode, int min, int max) {
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

    private void setArrowsOnClick(ConstraintLayout viewDateTime, int min, int max, int mode, int btnUpId, int btnDownId, int txtId) {
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
        ConstraintLayout viewDateTime = (ConstraintLayout)getChildAt(0);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int value = calendar.get(mode) + 1;
        if (value > max) {
            value = min;
        }
        calendar.set(mode, value);
        date = calendar.getTime();
        TextView textView1 = viewDateTime.findViewById(txtId);
        if (mode == Calendar.MONTH) {
            textView1.setText((value + 1) + "");
        } else {
            textView1.setText(value + "");
        }
    }

    private void decreaseField(int min, int max, int mode, int txtId) {
        ConstraintLayout viewDateTime = (ConstraintLayout)getChildAt(0);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int value = calendar.get(mode) - 1;
        if (value < min) {
            value = max;
        }
        calendar.set(mode, value);
        date = calendar.getTime();
        TextView textView1 = viewDateTime.findViewById(txtId);
        if (mode == Calendar.MONTH) {
            textView1.setText((value + 1) + "");
        } else {
            textView1.setText(value + "");
        }
    }

    private void validateEditText(EditText t, int min, int max, int mode) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int value = calendar.get(mode);
        try {
            value = Integer.parseInt(t.getText().toString()) - 1;
            if (value < min || value > max) {
                if (mode == Calendar.MONTH) {
                    t.setText((calendar.get(mode) + 1) + "");
                } else {
                    t.setText(calendar.get(mode) + "");
                }
                calendar.set(mode, value);
                date = calendar.getTime();
            }
        } catch (Exception ex) {
            if (mode == Calendar.MONTH) {
                t.setText((value + 1) + "");
            } else {
                t.setText(value + "");
            }
        }
    }

    public void setDate(Date date) {
        this.date = date;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        View viewDateTime = getChildAt(0);
        EditText textView = viewDateTime.findViewById(R.id.txtDate);
        textView.setText(calendar.get(Calendar.DATE) + "");

        textView = viewDateTime.findViewById(R.id.txtMon);
        textView.setText((calendar.get(Calendar.MONTH) + 1) + "");

        textView = viewDateTime.findViewById(R.id.txtYear);
        textView.setText(calendar.get(Calendar.YEAR) + "");

        textView = viewDateTime.findViewById(R.id.txtHour);
        textView.setText(calendar.get(Calendar.HOUR_OF_DAY) + "");

        textView = viewDateTime.findViewById(R.id.txtMin);
        textView.setText(calendar.get(Calendar.MINUTE) + "");

        textView = viewDateTime.findViewById(R.id.txtSec);
        textView.setText(calendar.get(Calendar.SECOND) + "");
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
