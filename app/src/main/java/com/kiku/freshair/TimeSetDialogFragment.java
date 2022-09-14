package com.kiku.freshair;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

public class TimeSetDialogFragment extends DialogFragment {
    private OnClickListener onClickListener;
    private EditText editText_setTime;
    private TimePicker timePicker;
    private int h, m;

    private TextView test1;

    public interface OnClickListener {
        void onOkClicked(int hour, int minute);
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }


    public static TimeSetDialogFragment newInstance(OnClickListener listener) {

        Bundle args = new Bundle();

        TimeSetDialogFragment fragment = new TimeSetDialogFragment();
        fragment.setOnClickListener(listener);
        fragment.setArguments(args);
        return fragment;
    }

    public TimeSetDialogFragment() {
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_time_set_dialog, null, false);

        editText_setTime = (EditText)view.findViewById(R.id.settime);
        timePicker = (TimePicker)view.findViewById(R.id.timepicker);

        test1 = view.findViewById(R.id.test1);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("시간 입력");
        builder.setView(view);
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                onClickListener.onOkClicked(h, m);
            }
        });
        builder.setNegativeButton("취소", null);
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                h = hourOfDay;
                m = minute;

            }
        });
        return builder.create();
    }
}