package de.pbma.moa.createroomdemo.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


public class StartActivity extends AppCompatActivity {
    final static String TAG = StartActivity.class.getCanonicalName();

    private TextView tv;
    private Button btn;
    private EditText et;

    private boolean mqttServiceBound;
    private MQTTService mqttService;

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.v(TAG, "onServiceConnected");
            mqttService = ((MQTTService.LocalBinder) service).getMQTTService();
            mqttService.registerPressListener(myListener);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // unintentionally disconnected
            Log.v(TAG, "onServiceDisconnected");
            unbindMQTTService(); // cleanup
        }
    };

    private final Handler handler = new Handler();
    private final MyListener myListener = new MyListener() {
        @Override
        public void onRecieve(final String topic, final String msg) {
            handler.post(() -> {
                tv.setText(tv.getText()+"\n"+topic + " " + msg);
            });
        }

        @Override
        public void onMQTTStatus(final boolean connected) {
            handler.post(() -> {
                if (connected) {
                    Log.v("MQTTService", "connected");
                } else {
                    Log.v("MQTTService", "disconnected");
                }
            });
        }

        @Override
        public void log(final String message) {
            handler.post(() -> Log.v("MQTTService", message));
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        mqttServiceBound = false;
        setContentView(R.layout.page_start);
        tv = findViewById(R.id.tv_page_start);
        et = findViewById(R.id.et_page_start);
        btn = findViewById(R.id.btn_page_start);

        btn.setOnClickListener(this::btnSendClicked);
        onStartService();
    }

    @Override
    protected void onStart() {
        Log.v(TAG, "onStart");
        super.onStart();
    }

    @Override
    protected void onResume() {
        Log.v(TAG, "onResume");
        super.onResume();
        bindMQTTService();
    }

    @Override
    protected void onPause() {
        Log.v(TAG, "onPause");
        super.onPause();
        unbindMQTTService();
    }

    @Override
    protected void onStop() {
        Log.v(TAG, "onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.v(TAG, "onDestroy");
        onStopService();
        super.onDestroy();
    }

    public void onStartService() {
        Log.v(TAG, "onStartService");
        Intent intent = new Intent(this, MQTTService.class);
        intent.setAction(MQTTService.ACTION_START);
        startService(intent);
    }

    public void onStopService() {
        Log.v(TAG, "onStopService");
        Intent intent = new Intent(this, MQTTService.class);
        intent.setAction(MQTTService.ACTION_STOP);
        startService(intent); // to stop
    }

    public void btnSendClicked(View view) {
        Log.v(TAG, "onPress");
        if (!mqttServiceBound) {
            Log.e(TAG, "ignore press request, if not tried to bind");
            return;
        }
        if (mqttService == null) {
            Log.w(TAG, "tried to bind, but not yet successful, save for later");
            return;
        }
        // is bound, do it
        mqttService.send(et.getText().toString());
        et.setText("");
    }


    private void bindMQTTService() {
        Log.v(TAG, "bindMQTTService");
        Intent intent = new Intent(this, MQTTService.class);
        intent.setAction(MQTTService.ACTION_PRESS);
        mqttServiceBound = bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        if (!mqttServiceBound) {
            Log.w(TAG, "could not try to bind service, will not be bound");
        }
    }

    private void unbindMQTTService() {
        Log.v(TAG, "unbindMQTTService");
        if (mqttServiceBound) {
            if (mqttService != null) {
                // deregister listeners, if there are any
            }
            mqttServiceBound = false;
            unbindService(serviceConnection);
        }
    }

}
