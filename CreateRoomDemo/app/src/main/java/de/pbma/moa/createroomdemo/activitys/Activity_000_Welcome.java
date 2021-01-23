package de.pbma.moa.createroomdemo.activitys;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import de.pbma.moa.createroomdemo.R;

public class Activity_000_Welcome extends AppCompatActivity {
    final static String TAG = Activity_000_Welcome.class.getCanonicalName();
    private TextView tvConnection;
    private boolean connected = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.v(TAG, "Welcome_OnCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_000_welcome_start);
        tvConnection = findViewById(R.id.tv_000_welcome_connect);
        connectionInfo();
    }

    private boolean checkIfConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            return this.connected = true;
        } else
            return this.connected = false;
    }

    private void connectionInfo() {
        if (checkIfConnected() == true) {
            Intent intent = new Intent(Activity_000_Welcome.this, Activity_00_Start.class);
            startActivity(intent);
            Log.v(TAG, "Check connected true");
            finish();
        } else {
            tvConnection.setText("Es besteht keine Internetverbindung!");
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.v(TAG, "Welcome_onResume");
        connectionInfo();
    }
}


