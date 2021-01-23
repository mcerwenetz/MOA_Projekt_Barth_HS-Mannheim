package de.pbma.moa.createroomdemo.activitys;

import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import de.pbma.moa.createroomdemo.R;
import de.pbma.moa.createroomdemo.service.NetworkStartedStateReceiver;

public class Activity_000_NetworkError extends AppCompatActivity {
    final static String TAG = Activity_000_NetworkError.class.getCanonicalName();
    private TextView tvConnection;
    private boolean connected = false;
    private final NetworkStartedStateReceiver networkStartedStateReceiver = new NetworkStartedStateReceiver();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.v(TAG, "Welcome_OnCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_000_network_disconnected);
        tvConnection = findViewById(R.id.tv_000_welcome_connect);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(networkStartedStateReceiver,intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(networkStartedStateReceiver);
    }

}


