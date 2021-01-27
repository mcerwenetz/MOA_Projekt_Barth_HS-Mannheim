package de.pbma.moa.createroomdemo.preferences;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import de.pbma.moa.createroomdemo.R;

public class PreferenceActivity extends AppCompatActivity {
    final static String TAG = PreferenceActivity.class.getCanonicalName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.v(TAG,"onCreate");
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content,new SettingsFragment()).commit();
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_01_preferences, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        finish();
        return true;
    }
}
