package de.pbma.moa.createroomdemo.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import de.pbma.moa.createroomdemo.activitys.Activity_14_RoomParticipantDetail;
import de.pbma.moa.createroomdemo.database.ParticipantItem;
import de.pbma.moa.createroomdemo.database.Repository;
import de.pbma.moa.createroomdemo.database.RoomItem;
import de.pbma.moa.createroomdemo.preferences.MySelf;


public class MQTTService extends Service {
    final static String TAG = MQTTService.class.getCanonicalName();
    // for LocalService getInstance
    final static String ACTION_START = "start"; // connect
    final static String ACTION_STOP = "stop"; // disconnect
    // for LocalService Messaging
    final static String ACTION_PRESS = "press";
    final static String TOPIC = "intent_topic";
    final public static String PROTOCOL_SECURE = "ssl";
    //    final public static String PROTOCOL_TCP = "tcp";
    final public static String URL = "pma.inftech.hs-mannheim.de";
    final public static int PORT = 8883;
    final public static String CONNECTION_URL = String.format("%s://%s:%d", PROTOCOL_SECURE, URL, PORT);
    final public static String USER = "20moagm";
    final public static String PASSWORT = "1a748f9e";

    private MqttMessaging mqttMessaging;
    private String topic = "20moagm/test";

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
        }

        @Override
        public void onDisconnect() {
            log("disconnected");
        }
    };

    public boolean registerPressListener(MyListener pressListener) {
        return listeners.addIfAbsent(pressListener);
    }

    public boolean deregisterPressListener(MyListener pressListener) {
        return listeners.remove(pressListener);
    }

    public void changeTopic(String newTopic){
        this.topic=newTopic;
    }

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
            this.topic= intent.getStringExtra(TOPIC);
            if(this.topic==null)
                Log.w(TAG, "upps, forgot toppic");
            action = ACTION_START;
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

    private void doOnRecieve(String topic, String msg) {
        for (MyListener listener : listeners) {
            listener.onRecieve(topic, msg);
        }
    }

    private String getUriFromTopic(String topic) {
        String[] ele = topic.split("/");
        return ele[1] + "/" + ele[2] + "/" + ele[3];
    }


    //Send and Receive
    final private MqttMessaging.MessageListener messageListener = new MqttMessaging.MessageListener() {
        @Override
        public void onMessage(String topic, String stringMsg) {
            Log.v(TAG, "  mqttService receives: " + stringMsg + " @ " + topic);
            if (topic.equals(MQTTService.this.topic))
                return;

            ObjectFactory objectFactory = new ObjectFactory();
            Repository repository = new Repository(MQTTService.this);


            try {
                JSONObject msg = new JSONObject(stringMsg);
                if (msg.has(JSONFactory.RAUM)) {
                    new Thread(()-> {
                        RoomItem roomItem = objectFactory.createRoomItem(msg.getString(JSONFactory.RAUM));
                        roomItem.id = repository.getIdOfRoomByUriNow(getUriFromTopic(topic));
                        repository.updateRoomItem(roomItem);
                    }).start();
                }
                if (msg.has(JSONFactory.ENTERTIME)) {
                    new Thread(() -> {
                        ParticipantItem item = objectFactory.createParticipantItem(msg.getString(JSONFactory.TEILNEHMER));
                        item.roomId = repository.getIdOfRoomByUriNow(getUriFromTopic(topic));
                        repository.addParticipantEntry(item);
                    }).start();
                }

                if (msg.has(JSONFactory.EXITTIME)) {
                    new Thread(() -> {
                        ParticipantItem item = objectFactory.createParticipantItem(msg.getString(JSONFactory.TEILNEHMER));
                        item = repository.getPaticipantItemNow(item.id, item.eMail);
                        item.exitTime = Long.parseLong(msg.getString(JSONFactory.EXITTIME));
                        repository.updateParticipantItem(item);
                    }).start();
                }

                if (msg.has(JSONFactory.TEILNEHMERLIST)) {
                    new Thread(() -> {
                        ArrayList<ParticipantItem> list = objectFactory.createParticipantItemList(msg.getString(JSONFactory.TEILNEHMERLIST));
                        long id = repository.getIdOfRoomByUriNow(getUriFromTopic(topic));
                        for (ParticipantItem item : list) {
                            item.roomId = id;
                            repository.addParticipantEntry(item);
                        }
                    }).start();
                }

                doOnRecieve(topic, stringMsg);
            } catch (JSONException e) {
                Log.e(TAG, "mqtt receiver, JSONException while receiving" + e.getMessage());
            }

        }
    };

    public boolean sendEnterRoom() { // called by activity after binding
        Log.v(TAG, "sendEnterRoom()");
        MySelf me = new MySelf(this);
        if (!me.isValide())
            return false;
        JSONFactory factory = new JSONFactory();
        msg = factory.anmelung(me).toString();
        mqttMessaging.send(topic, msg);
        return true;
    }

    public boolean sendRoom(RoomItem room) { // called by activity after binding
        Log.v(TAG, "sendRoom()");
        JSONFactory factory = new JSONFactory();
        msg = factory.raum(room).toString();
        mqttMessaging.send(topic, msg);
        return true;
    }

    public boolean sendParticipants(List<ParticipantItem> participantItems) { // called by activity after binding
        Log.v(TAG, "sendRoom()");
        JSONFactory factory = new JSONFactory();
        msg = factory.teilnehmer(participantItems).toString();
        mqttMessaging.send(topic, msg);
        return true;
    }

    public boolean sendExitRoom() { // called by activity after binding
        Log.v(TAG, "sendExitRoom()");
        MySelf me = new MySelf(this);
        if (!me.isValide())
            return false;
        JSONFactory factory = new JSONFactory();
        msg = factory.abmeldung(me).toString();
        mqttMessaging.send(topic, msg);
        return true;
    }


    //Connect and Disconnect
    private void connect() {
        Log.v(TAG, "connect");
        if (mqttMessaging != null) {
            disconnect();
            Log.w(TAG, "reconnect");
        }
        mqttMessaging = new MqttMessaging(failureListener, messageListener, connectionListener);
        Log.v(TAG, "connectionURL=" + CONNECTION_URL);
        MqttConnectOptions options = MqttMessaging.getMqttConnectOptions();
        options.setUserName(USER);
        options.setPassword(PASSWORT.toCharArray());
        Log.v(TAG, String.format("username=%s, password=%s, ", USER, PASSWORT));

        mqttMessaging.connect(CONNECTION_URL, options); // secure via URL
        // do not forget to subscribe
        mqttMessaging.subscribe(topic);
    }

    private void disconnect() {
        Log.v(TAG, "disconnect");
        if (mqttMessaging != null) {
            mqttMessaging.unsubscribe(topic);
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
