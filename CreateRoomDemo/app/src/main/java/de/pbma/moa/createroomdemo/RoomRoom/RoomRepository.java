package de.pbma.moa.createroomdemo.RoomRoom;

import android.content.Context;

import androidx.lifecycle.LiveData;

import java.util.List;

public class RoomRepository {
    private RoomDao dao;
    private LiveData<List<RoomItem>> roomList;
    private Context context;

    public RoomRepository(Context context) {
        this.context = context;
        RoomDatabase roomDatabase = RoomDatabase.getInstance(context);
        dao = roomDatabase.dao();
        roomList = dao.getAll();
    }

    public void deleteOlderTwoWeeks(long now) {
        final long timeSpanOfTwoWeeks = 1209600000;
        new Thread(() -> {
            dao.deleteAllOlderTwoWeeks(now, timeSpanOfTwoWeeks);
        }).start();
        ;
    }

    public void addEntry(RoomItem item) {
        new Thread(() -> {
            dao.insert(item);
        }).start();
    }

    public LiveData<List<RoomItem>> getDbAll() {
        return roomList;
    }
}

