package de.pbma.moa.createroomdemo.database;

import android.content.Context;

import androidx.lifecycle.LiveData;

import java.util.List;

public class Repository {
    private final RoomDao roomDao; //final hinzugefuegt
    final long timeSpanOfTwoWeeks = 1209600000;

    private ParticipantDao participantDao;
    private LiveData<List<RoomItem>> roomList;
    private Context context;

    public Repository(Context context) {
        this.context = context;
        AppDatabase appDatabase = AppDatabase.getInstance(context);
        roomDao = appDatabase.roomDao();
        participantDao=appDatabase.participantDao();
        roomList = roomDao.getAll();
    }

    public void deleteRoomsOlderTwoWeeks(long now) {
        new Thread(() -> {
            roomDao.deleteAllOlderTwoWeeks(now, timeSpanOfTwoWeeks);
        }).start();
        ;
    }

    public void addEntry(RoomItem item, AfterInsert afterInsert) {
        new Thread(() -> {
            long id = roomDao.insert(item);
            RoomItem newItem = roomDao.getItemByIdNow(id);
            if (afterInsert != null) {
                afterInsert.inserted(newItem);
            }
        }).start();
    }

    public void update(RoomItem item) {
        new Thread(() -> {
            roomDao.update(item);
        }).start();
    }

    public LiveData<RoomItem> getID(long searchid) {
        return roomDao.getById(searchid);
    }

    public RoomItem getItemByIdNow(long searchid) {
        return roomDao.getItemByIdNow(searchid);
    }

    public LiveData<List<RoomItem>> getDbAll() {
        return roomList;
    }

    public List<RoomItem> getAllClosedRooms(){
        return roomDao.getAllClosedRooms();
    }

    public List<RoomItem> getAllOpenRooms(){
        return roomDao.getAllOpenRooms();
    }


    public LiveData<List<RoomItem>> getDbAllFromMeAsHost(String vorname, String name, String email, String phone) {
        return roomDao.getAllFromMeAsHost(vorname + " " + name, phone, email);
    }

    public LiveData<List<RoomItem>> getDbAllFromExceptMeAsHost(String vorname, String name, String email, String phone) {
        return roomDao.getAllFromExceptMeAsHost(vorname + " " + name, phone, email);
    }

    public void closeById(long roomId) {
        new Thread(() -> {
            roomDao.closeRoomById(roomId);
        }).start();
    }

    public void openById(long roomId) {
        new Thread(() -> {
            roomDao.openRoomById(roomId);
        }).start();
    }


    public static interface AfterInsert {
        public void inserted(RoomItem item);
    }

    public void deleteParticipantsOfRoom(long roomId) {
        new Thread(() -> {
            participantDao.deleteParticipantsOfRoom(roomId);
        }).start();
    }

    public LiveData<List<ParticipantItem>> getParticipantsOfRoom(long roomId) {
        return participantDao.getParticipantsOfRoom(roomId);
    }

    public LiveData<List<ParticipantItem>> getAll() {
        return participantDao.getAll();
    }

    public void addEntry(ParticipantItem item) {
        new Thread(() -> {
            participantDao.insert(item);
        }).start();
    }
    public LiveData<List<RoomItem>> getAllRoomsOlderTwoWeeks(long now){
        return roomDao.getAllOlderTwoWeeks(now,timeSpanOfTwoWeeks);
    }
}

