package de.pbma.moa.createroomdemo.activitys;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import de.pbma.moa.createroomdemo.R;

public class Activity_000_NetworkError extends AppCompatActivity {
    final static String TAG = Activity_000_NetworkError.class.getCanonicalName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.v(TAG, "displaying network error message");
        super.onCreate(savedInstanceState);
        this.setTitle("Störung");
        setContentView(R.layout.page_000_network_disconnected);
        TextView tv = findViewById(R.id.tv_000_error_msg);
        tv.setText("Bitte stellen Sie eine Internetverbindung her und starten Sie die App neu");
    }
}


