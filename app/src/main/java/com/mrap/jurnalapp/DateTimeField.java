package com.mrap.jurnalapp;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
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
    }

    public void setDate(Date date) {
        this.date = date;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        View viewDateTime = getChildAt(0);
        EditText textView = viewDateTime.findViewById(R.id.txtDate);
        textView.setText(calendar.get(Calendar.DAY_OF_WEEK) + "");

        textView = viewDateTime.findViewById(R.id.txtMon);
        textView.setText(calendar.get(Calendar.MONTH) + "");

        textView = viewDateTime.findViewById(R.id.txtYear);
        textView.setText(calendar.get(Calendar.YEAR) + "");

        textView = viewDateTime.findViewById(R.id.txtHour);
        textView.setText(calendar.get(Calendar.HOUR_OF_DAY) + "");

        textView = viewDateTime.findViewById(R.id.txtMin);
        textView.setText(calendar.get(Calendar.MINUTE) + "");

        textView = viewDateTime.findViewById(R.id.txtSec);
        textView.setText(calendar.get(Calendar.SECOND) + "");
    }
}
