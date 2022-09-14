package com.kiku.freshair;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.preference.PreferenceManager;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class WindowActivity extends AppCompatActivity {


    private TextView mConnectionStatus;
    private EditText mInputEditText;
    private ArrayAdapter<String> mConversationArrayAdapter;
    private ListView mMessageListview;
    private Button openbtn, closebtn;
    private Switch modeSwitch;
    private TextView temp, humi;
    private TextView tAlarmTime;
    private ImageView windowstate;
    private ImageButton refresh;

    private String window = "닫힘";
    private boolean isOpen = false;

    private static final String TAG = "TcpClient";
    private boolean isConnected = false;

    private String mServerIP = null;
    private Socket mSocket = null;
    private PrintWriter mOut;
    private BufferedReader mIn;

    private Thread mReceiverThread = null;

    private String windowTime;

    private AlarmManager alarmManager;
    private PendingIntent alarmIntent;


    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private String alarmTime;
    private Boolean isChecked;
    public void initializeValue(){
        isChecked = preferences.getBoolean("isChecked", false);
        alarmTime = preferences.getString("alarmTime","알람을 설정해주세요");
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.windowmain, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_refresh:
                setUp();
                Toast.makeText(this,"setup",Toast.LENGTH_SHORT).show();
                break;
        }


        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_window);

        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = preferences.edit();
        initializeValue();

        //alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        refresh = (ImageButton)findViewById(R.id.refresh);
        refresh.setImageResource(R.drawable.ic_baseline_refresh_24);
        mConnectionStatus = (TextView)findViewById(R.id.connection_status_textview);
        mInputEditText = (EditText)findViewById(R.id.input_string_edittext);
        mMessageListview = (ListView) findViewById(R.id.message_listview);
        openbtn = (Button) findViewById(R.id.open);
        closebtn = (Button) findViewById(R.id.close);
        modeSwitch = (Switch) findViewById(R.id.modeswitch);
        windowstate = (ImageView) findViewById(R.id.windowstate);
        temp = (TextView)findViewById(R.id.temp);
        humi = (TextView)findViewById(R.id.humi);
        tAlarmTime = (TextView)findViewById(R.id.alarmtime);
        if(savedInstanceState!=null){
            window = savedInstanceState.getString("window");
        }

        if(alarmTime!=null){
            tAlarmTime.setText(alarmTime);
            modeSwitch.setChecked(isChecked);
        }

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUp();
            }
        });
        modeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    TimeSetDialogFragment.newInstance(new TimeSetDialogFragment.OnClickListener() {
                        @Override
                        public void onOkClicked(int hour, int minute) {
                            SimpleDateFormat now = new SimpleDateFormat("HHmm");
                            Date date = new Date();

                            if(hour*100+minute<=Integer.parseInt(now.format(date))){
                                Toast.makeText(getApplicationContext(),"다시 설정해주세요",Toast.LENGTH_SHORT).show();
                                tAlarmTime.setText("예약이 취소되었습니다.");
                                modeSwitch.setChecked(false);
                            }else {
                                windowTime = hour + "시" + minute + "분"+ "에 환기할게요";
                                tAlarmTime.setText(windowTime);
                                editor.remove("alarmTime");
                                editor.putString("alarmTime",windowTime);
                                editor.remove("isChecked");
                                editor.putBoolean("isChecked",true);
                                editor.apply();
                                show();
                                Calendar calendar1 = Calendar.getInstance();
                                calendar1.set(Calendar.HOUR_OF_DAY, hour);
                                calendar1.set(Calendar.MINUTE, minute);
                                calendar1.set(Calendar.SECOND, 0);
                                Calendar calendar2 = Calendar.getInstance();
                                calendar2.set(Calendar.HOUR_OF_DAY, hour);
                                calendar2.set(Calendar.MINUTE, minute+1);
                                calendar2.set(Calendar.SECOND, 0);
                                //안되면 getcontext

                                AlarmManager alarmManager=(AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                                Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);

                                PendingIntent alarmIntent = PendingIntent.getBroadcast(getApplicationContext(), 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                                //PendingIntent alarmIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
                                //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar1.getTimeInMillis(), AlarmManager.INTERVAL_DAY, alarmIntent);
                                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar1.getTimeInMillis(), alarmIntent);
                                Toast.makeText(getApplicationContext(), "알람이 저장되었습니다.", Toast.LENGTH_LONG).show();

                            }

                        }
                    }).show(getSupportFragmentManager(),"dialog");
                }else{
                    tAlarmTime.setText("예약이 취소되었습니다.");
                    editor.remove("alarmTime");
                    editor.putString("alarmTime","알람을 설정해주세요");
                    editor.remove("isChecked");
                    editor.putBoolean("isChecked",false);
                    editor.apply();
                    //alarmManager.cancel(alarmIntent);

                }
            }
        });

        Button sendButton = (Button)findViewById(R.id.send_button);
        sendButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){

                String sendMessage = mInputEditText.getText().toString();
                if ( sendMessage.length() > 0 ) {

                    if (!isConnected) showErrorDialog("서버로 접속된후 다시 해보세요.");
                    else {
                        new Thread(new SenderThread(sendMessage)).start();
                        mInputEditText.setText(" ");
                    }
                }
            }
        });
        openbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isConnected) showErrorDialog("서버로 접속된후 다시 해보세요.");
                else {
                    new Thread(new SenderThread("o")).start();
                    window = "열림";
                }
            }
        });
        closebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isConnected) showErrorDialog("서버로 접속된후 다시 해보세요.");
                else {
                    new Thread(new SenderThread("c")).start();
                    window = "닫힘";
                }
            }
        });

        setUp();

    }

    @Override
    protected void onSaveInstanceState(@NonNull @NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("window", window);
    }


    private void setUp(){
        mConversationArrayAdapter = new ArrayAdapter<>( this,
                android.R.layout.simple_list_item_1 );
        mMessageListview.setAdapter(mConversationArrayAdapter);

        new Thread(new ConnectThread("192.168.0.60", 8090)).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        isConnected = false;

    }


    private static long back_pressed;
    @Override
    public void onBackPressed(){

        if (back_pressed + 2000 > System.currentTimeMillis()){
            super.onBackPressed();

            Log.d(TAG, "onBackPressed:");
            isConnected = false;

            finish();
        }
        else{
            Toast.makeText(getBaseContext(), "한번 더 뒤로가기를 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
            back_pressed = System.currentTimeMillis();
        }

    }


    private class ConnectThread implements Runnable {

        private String serverIP;
        private int serverPort;

        ConnectThread(String ip, int port) {
            serverIP = ip;
            serverPort = port;

            //mConnectionStatus.setText("connecting to " + serverIP + ".......");
            mConnectionStatus.setText("연결중");
        }

        @Override
        public void run() {

            try {

                mSocket = new Socket(serverIP, serverPort);
                //ReceiverThread: java.net.SocketTimeoutException: Read timed out 때문에 주석처리
                //mSocket.setSoTimeout(3000);

                mServerIP = mSocket.getRemoteSocketAddress().toString();


            } catch( UnknownHostException e )
            {
                Log.d(TAG,  "ConnectThread: can't find host");
            }
            catch( SocketTimeoutException e )
            {
                Log.d(TAG, "ConnectThread: timeout");
            }
            catch (Exception e) {

                Log.e(TAG, ("ConnectThread:" + e.getMessage()));
            }


            if (mSocket != null) {

                try {

                    mOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(mSocket.getOutputStream(), "UTF-8")), true);
                    mIn = new BufferedReader(new InputStreamReader(mSocket.getInputStream(), "UTF-8"));

                    isConnected = true;
                    if(isConnected)
                        new Thread(new SenderThread("s")).start();
                } catch (IOException e) {

                    Log.e(TAG, ("ConnectThread:" + e.getMessage()));
                }
            }


            runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    if (isConnected) {

                        Log.d(TAG, "connected to " + serverIP);
                        //mConnectionStatus.setText("connected to " + serverIP);
                        mConnectionStatus.setText("연결");
                        if(isConnected)
                            new Thread(new SenderThread("s")).start();
                        mReceiverThread = new Thread(new ReceiverThread());
                        mReceiverThread.start();
                    }else{

                        Log.d(TAG, "failed to connect to server " + serverIP);
                        //mConnectionStatus.setText("failed to connect to server "  + serverIP);
                        mConnectionStatus.setText("실패");
                    }

                }
            });
        }
    }


    private class SenderThread implements Runnable {

        private String msg;

        SenderThread(String msg) {
            this.msg = msg;
        }

        @Override
        public void run() {

            mOut.println(this.msg);
            mOut.flush();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "send message: " + msg);
                    mConversationArrayAdapter.insert("Me - " + msg, 0);

                }
            });
        }
    }


    private class ReceiverThread implements Runnable {

        @Override
        public void run() {

            try {

                while (isConnected) {

                    if ( mIn ==  null ) {

                        Log.d(TAG, "ReceiverThread: mIn is null");
                        break;
                    }

                    final String recvMessage =  mIn.readLine();

                    if (recvMessage != null) {

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                //if(recvMessage!=null) windowstate.setText(recvMessage);
                                Log.d(TAG, "recv message: "+recvMessage);
                                //mConversationArrayAdapter.insert(mServerIP + " - " + recvMessage, 0);

                                if(recvMessage.contains("w")){
                                    if(recvMessage.substring(1).equals("open"))
                                        windowstate.setImageResource(R.drawable.open);
                                    else if(recvMessage.substring(1).equals("close"))
                                        windowstate.setImageResource(R.drawable.close);
                                }else if(recvMessage.contains("h")){
                                    humi.setText(recvMessage.substring(1));
                                }else if(recvMessage.contains("t")){
                                    temp.setText(recvMessage.substring(1));
                                }


                            }
                        });
                    }
                }

                Log.d(TAG, "ReceiverThread: thread has exited");
                if (mOut != null) {
                    mOut.flush();
                    mOut.close();
                }

                mIn = null;
                mOut = null;

                if (mSocket != null) {
                    try {
                        mSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            catch (IOException e) {

                Log.e(TAG, "ReceiverThread: "+ e);
            }
        }

    }


    public void showErrorDialog(String message)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error");
        builder.setCancelable(false);
        builder.setMessage(message);
        builder.setPositiveButton("OK",  new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        builder.create().show();
    }

    private void show() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default");

        // 필수 항목
        builder.setSmallIcon(R.drawable.open);
        builder.setContentTitle("환기 시작");
        builder.setContentText(windowTime);

        // 액션 정의
        Intent intent = new Intent(this, WindowActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        // 클릭 이벤트 설정
        builder.setContentIntent(pendingIntent);

        // 큰 아이콘 설정
        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.open);
        builder.setLargeIcon(largeIcon);

        // 색상 변경
        builder.setColor(Color.RED);

        // 기본 알림음 사운드 설정
        Uri ringtoneUri = RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(ringtoneUri);

        // 진동설정: 대기시간, 진동시간, 대기시간, 진동시간 ... 반복 패턴
        long[] vibrate = {0, 100, 200, 300};
        builder.setVibrate(vibrate);

        builder.setAutoCancel(true);

        // 알림 매니저
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // 오레오에서는 알림 채널을 매니저에 생성해야 한다
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(new NotificationChannel("default", "기본 채널", NotificationManager.IMPORTANCE_DEFAULT));
        }

        // 알림 통지
        manager.notify(0, builder.build());
    }

    private void hide() {
        // 알림 해제
        NotificationManagerCompat.from(this).cancel(0);
    }

}
