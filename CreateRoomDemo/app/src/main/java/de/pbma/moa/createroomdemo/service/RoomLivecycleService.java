package de.pbma.moa.createroomdemo.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import de.pbma.moa.createroomdemo.database.Repository;
import de.pbma.moa.createroomdemo.database.RoomItem;

/**
 * Verwaltet ob Räume die geöffnet sind geschlossen werden sollen oder ob zu öffnende Räume jetzt
 * geöffnet werden sollen.<br>
 * Das soll auch passieren falls das Smartphone gelockt auf dem Tisch liegt, damit der Akku nicht
 * die ganze Zeit gedrained wird.
 */
public class RoomLivecycleService extends Service {

    private static final String TAG = "HostRoomCloserService";
    private final List<RoomItem> toSubscribe = Collections.synchronizedList(new ArrayList<>());
    private final List<RoomItem> toSend = Collections.synchronizedList(new ArrayList<>());
    private final NetworkStoppedStateReceiver networkStoppedStateReceiver =
            new NetworkStoppedStateReceiver();
    private Repository repository;
    private AtomicBoolean keepRunning;
    private List<RoomItem> futureRooms;
    private List<RoomItem> openrooms;
    private List<RoomItem> notClosedNotOwnRooms;
    private List<RoomItem> notClosedOwnRooms;
    private Boolean mqttServiceBound;
    private MQTTService mqttService;
    private Handler handler;
    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.v(TAG, "onServiceConnected");
            mqttService = ((MQTTService.LocalBinder) service).getMQTTService();
            postPendingRooms();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // unintentionally disconnected
            Log.v(TAG, "onServiceDisconnected");
            unbindMQTTService(); // cleanup
        }
    };

    private void bindMQTTService() {
        Log.v(TAG, "bindMQTTService");
        Intent intent = new Intent(this, MQTTService.class);
        intent.setAction(MQTTService.ACTION_PRESS);
        mqttServiceBound = bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        if (!mqttServiceBound) {
            Log.w(TAG, "could not try to bind service, will not be bound");
        }
    }

    private void unbindMQTTService() {
        Log.v(TAG, "unbindMQTTService");
        if (mqttServiceBound) {
            mqttServiceBound = false;
            unbindService(serviceConnection);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler();
        repository = new Repository(this);
        keepRunning = new AtomicBoolean(false);
        Log.v(TAG, "Service created");
    }

    /**
     * Falls der Service gestartet wird soll der der Überprüferthread gestartet, der mqqt gebunden
     * und der NetzwerkAusfallStateReceiver anlaufen.
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v(TAG, "Started Service");
        keepRunning.set(true);
        startThread();
        bindMQTTService();
        bindNetworkStopppedStateReceiver();
        return START_STICKY;
    }

    private void bindNetworkStopppedStateReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(networkStoppedStateReceiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        keepRunning.set(false);
        unregisterReceiver(networkStoppedStateReceiver);
        unbindMQTTService();
    }

    /**
     * Startet den Überprüfungsthread
     */
    void startThread() {
        // falls nachrichten nicht empfangen wurde wird der Raumstatus lokal aktualisiert
        // check rooms periodisch
        //raum öffnen
        //raum schließen
        //hinzufügen aller eigenen Raume auf welche gehört werden soll
        //mqttService.addRoomToListen(room, false);
        //hinzufügen aller fremd Raume auf welche gehört werden soll
        Thread checkingThread = new Thread(() -> {
            // falls nachrichten nicht empfangen wurde wird der Raumstatus lokal aktualisiert
            long now = System.currentTimeMillis();
            openrooms = repository.getAllNotOwnRoomsWithRoomStatus(RoomItem.ROOMISOPEN);
            futureRooms = repository.getAllNotOwnRoomsWithRoomStatus(RoomItem.ROOMWILLOPEN);
            //alle hosträume die sich öffnen sollen oder geschlossen werden solle überprüfen ob
            //sie jetzt die vss erfüllen. Dieser check wird nur initial ausgeführt.
            for (RoomItem room : futureRooms) {
                if (room.startTime <= now && room.endTime >= now) {
                    room.status = RoomItem.ROOMISOPEN;
                    repository.updateRoomItem(room);
                    Log.v(TAG, "opening room " + room.id);
                }
            }
            for (RoomItem room : openrooms) {
                if (room.startTime >= now || room.endTime <= now) {
                    room.status = RoomItem.ROOMISCLOSE;
                    repository.updateRoomItem(room);
                    Log.v(TAG, "Closing room " + room.id);
                }
            }

            // check rooms periodisch
            while (keepRunning.get()) {
                now = System.currentTimeMillis();
                openrooms = repository.getAllOwnRoomsWithRoomStatus(RoomItem.ROOMISOPEN);
                futureRooms = repository.getAllOwnRoomsWithRoomStatus(RoomItem.ROOMWILLOPEN);

                //raum öffnen
                for (RoomItem room : futureRooms) {
                    if (room.startTime <= now && room.endTime >= now) {
                        room.status = RoomItem.ROOMISOPEN;
                        repository.updateRoomItem(room);
                        toSend.add(room);
                        Log.v(TAG, "opening room " + room.id);
                    }
                }
                //raum schließen
                for (RoomItem room : openrooms) {
                    if (room.startTime >= now || room.endTime <= now) {
                        room.status = RoomItem.ROOMISCLOSE;
                        repository.updateRoomItem(room);
                        repository.kickOutParticipants(room, System.currentTimeMillis());
                        toSend.add(room);
                        Log.v(TAG, "Closing room " + room.id);
                    }
                }

                //hinzufügen aller eigenen Raume auf welche gehört werden soll
                notClosedOwnRooms = repository.getAllOwnRoomsWithRoomStatus(RoomItem.ROOMWILLOPEN);
                notClosedOwnRooms.addAll(repository.getAllOwnRoomsWithRoomStatus(RoomItem.ROOMISOPEN));
                //                    mqttService.addRoomToListen(room, false);
                //nur auf offene räume hören
                toSubscribe.addAll(notClosedOwnRooms);

                //hinzufügen aller fremd Raume auf welche gehört werden soll. nur für als
                //Teilnehmer
                notClosedNotOwnRooms = repository.getAllNotOwnRoomsWithRoomStatus(RoomItem.
                        ROOMWILLOPEN);
                notClosedNotOwnRooms.addAll(repository.
                        getAllNotOwnRoomsWithRoomStatus(RoomItem.ROOMISOPEN));
                toSubscribe.addAll(notClosedNotOwnRooms);

                handler.post(RoomLivecycleService.this::postPendingRooms);

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        checkingThread.start();
    }

    /**
     * Alle Räume asynchron rausschicken.
     */
    private void postPendingRooms() {
        if (mqttService == null) {
            return;
        }
        ArrayList<RoomItem> localToSend = new ArrayList<>();
        synchronized (toSend) {
            localToSend.addAll(toSend);
            toSend.clear();
        }

        for (RoomItem room : localToSend) {
            boolean status = room.status == RoomItem.ROOMISCLOSE;
            mqttService.sendRoom(room, status);
        }

        ArrayList<RoomItem> localToSubscribe = new ArrayList<>();
        synchronized (toSubscribe) {
            localToSubscribe.addAll(toSubscribe);
            toSubscribe.clear();
        }

        for (RoomItem room : localToSubscribe) {
            boolean status = room.fremdId != null;
            mqttService.addRoomToListen(room, status);
        }
    }
}
