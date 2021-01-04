package de.pbma.moa.createroomdemo.Activity;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import org.joda.time.DateTime;

import java.util.concurrent.atomic.AtomicBoolean;

import de.pbma.moa.createroomdemo.RoomRoom.RoomItem;
import de.pbma.moa.createroomdemo.RoomRoom.RoomRepository;

public class RoomCloserService extends Service {

    private RoomItem roomItem;
    private final RoomRepository repository;
    private Thread countingThread;
    private final AtomicBoolean keepCounting;

    public RoomCloserService(RoomItem roomItem, RoomRepository repository) {
        this.roomItem = roomItem;
        this.repository = repository;
        this.keepCounting = new AtomicBoolean(true);
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startThread();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            countingThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    void startThread() {
        countingThread = new Thread(() -> {
            long now = DateTime.now().getMillis();
            while (keepCounting.get()) {
                if (roomItem.endTime > now) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    now = DateTime.now().getMillis();
                    roomItem = repository.getItemByIdNow(roomItem.id);
                } else {
                    roomItem.open = false;
                    repository.update(roomItem);
                    keepCounting.set(false);
                    stopSelf();
                }
            }
        });
        countingThread.start();
    }


}
