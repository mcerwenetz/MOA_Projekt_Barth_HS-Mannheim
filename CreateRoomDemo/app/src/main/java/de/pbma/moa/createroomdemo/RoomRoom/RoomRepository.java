package de.pbma.moa.createroomdemo.RoomRoom;

import android.content.Context;

import androidx.lifecycle.LiveData;

import java.util.List;

public class RoomRepository {
    private final RoomDao dao; //final hinzugefuegt
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

    public void addEntry(RoomItem item, AfterInsert afterInsert) {
        new Thread(() -> {
            long id = dao.insert(item);
            RoomItem newItem = dao.getItemByIdNow(id);
            if (afterInsert != null) {
                afterInsert.inserted(newItem);
            }
        }).start();
    }

    public void update(RoomItem item) {
        new Thread(() -> {
            dao.update(item);
        }).start();
    }

    public LiveData<RoomItem> getID(long searchid) {
        return dao.getById(searchid);
    }

    public RoomItem getItemByIdNow(long searchid) {
        return dao.getItemByIdNow(searchid);
    }

    public LiveData<List<RoomItem>> getDbAll() {
        return roomList;
    }

    public List<RoomItem> getAllClosedRooms(){
        return dao.getAllClosedRooms();
    }

    public List<RoomItem> getAllOpenRooms(){
        return dao.getAllOpenRooms();
    }


    public LiveData<List<RoomItem>> getDbAllFromMeAsHost(String vorname, String name, String email, String phone) {
        return dao.getAllFromMeAsHost(vorname + " " + name, phone, email);
    }

    public LiveData<List<RoomItem>> getDbAllFromExceptMeAsHost(String vorname, String name, String email, String phone) {
        return dao.getAllFromExceptMeAsHost(vorname + " " + name, phone, email);
    }

    public static interface AfterInsert {
        public void inserted(RoomItem item);
    }
}

