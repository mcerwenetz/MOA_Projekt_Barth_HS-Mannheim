package de.pbma.moa.createroomdemo.service;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class NetworkStartedStateReceiver extends BroadcastReceiver {
    private final String TAG = NetworkStartedStateReceiver.class.getCanonicalName();

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null || networkInfo.getState() == NetworkInfo.State.CONNECTED) {
            Log.v(TAG, "Internet reconnected");
            Intent startIntent = new Intent(context, MQTTService.class);
            intent.setAction(MQTTService.ACTION_START);
            context.startService(startIntent);

            Activity activity = (Activity) context;
            activity.finish();
        }
    }
}
