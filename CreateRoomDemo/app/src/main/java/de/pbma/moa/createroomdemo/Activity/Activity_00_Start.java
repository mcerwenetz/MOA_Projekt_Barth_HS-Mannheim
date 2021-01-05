package de.pbma.moa.createroomdemo.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import de.pbma.moa.createroomdemo.Preferences.MySelf;
import de.pbma.moa.createroomdemo.Preferences.PreferenceActivity;
import de.pbma.moa.createroomdemo.R;

public class Activity_00_Start extends AppCompatActivity {
    final static String TAG = Activity_00_Start.class.getCanonicalName();
    private Button btnHost, btnParticipant;

    //Todo: Service für TimeOutRunoutCapture

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.v(TAG, "OnCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_00_start);

        btnHost = findViewById(R.id.btn_start_host);
        btnParticipant = findViewById(R.id.btn_start_participant);

        btnHost.setOnClickListener(Activity_00_Start.this::iAmHost);
        btnParticipant.setOnClickListener(Activity_00_Start.this::iAmParticipant);
    }

    //Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.start, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.menu_start_pref:
                Log.v(TAG, "onOptionsItemSelected() Settings");
                intent = new Intent(Activity_00_Start.this, PreferenceActivity.class);
                startActivity(intent);
                return true;
            case R.id.menu_start_history:
                Log.v(TAG, "onOptionsItemSelected() History");
                intent = new Intent(Activity_00_Start.this, Activity_10_RoomListVisited.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void iAmHost(View view) {
        Log.v(TAG, "iAmHost() clicked");
        Intent intent = new Intent(Activity_00_Start.this, Activity_20_RoomListHost.class);
        startActivity(intent);
    }

    private void iAmParticipant(View view) {
        Log.v(TAG, "iAmParticipant() clicked");
        MySelf me = new MySelf(Activity_00_Start.this);
        if(!me.isValide()){
            Toast.makeText(this, "Gastgeberdaten sind nicht vollständig", Toast.LENGTH_LONG).show();
            Log.v(TAG, "prefs not valide");
            Intent intent = new Intent(Activity_00_Start.this, PreferenceActivity.class );
            startActivity(intent);
            return;
        }
        Log.v(TAG, "iAmHost() clicked");
        Intent intent = new Intent(Activity_00_Start.this, Activity_11_EnterViaQrNfc.class);
        startActivity(intent);
    }

}
