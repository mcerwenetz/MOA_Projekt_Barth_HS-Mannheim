package de.pbma.moa.createroomdemo.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import de.pbma.moa.createroomdemo.Preferences.PreferenceActivity;
import de.pbma.moa.createroomdemo.Preferences.SettingsFragment;
import de.pbma.moa.createroomdemo.R;
import de.pbma.moa.createroomdemo.RoomRoom.RoomItem;

public class StartActivity extends AppCompatActivity {
        final static String TAG = StartActivity.class.getCanonicalName();
        private Button btnHost,btnParticipant;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.v(TAG,"OnCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_start);

        btnHost = findViewById(R.id.btn_start_host);
        btnParticipant = findViewById(R.id.btn_start_participant);

        btnHost.setOnClickListener(StartActivity.this::iAmHost);
        btnParticipant.setOnClickListener(StartActivity.this::iAmParticipant);
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
        switch (item.getItemId()) {
            case R.id.menu_start_pref:
                Log.v(TAG,"onOptionsItemSelected() Settings");
                Intent intent = new Intent(StartActivity.this, PreferenceActivity.class );
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void iAmHost(View view){
        Log.v(TAG,"iAmHost() clicked");
        Intent intent = new Intent(StartActivity.this, RoomListActivity.class );
        startActivity(intent);
    }

    private void iAmParticipant (View view){
//        Log.v(TAG,"iAmParticipant() clicked");
//        Intent intent = new Intent(StartActivity.this, .class );
//        startActivity(intent);
    }

}
