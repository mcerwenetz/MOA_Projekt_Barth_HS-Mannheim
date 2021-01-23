package de.pbma.moa.createroomdemo.service;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import de.pbma.moa.createroomdemo.activitys.Activity_000_Welcome;

public class NetworkStateReceiver extends BroadcastReceiver {
    private final String TAG = NetworkStateReceiver.class.getCanonicalName();

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo == null || networkInfo.getState() != NetworkInfo.State.CONNECTED){
            Log.v(TAG,"Internet disconnected");
            Intent stopIntent = new Intent(context, MQTTService.class);
            intent.setAction(MQTTService.ACTION_STOP);
            context.startService(stopIntent);
            Intent startIntenetLost = new Intent(context, Activity_000_Welcome.class);
            context.startActivity(startIntenetLost);
        }
        if(!mqttServiceRunning(context)){
            Intent startIntent = new Intent(context, MQTTService.class);
            intent.setAction(MQTTService.ACTION_START);
            context.startService(startIntent);
        }


    }

    private boolean mqttServiceRunning(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)){
            if(service.service.getClassName().equals(MqttMessaging.class.getName())){
                return true;
            }
        }
        return false;
    }

}
