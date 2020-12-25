package de.pbma.moa.createroomdemo.Preferences;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.util.Log;

import de.pbma.moa.createroomdemo.R;

public class SettingsFragment extends PreferenceFragment {
    final static String TAG = SettingsFragment.class.getCanonicalName();

    @Override
    public void onCreate(Bundle savedInstanceState){
        Log.v(TAG,"onCreate:");
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
