package de.pbma.moa.createroomdemo.preferences;

import android.app.ActionBar;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.Patterns;

import de.pbma.moa.createroomdemo.R;

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    final static String TAG = SettingsFragment.class.getCanonicalName();
    private String keyName, keyExtra, keyEmail, keyPhone, keyFirstName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "onCreate:");
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        Resources resources = this.getResources();
        keyName = resources.getString(R.string.key_pref_name);
        keyExtra = resources.getString(R.string.key_pref_extra);
        keyEmail = resources.getString(R.string.key_pref_email);
        keyPhone = resources.getString(R.string.key_pref_phone);
        keyFirstName = resources.getString(R.string.key_pref_vorname);

        SharedPreferences preferences =PreferenceManager.getDefaultSharedPreferences(getActivity());
        findPreference(keyName).setSummary(preferences.getString(keyName, ""));
        findPreference(keyFirstName).setSummary(preferences.getString(keyFirstName, ""));
        findPreference(keyExtra).setSummary(preferences.getString(keyExtra, ""));
        findPreference(keyEmail).setSummary(preferences.getString(keyEmail, ""));
        findPreference(keyPhone).setSummary(preferences.getString(keyPhone, ""));

    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(SettingsFragment.this);
    }

    @Override
    public void onPause() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(SettingsFragment.this);
        super.onPause();
    }

    /**
     * Hier wird gecheckt ob die Telefonnummer und Email Adresse aussehen wie eine Telefonnummer
     * und eine Email Adresse aussehen sollen.
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(keyPhone)) {
            if (!android.util.Patterns.PHONE.matcher(sharedPreferences.getString(key, "")).matches())
                sharedPreferences.edit().putString(key, "").apply();
        }
        if (key.equals(keyEmail)) {
            if (!Patterns.EMAIL_ADDRESS.matcher(sharedPreferences.getString(key, "")).matches())
                sharedPreferences.edit().putString(key, "").apply();
        }
        Preference preference = findPreference(key);
        preference.setSummary(sharedPreferences.getString(key, ""));
    }
}
