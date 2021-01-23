package de.pbma.moa.createroomdemo.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import de.pbma.moa.createroomdemo.activitys.Activity_000_NetworkError;

public class NetworkStoppedStateReceiver extends BroadcastReceiver {
    private final String TAG = NetworkStoppedStateReceiver.class.getCanonicalName();

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null || networkInfo.getState() != NetworkInfo.State.CONNECTED) {
            Log.v(TAG, "Internet disconnected");
            Intent stopIntent = new Intent(context, MQTTService.class);
            intent.setAction(MQTTService.ACTION_STOP);
            context.startService(stopIntent);

            Intent startInternetLost = new Intent(context, Activity_000_NetworkError.class);
            startInternetLost.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            context.startActivity(startInternetLost);


        }
    }
}
