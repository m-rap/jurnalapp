package com.mrap.jurnalapp;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

public class ViewConfirmation {
    public static int CODE_OK = 0;
    public static int CODE_CANCEL = -1;

    public void showModal(Activity that, ConstraintLayout root, boolean usingCheck, String checkText, ModalUtil.Callback callback) {
        showModal(that, root, null, usingCheck, checkText, callback);
    }

    public void showModal(Activity that, ConstraintLayout root, String message, boolean usingCheck, String checkText, ModalUtil.Callback callback) {
        ConstraintLayout viewConfirmation = (ConstraintLayout) LayoutInflater.from(that).inflate(R.layout.view_confirmation, null);
        ModalUtil modalUtil = new ModalUtil();
        ConstraintLayout bgLayout = modalUtil.createModal(that, root, viewConfirmation);

        if (message != null && !message.isEmpty()) {
            TextView textView = viewConfirmation.findViewById(R.id.cnf_txtMessage);
            textView.setText(message);
        }

        CheckBox checkBox = viewConfirmation.findViewById(R.id.cnf_check);
        if (!usingCheck) {
            checkBox.setVisibility(View.GONE);
        } else {
            checkBox.setText(checkText);
        }

        Button button = viewConfirmation.findViewById(R.id.cnf_btnOk);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (usingCheck) {
                    CheckBox checkBox = viewConfirmation.findViewById(R.id.cnf_check);
                    if (checkBox.isChecked()) {
                        callback.onCallback(-1, CODE_OK, null);
                        root.removeView(bgLayout);
                    } else {
                        checkBox.requestFocus();
                    }
                } else {
                    callback.onCallback(-1, CODE_OK, null);
                    root.removeView(bgLayout);
                }
            }
        });

        button = viewConfirmation.findViewById(R.id.cnf_btnCancel);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onCallback(-1, CODE_CANCEL, null);
                root.removeView(bgLayout);
            }
        });
    }
}
