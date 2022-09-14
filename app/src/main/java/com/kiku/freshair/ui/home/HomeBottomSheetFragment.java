package com.kiku.freshair.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.kiku.freshair.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

public class HomeBottomSheetFragment extends BottomSheetDialogFragment {
    Context context;
    TextView text1, text2, text3;
    private View view;

    private TextView mConnectionStatus;

    private static final String TAG = "TcpClient";
    private boolean isConnected = false;

    private String mServerIP = null;
    private Socket mSocket = null;
    private PrintWriter mOut;
    private BufferedReader mIn;
    private Thread mReceiverThread = null;

    public HomeBottomSheetFragment(Context context){
        this.context = context;
    }

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);

    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, 0);


    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.bottom_window, container, false);

        return view;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();

        isConnected = false;
    }





}
