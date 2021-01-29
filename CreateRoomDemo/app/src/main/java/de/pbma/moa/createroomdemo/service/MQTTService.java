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
import java.util.Locale;
import java.util.Objects;

import de.pbma.moa.createroomdemo.AdapterJsonMqtt;
import de.pbma.moa.createroomdemo.database.ParticipantItem;
import de.pbma.moa.createroomdemo.database.Repository;
import de.pbma.moa.createroomdemo.database.RoomItem;
import de.pbma.moa.createroomdemo.preferences.MySelf;

/**
 * Die Hauptklasse für jegliche Kommunikation zwischen Teilnehmern und Hosts. Verwendet die
 * {@link MqttMessaging} Klasse.
 */
public class MQTTService extends Service {
    final static String TAG = MQTTService.class.getCanonicalName();
    // for LocalService getInstance
    final public static String ACTION_START = "start"; // connect
    final public static String ACTION_STOP = "stop"; // disconnect
    // for LocalService Messaging
    final public static String ACTION_PRESS = "press";
    final public static String PROTOCOL_SECURE = "ssl";
    //    final public static String PROTOCOL_TCP = "tcp";
    final public static String URL = "pma.inftech.hs-mannheim.de";
    final public static int PORT = 8883;
    final public static String CONNECTION_URL = String.format(Locale.GERMAN,
            "%s://%s:%d", PROTOCOL_SECURE, URL, PORT);
    final public static String USER = "20moagm";
    final public static String PASSWORT = "1a748f9e";

    private MqttMessaging mqttMessaging;
    private final ArrayList<String> topicList = new ArrayList<>();


    final private MqttMessaging.FailureListener failureListener =
            new MqttMessaging.FailureListener() {
        @Override
        public void onConnectionError(Throwable throwable) {
            Log.e(TAG,"ConnectionError: " + throwable.getMessage());
        }

        @Override
        public void onMessageError(Throwable throwable, String msg) {
            Log.e(TAG,"MessageError: " + throwable.getMessage());
        }

        @Override
        public void onSubscriptionError(Throwable throwable, String topic) {
            Log.e(TAG,"SubscriptionError:" + throwable.getMessage());
        }
    };

    final private MqttMessaging.ConnectionListener connectionListener = new MqttMessaging.
            ConnectionListener() {
        @Override
        public void onConnect() {
            Log.v(TAG,"connected");

        }

        @Override
        public void onDisconnect() {
            Log.v(TAG,"disconnected");
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
            //initial starten
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

                connect();
                // whatever else needs to be done on start may be done  here
                return START_STICKY;
            case ACTION_STOP:
                Log.v(TAG, "onStartCommand: stopping MQTT");

                disconnect();
                // whatever else needs to be done on stop may be done  here
                return START_NOT_STICKY;
            default:
                Log.w(TAG, "onStartCommand: unkown action=" + action);
                return START_NOT_STICKY;
        }
    }




    private String getRoomTagFromTopic(String topic) {
        String[] ele = topic.split("/");
        return ele[1] + "/" + ele[2] + "/" + ele[3];
    }

    private String getTopic(String RoomTag, boolean isPublic) {
        final String PUBLIC_TOPIC = "public";
        if (isPublic)
            return USER + "/" + RoomTag + "/" + PUBLIC_TOPIC;
        return USER + "/" + RoomTag;
    }

    private void addTopic(String topic) {
        if (this.topicList.contains(topic))
            return;
        this.topicList.add(topic);
        mqttMessaging.subscribe(topic);
    }

    private void removeTopic(String topic) {
        this.topicList.remove(topic);
        mqttMessaging.unsubscribe(topic);
    }


    //Send and Receive
    final private MqttMessaging.MessageListener messageListener = (topic, stringMsg) -> {
        Repository repository = new Repository(MQTTService.this);
        Log.v(TAG, "  mqttService receives: " + stringMsg + " @ " + topic);
        JSONObject msg;

        try {
            msg = new JSONObject(stringMsg);
        } catch (JSONException e) {
            Log.e(TAG, "mqtt receiver, JSONException while receiving" + e.getMessage());
            return;
        }

//             Empfangen von Raum infos
            if (msg.has(AdapterJsonMqtt.RAUM)) {
                Log.v(TAG,"Raum");
                RoomItem roomItem;
                try {
                    roomItem = AdapterJsonMqtt.createRoomItem(msg.getJSONObject(AdapterJsonMqtt.RAUM));
                } catch (JSONException e) {
                    e.printStackTrace();
                    return;
                }
                roomItem.id = repository.getIdOfRoomByRoomTagNow(getRoomTagFromTopic(topic));
                repository.updateRoomItem(roomItem);
            }
            //empfangen eines Teilnehmers welcher den raum betritt
            if (msg.has(AdapterJsonMqtt.ENTERTIME)) {
                Log.v(TAG,"Entertime");
                ParticipantItem item;
                try {
                    item = AdapterJsonMqtt.createParticipantItem(msg.getJSONObject(AdapterJsonMqtt.
                            TEILNEHMER));
                    item.enterTime = msg.getLong(AdapterJsonMqtt.ENTERTIME);
                } catch (JSONException e) {
                    e.printStackTrace();
                    return;
                }
                item.roomId = repository.getIdOfRoomByRoomTagNow(getRoomTagFromTopic(topic));
                repository.addParticipantEntryNow(item);
                //send infos to participants
                RoomItem roomItem = repository.getRoomItemByIdNow(item.roomId);
                sendRoom(roomItem, false);
                sendParticipants(repository.getParticipantsOfRoomNow(item.roomId), roomItem);
            }
            //empfangen eines Teilnehmers welcher den raum verlässt
            if (msg.has(AdapterJsonMqtt.EXITTIME)) {
                Log.v(TAG,"Exittime");
                ParticipantItem item;
                try {
                    item = AdapterJsonMqtt.createParticipantItem(msg.getJSONObject(AdapterJsonMqtt
                            .TEILNEHMER));
                    item.roomId = repository.getIdOfRoomByRoomTagNow(getRoomTagFromTopic(topic));
                    item = repository.getPaticipantItemNow(item.roomId, item.eMail);
                    item.exitTime = Long.parseLong(msg.getString(AdapterJsonMqtt.EXITTIME));
                } catch (JSONException e) {
                    e.printStackTrace();
                    return;
                }
                repository.updateParticipantItem(item);
            }
            //empfangen einer liste mit allen teilnehmern aus einem raum
            if (msg.has(AdapterJsonMqtt.TEILNEHMERLIST)) {
                Log.v(TAG,"Teilnehmerliste");
                ArrayList<ParticipantItem> list = null;
                try {
                    list = AdapterJsonMqtt.createParticipantItemList(msg
                            .getJSONArray(AdapterJsonMqtt.TEILNEHMERLIST));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                long id = repository.getIdOfRoomByRoomTagNow(getRoomTagFromTopic(topic));
                int count = repository.getCountOfExistingParticipantsInRoomNow(id);
                for (ParticipantItem item : Objects.requireNonNull(list).subList(count, list.size())) {
                    item.roomId = id;
                    repository.addParticipantEntry(item);
                }
            }
    };

    /**
     * Hinzufügen eines Raumes auf dessen MQTT nachrichten gehört werden soll
     * nur für den Host
     *
     * @param item RoomItem
     */
    public void addRoomToListen(RoomItem item, boolean imGuest) {
        String topic = getTopic(item.getRoomTag(), imGuest);
        this.addTopic(topic);
    }


    /**
     * called after binding
     * senden der eigenen Daten sowie dem anmeldezeitpunk an den Host
     *
     * @param me  meine eignen Daten instanz von MySelf
     * @param uri die erhaltene RaumUri
     */
    public void sendEnterRoom(MySelf me, String uri) {
        Log.v(TAG, "sendEnterRoom()");
        String msg = AdapterJsonMqtt.getAnmeldungJSON(me, System.currentTimeMillis()).toString();
        addTopic(getTopic(uri, true));
        try {
            mqttMessaging.send(getTopic(uri, false), msg);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    /**
     * called after binding
     * senden der eigenen Daten sowie dem abmeldezeitpunkt an den Host
     *
     * @param me  meine eignen Daten instanz von MySelf
     * @param uri die erhaltene RaumUri
     */
    public void sendExitFromRoom(MySelf me, String uri) { // called by activity after binding
        Log.v(TAG, "sendExitRoom()");
        String msg = AdapterJsonMqtt.getAbmeldungJSON(me, System.currentTimeMillis()).toString();
        try {
            mqttMessaging.send(getTopic(uri, false), msg);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return;
        }
        removeTopic(getTopic(uri, true));
    }

    /**
     * called after binding
     * senden der Rauminfos an alle teilnehmer
     *
     * @param room eine Instanz von RaumInfo
     */
    public void sendRoom(RoomItem room, boolean closeRoom) { // called by activity after binding
        Log.v(TAG, "sendRoom()");
        String msg = AdapterJsonMqtt.getRauminfoJSON(room).toString();
        try {
            mqttMessaging.send(getTopic(room.getRoomTag(), true), msg);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return;
        }
        if (closeRoom)
            removeTopic(getTopic(room.getRoomTag(), false));
    }

    /**
     * called after binding
     * senden der teilnehmer an alle teilnehmer
     *
     * @param participantItems liste aller teilnehmer in einem raum
     * @param room             eine Instanz von RaumInfo
     */
    public void sendParticipants(List<ParticipantItem> participantItems, RoomItem room) { // called by activity after binding
        Log.v(TAG, "sendRoom()");
        String msg = AdapterJsonMqtt.getTeilnehmerListJSON(participantItems).toString();
        try {
            mqttMessaging.send(getTopic(room.getRoomTag(), true), msg);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
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

        Log.v(TAG, "connected");
    }

    private void disconnect() {
        try {
            Log.v(TAG, "disconnect");
            if (mqttMessaging != null) {
                for (String topic : this.topicList)
                    mqttMessaging.unsubscribe(topic);
                List<MqttMessaging.Pair<String, String>> pending = mqttMessaging.disconnect();
                if (!pending.isEmpty()) {
                    Log.w(TAG, "pending messages: " + pending.size());
                }
            }
            mqttMessaging = null;
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    //BINDER

    final private IBinder localBinder = new LocalBinder();

    public class LocalBinder extends Binder {

        public MQTTService getMQTTService() {
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
