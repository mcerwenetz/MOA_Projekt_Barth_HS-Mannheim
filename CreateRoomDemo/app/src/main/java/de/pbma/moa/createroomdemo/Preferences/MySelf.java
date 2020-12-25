package de.pbma.moa.createroomdemo.Preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.util.Log;

import de.pbma.moa.createroomdemo.R;

public class MySelf {
    final static String TAG = MySelf.class.getCanonicalName();
    private Context context;
    private SharedPreferences preferences;
    Resources resources;

    final String keyFirstName = resources.getString(R.string.key_pref_vorname);
    final String keyName = resources.getString(R.string.key_pref_name);
    final String keyExtra = resources.getString(R.string.key_pref_extra);
    final String keyEmail = resources.getString(R.string.key_pref_email);
    final String keyPhone = resources.getString(R.string.key_pref_phone);

    public MySelf(Context context) {
        Log.v(TAG,"create MySelf");
        this.context = context;
        resources = context.getResources();
        PreferenceManager.setDefaultValues(context, R.xml.preferences, false);
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
//        preferences.registerOnSharedPreferenceChangeListener(ospcl);
    }

    public String getFirstName(){
        return preferences.getString(keyFirstName,null);
    }
    public String getName(){
        return preferences.getString(keyName,null);
    }
    public String getExtra(){
        return preferences.getString(keyExtra,null);
    }
    public String getEmail(){
        return preferences.getString(keyEmail,null);
    }
    public String getPhone(){
        return preferences.getString(keyPhone,null);
    }
    public boolean isValide(){
        if(preferences.getString(keyFirstName,null).equals(""))
            return false;
        if(preferences.getString(keyName,null).equals(""))
            return false;
        if(preferences.getString(keyEmail,null).equals("") && preferences.getString(keyPhone,null).equals(""))
            return false;
        return true;
    }
//    SharedPreferences.OnSharedPreferenceChangeListener ospcl = new SharedPreferences.OnSharedPreferenceChangeListener() {
//        @Override
//        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
//            if(key.equals(keyFirstName)){
//            }
//            if(key.equals(keyName)){
//            }
//            if(key.equals(keyExtra)){
//            }
//            if(key.equals(keyEmail)){
//            }
//            if(key.equals(keyPhone)){
//            }
//        }
//    };



}
