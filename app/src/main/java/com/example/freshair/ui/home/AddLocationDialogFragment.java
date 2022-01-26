package com.example.freshair.ui.home;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.freshair.R;


public class AddLocationDialogFragment extends DialogFragment {
    private OnClickListener onClickListener;
    private EditText editText_umdName;


    public interface OnClickListener {
        void onOkClicked(String stationName);
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }


    public static AddLocationDialogFragment newInstance(OnClickListener listener) {

        Bundle args = new Bundle();

        AddLocationDialogFragment fragment = new AddLocationDialogFragment();
        fragment.setOnClickListener(listener);
        fragment.setArguments(args);
        return fragment;
    }

    public AddLocationDialogFragment() {
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_add_location_dialog, null, false);

        editText_umdName = (EditText)view.findViewById(R.id.stationname);


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("지역 입력");
        builder.setView(view);
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String umdName = editText_umdName.getText().toString();
                onClickListener.onOkClicked(umdName);
            }
        });
        builder.setNegativeButton("취소", null);
        return builder.create();
    }


}