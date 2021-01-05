package de.pbma.moa.createroomdemo.Activity;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import org.joda.time.DateTime;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import de.pbma.moa.createroomdemo.RoomRoom.RoomItem;
import de.pbma.moa.createroomdemo.RoomRoom.Repository;

public class RoomLivecycleService extends Service {

    private static final String TAG = "HostRoomCloserService";
    private Repository repository;
    private AtomicBoolean keepRunning;
    private Thread checkingThread;
    private List<RoomItem> closedrooms;
    private List<RoomItem> openrooms;



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        repository = new Repository(this);
        keepRunning =new AtomicBoolean(false);
        Log.v(TAG,"Service created");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v(TAG,"Started Service");
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
    }

    void startThread() {
        checkingThread = new Thread(() -> {
            while (keepRunning.get()) {
                long now = DateTime.now().getMillis();
                closedrooms = repository.getAllClosedRooms();
                openrooms = repository.getAllOpenRooms();
                for (RoomItem closedroom : closedrooms) {
                    if (closedroom.startTime <= now && closedroom.endTime >= now) {
                        repository.openById(closedroom.id);
                        Log.v(TAG, "opening room " + closedroom.id);
                    }
                }
                for (RoomItem openroom : openrooms) {
                    if (openroom.startTime >= now || openroom.endTime <= now) {
                        repository.closeById(openroom.id);
                        Log.v(TAG, "Closing room " + openroom.id);
                    }
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
