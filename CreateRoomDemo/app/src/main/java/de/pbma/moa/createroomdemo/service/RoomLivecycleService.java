package de.pbma.moa.createroomdemo.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import org.joda.time.DateTime;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import de.pbma.moa.createroomdemo.database.Repository;
import de.pbma.moa.createroomdemo.database.RoomItem;


public class RoomLivecycleService extends Service {

    private static final String TAG = "HostRoomCloserService";
    private Repository repository;
    private AtomicBoolean keepRunning;
    private Thread checkingThread;
    private List<RoomItem> futureRooms;
    private List<RoomItem> openrooms;
    private List<RoomItem> notClosedNotOwnRooms;
    private List<RoomItem> notClosedOwnRooms;
    private boolean mqttServiceBound;
    private MQTTService mqttService;


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
            if (mqttService != null) {
                // deregister listeners, if there are any
            }
            mqttServiceBound = false;
            unbindService(serviceConnection);
        }
    }

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.v(TAG, "onServiceConnected");
            mqttService = ((MQTTService.LocalBinder) service).getMQTTService();
            mqttServiceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // unintentionally disconnected
            Log.v(TAG, "onServiceDisconnected");
            unbindMQTTService(); // cleanup
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        repository = new Repository(this);
        keepRunning = new AtomicBoolean(false);
        bindMQTTService();
        Log.v(TAG, "Service created");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v(TAG, "Started Service");
        keepRunning.set(true);
        startThread();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        keepRunning.set(false);
        try {
            checkingThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        unbindMQTTService();
    }

    void startThread() {
        checkingThread = new Thread(() -> {
            while (keepRunning.get()) {
                //60000 weil + 1 Minute
                long now = System.currentTimeMillis();
                openrooms = repository.getAllOwnRoomsWithRoomStatus(RoomItem.ROOMISOPEN);
                futureRooms = repository.getAllOwnRoomsWithRoomStatus(RoomItem.ROOMWILLOPEN);

                //raum öffnen
                for (RoomItem room : futureRooms) {
                    if (room.startTime <= now && room.endTime >= now) {
                        room.status = RoomItem.ROOMISOPEN;
                        repository.updateRoomItem(room);
                        mqttService.sendRoom(room, false);
                        Log.v(TAG, "opening room " + room.id);
                    }
                }
                //raum schließen
                for (RoomItem room : openrooms) {
                    if (room.startTime >= now || room.endTime <= now) {
                        room.status = RoomItem.ROOMISCLOSE;
                        repository.updateRoomItem(room);
                        mqttService.sendRoom(room, true);
                        Log.v(TAG, "Closing room " + room.id);
                    }
                }

                //hinzufügen aller eigenen Raume auf welche gehört werden soll
                notClosedOwnRooms = repository.getAllOwnRoomsWithRoomStatus(RoomItem.ROOMWILLOPEN);
                notClosedOwnRooms.addAll(repository.getAllOwnRoomsWithRoomStatus(RoomItem.ROOMISOPEN));
                for (RoomItem room : notClosedOwnRooms) {
                    mqttService.addRoomToListen(room, false);
                }

                //hinzufügen aller fremd Raume auf welche gehört werden soll
                notClosedNotOwnRooms = repository.getAllNotOwnRoomsWithRoomStatus(RoomItem.ROOMWILLOPEN);
                notClosedNotOwnRooms.addAll(repository.getAllNotOwnRoomsWithRoomStatus(RoomItem.ROOMISOPEN));
                for (RoomItem room : notClosedNotOwnRooms) {
                    mqttService.addRoomToListen(room, true);
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        checkingThread.start();
    }


}
