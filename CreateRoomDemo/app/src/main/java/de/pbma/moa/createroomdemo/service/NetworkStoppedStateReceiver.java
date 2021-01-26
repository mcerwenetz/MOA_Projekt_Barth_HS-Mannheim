package de.pbma.moa.createroomdemo.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import de.pbma.moa.createroomdemo.activitys.Activity_000_NetworkError;

/**
 * Dieser Broadcastreceiver started die NetworkError Activity, wenn die Internetconnection beendet wird<br>
 * Der allgemeine Ablauf ist so:<br>
 * <ol>
 *     <li>Initialer check ob Internetconnection steht in der StartActivity.</li>
 *     <li>Register StateStoppedReceiver im LiveCycleService<br>
 *         Fällt das Internet aus dann stoppt dieser BroadcastReceiver hier den MQTT Service
 *         und started die NetworkError Activity.
 *     </li>
 *     <li>Register StateStartReceiver in der NetworkError Activity.<br>
 * </ol>
 */


public class NetworkStoppedStateReceiver extends BroadcastReceiver {
    private final String TAG = NetworkStoppedStateReceiver.class.getCanonicalName();

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null)
            errorIntents(context);
        else if (networkInfo.getState() != NetworkInfo.State.CONNECTED)
            errorIntents(context);


    }

    private void errorIntents(Context context) {
        Log.v(TAG, "Internet disconnected");
        Intent stopIntent = new Intent(context, MQTTService.class);
        context.stopService(stopIntent);

        stopIntent = new Intent(context, RoomLivecycleService.class);
        context.stopService(stopIntent);

        Intent startInternetLost = new Intent(context, Activity_000_NetworkError.class);
        startInternetLost.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(startInternetLost);
    }
}

