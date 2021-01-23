package de.pbma.moa.createroomdemo.service;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * Dieser Broadcastreceiver beendet die NetworkError Activity, wenn die Internetconnection wieder
 * steht.
 * Der Ablauf ist so:
 * 1.Initialer check ob Internetconnection steht in der StartActivity.
 * 2.Register StateStoppedReceiver im LiveCycleService
 * Wenn jetzt das Internet ausfällt started der LivecycleService die NetworkError Activity.
 * MQTT Service wird gestopppt.
 * 3.Register StateStartReceiver in der NetworkError Activity.
 * Wenn jetzt das Internet wieder startet löscht dieser Receiver hier die TopActivity
 * (NetworkError Activity) vom Stack und die letzte Activity ist wieder zu sehen.
 * Falls der MQTTService nicht läuft wird er wieder angestoßen.
 */


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
