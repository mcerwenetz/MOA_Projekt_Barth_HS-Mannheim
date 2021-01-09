package de.pbma.moa.createroomdemo.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public class MQTTService extends Service {
    final static String TAG = MQTTService.class.getCanonicalName();
    // for LocalService getInstance
    final static String ACTION_START = "start"; // connect
    final static String ACTION_STOP = "stop"; // disconnect
    // for LocalService Messaging
    final static String ACTION_PRESS = "press";
    final static String ACTION_LOG = "log";
    // MessageKeys
    final static String MSGKEY_ID = "id";
    final static String MSGKEY_TEXT = "text";

    final public static String DEVICENAME = "Rapha";
    final public static String TOPIC = "20moagm/test";
    final public static String PROTOCOL_SECURE = "ssl";
    final public static String PROTOCOL_TCP = "tcp";
    final public static String URL = "pma.inftech.hs-mannheim.de";
    final public static int PORT = 8883;
    final public static String CONNECTION_URL = String.format("%s://%s:%d", PROTOCOL_SECURE, URL, PORT);
    final public static String USER = "20moagm";
    final public static String PASSWORT = "1a748f9e";

    private MqttMessaging mqttMessaging;

    private String x = "";

    final private CopyOnWriteArrayList<MyListener> listeners = new CopyOnWriteArrayList<>();

    final private MqttMessaging.FailureListener failureListener = new MqttMessaging.FailureListener() {
        @Override
        public void onConnectionError(Throwable throwable) {
            log("ConnectionError: " + throwable.getMessage());
        }

        @Override
        public void onMessageError(Throwable throwable, String msg) {
            log("MessageError: " + throwable.getMessage());
        }

        @Override
        public void onSubscriptionError(Throwable throwable, String topic) {
            log("SubscriptionError:" + throwable.getMessage());
        }
    };

    final private MqttMessaging.ConnectionListener connectionListener = new MqttMessaging.ConnectionListener() {
        @Override
        public void onConnect() {
            log("connected");
            doMqttStatus(true); // on purpose a little weird, typical to have interface translation
        }

        @Override
        public void onDisconnect() {
            log("disconnected");
            doMqttStatus(false);
        }
    };


    @Override
    public void onCreate() {
        Log.v(TAG, "onCreate");
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        Log.v(TAG, "onDestroy");
        disconnect();
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v(TAG, "onStartCommand");
        String action;
        if (intent != null) {
            action = intent.getAction();
        } else {
            Log.w(TAG, "upps, restart");
            action = ACTION_START;
        }
        if (action == null) {
            Log.w(TAG, "  action=null, nothing further to do");
            return START_STICKY;
        }
        switch (action) {
            case ACTION_START:
                Log.v(TAG, "onStartCommand: starting MQTT");
                log("starting");
                connect();
                // whatever else needs to be done on start may be done  here
                return START_STICKY;
            case ACTION_STOP:
                Log.v(TAG, "onStartCommand: stopping MQTT");
                log("stopping");
                disconnect();
                // whatever else needs to be done on stop may be done  here
                return START_NOT_STICKY;
            default:
                Log.w(TAG, "onStartCommand: unkown action=" + action);
                return START_NOT_STICKY;
        }
    }



    private void log(String msg) {
        Log.v(TAG, "remoteLog: " + msg);
        for (MyListener listener : listeners) {
            listener.log(msg);
        }
    }

    private void doMqttStatus(boolean connected) {
        for (MyListener listener : listeners) {
            listener.onMQTTStatus(connected);
        }
    }

    private void doOnRecieve(String topic,String msg ) {
        for (MyListener listener : listeners) {
            listener.onRecieve(topic,msg);
        }
    }




    public boolean registerPressListener(MyListener pressListener) {
        return listeners.addIfAbsent(pressListener);
    }

    public boolean deregisterPressListener(MyListener pressListener) {
        return listeners.remove(pressListener);
    }

    //Send and Receive
    final private MqttMessaging.MessageListener messageListener = new MqttMessaging.MessageListener() {
        @Override
        public void onMessage(String topic, String stringMsg) {
            Log.v(TAG, "  mqttService receives: " + stringMsg);
            if(x.equals(stringMsg))
                return;
//            try {
//                JSONObject msg = new JSONObject(stringMsg);
//                if (!msg.has(MSGKEY_ID)) {
//                    Log.e(TAG, "MqttMessaging::MessageListener: no " + MSGKEY_ID + ", ignoring");
//                    return;
//                }
//                String id = msg.getString(MSGKEY_ID);
//                String text = msg.getString(MSGKEY_TEXT);
//                log("received: " + MSGKEY_ID + "=" + id + ", " + MSGKEY_TEXT + "=" + text);
            // here you would typically call a setSomething of a model
            // or store content in a content provider or whatever
            // we delegate to listener
            doOnRecieve(topic,stringMsg);
//            } catch (JSONException e) {
//                Log.e(TAG, "mqtt receiver, JSONException while receiving" + e.getMessage());
//            }

        }
    };

    public void send(String msg) { // called by activity after binding
        x = msg;
        Log.v(TAG, "Send messgae: "+msg);
//        try {
//            JSONObject msg = new JSONObject(); // wrap in JSON
//            // msg.put(MSGKEY_ID, deviceName);
//            msg.put(MSGKEY_ID, deviceName);
//            msg.put(MSGKEY_TEXT, greetings[msgIdx]);
//            msgIdx = (msgIdx+1) % greetings.length;
//            Log.v(TAG, "send: " + msg.toString());
//            mqttMessaging.send(pressTopic, msg.toString());
        mqttMessaging.send(TOPIC, msg);
//        } catch (JSONException e) {
//            String message = e.getMessage();
//            if (message == null) {
//                message = "message is null?";
//            }
//            Log.e(TAG, message);
//        }
    }

    //Connect and Disconnect
    private void connect() {
        Log.v(TAG, "connect");
        if (mqttMessaging != null) {
            disconnect();
            Log.w(TAG, "reconnect");
        }
        mqttMessaging = new MqttMessaging(failureListener, messageListener, connectionListener);
        Log.v(TAG, "connectionURL=" + CONNECTION_URL );
        MqttConnectOptions options = MqttMessaging.getMqttConnectOptions();
        options.setUserName(USER);
        options.setPassword(PASSWORT.toCharArray());
        Log.v(TAG, String.format("username=%s, password=%s, ", USER, PASSWORT));

        mqttMessaging.connect(CONNECTION_URL, options); // secure via URL
        // do not forget to subscribe
        mqttMessaging.subscribe(TOPIC);
    }

    private void disconnect() {
        Log.v(TAG, "disconnect");
        if (mqttMessaging != null) {
            mqttMessaging.unsubscribe(TOPIC);
            List<MqttMessaging.Pair<String, String>> pending = mqttMessaging.disconnect();
            if (!pending.isEmpty()) {
                Log.w(TAG, "pending messages: " + pending.size());
            }
        }
        mqttMessaging = null;
    }

    //BINDER

    final private IBinder localBinder = new LocalBinder();

    public class LocalBinder extends Binder {

        MQTTService getMQTTService() {
            return MQTTService.this;
        }

    }
    @Override
    public IBinder onBind(Intent intent) {
        Log.v(TAG, "onBind");
        String action = intent.getAction();
        if (action != null && action.equals(MQTTService.ACTION_PRESS)) {
            Log.v(TAG, "onBind for Press");
            return localBinder;
            // } else if (action.equals(MQTTService.ACTION_LOG)) {
            //    Log.v(TAG, "onBind for Log");
            //    return messenger.getBinder();
            // we do not provide messaging in this small example
            // you might want to
        } else {
            Log.e(TAG, "onBind only defined for ACTION_PRESS"); // or ACTION_LOG ");
            Log.e(TAG, "       did you want to call startService? ");
            return null;
        }
    }
}
