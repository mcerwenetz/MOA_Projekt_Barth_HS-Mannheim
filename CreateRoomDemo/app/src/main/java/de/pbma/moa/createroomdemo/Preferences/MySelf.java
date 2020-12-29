package de.pbma.moa.createroomdemo.Preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.util.Log;

import de.pbma.moa.createroomdemo.R;

public class MySelf {
    final static String TAG = MySelf.class.getCanonicalName();
    private Resources resources;
    private Context context;
    private SharedPreferences preferences;
    private String keyName, keyExtra, keyEmail, keyPhone, keyFirstName;


    public MySelf(Context context) {
        Log.v(TAG, "create MySelf");
        this.context = context;
        resources = context.getResources();
        keyName = resources.getString(R.string.key_pref_name);
        keyExtra = resources.getString(R.string.key_pref_extra);
        keyEmail = resources.getString(R.string.key_pref_email);
        keyPhone = resources.getString(R.string.key_pref_phone);
        keyFirstName = resources.getString(R.string.key_pref_vorname);
        PreferenceManager.setDefaultValues(context, R.xml.preferences, false);
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public String getFirstName() {
        return preferences.getString(keyFirstName, "");
    }

    public String getName() {
        return preferences.getString(keyName, "");
    }

    public String getExtra() {
        return preferences.getString(keyExtra, "");
    }

    public String getEmail() {
        return preferences.getString(keyEmail, "");
    }

    public String getPhone() {
        return preferences.getString(keyPhone, "");
    }

    public boolean isValide() {
        Log.v(TAG,"Check myself");
        if (preferences.getString(keyFirstName, "").equals(""))
            return false;
        if (preferences.getString(keyName, "").equals(""))
            return false;
        if (preferences.getString(keyEmail, "").equals("") && preferences.getString(keyPhone, "").equals(""))
            return false;
        return true;
    }



}
