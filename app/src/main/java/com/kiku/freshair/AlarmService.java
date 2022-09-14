package com.kiku.freshair;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class AlarmService extends Service {
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        Toast.makeText(this,"알람",Toast.LENGTH_SHORT).show();
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
