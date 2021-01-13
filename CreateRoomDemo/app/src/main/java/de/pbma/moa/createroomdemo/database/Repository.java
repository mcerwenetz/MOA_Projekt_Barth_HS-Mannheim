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

    public static interface AfterInsert {
        public void inserted(RoomItem item);
    }

    public Repository(Context context) {
        this.context = context;
        AppDatabase appDatabase = AppDatabase.getInstance(context);
        roomDao = appDatabase.roomDao();
        participantDao = appDatabase.participantDao();
        roomList = roomDao.getAll();
    }


    public void DeleteRoomAndParticipantOlderTwoWeeks() {
        new Thread(() -> {
            long currentTime = System.currentTimeMillis();
            final long timeSpanOfTwoWeeks = 1209600000;
            List<RoomItem> roomItems = roomDao.getAllOlderTwoWeeks(System.currentTimeMillis(), timeSpanOfTwoWeeks);
            for (RoomItem item : roomItems) {
                participantDao.deleteParticipantsOfRoom(item.id);
            }
            roomDao.deleteAllOlderTwoWeeks(currentTime, timeSpanOfTwoWeeks);
        }).start();
    }


    public void addRoomEntry(RoomItem item, AfterInsert afterInsert) {
        new Thread(() -> {
            long id = roomDao.insert(item);
            RoomItem newItem = roomDao.getItemByIdNow(id);
            if (afterInsert != null) {
                afterInsert.inserted(newItem);
            }
        }).start();
    }

    public long addRoomEntryNow(RoomItem roomItem) {
        return roomDao.insert(roomItem);
    }

    public void updateRoomItem(RoomItem item) {
        new Thread(() -> {
            roomDao.update(item);
        }).start();
    }

    public void updateParticipantItem(ParticipantItem item) {
        new Thread(() -> {
            participantDao.update(item);
        }).start();
    }

    public long getIdOfRoomByUriNow(String uri) {
        String[] elements = uri.split("/");
        long fremdId = Long.parseLong(elements[2]);
        return roomDao.getIdOfRoomByUriNow(elements[0], elements[1], fremdId);
    }

    public LiveData<RoomItem> getRoomByID(long searchid) {
        return roomDao.getById(searchid);
    }

    public ParticipantItem getPaticipantItemNow(Long roomId, String email) {
        return participantDao.getPaticipantItemNow(roomId, email);
    }

    public RoomItem getItemByIdNow(long searchid) {
        return roomDao.getItemByIdNow(searchid);
    }

    public LiveData<List<RoomItem>> getDbAll() {
        return roomList;
    }

    public List<RoomItem> getAllClosedRooms() {
        return roomDao.getAllClosedRooms();
    }

    public List<RoomItem> getAllOpenRooms() {
        return roomDao.getAllOpenRooms();
    }


    public LiveData<List<RoomItem>> getDbAllFromMeAsHost() {
        return roomDao.getAllFromMeAsHost();
    }

    public LiveData<List<RoomItem>> getDbAllFromExceptMeAsHost() {
        return roomDao.getAllFromExceptMeAsHost();
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




    public LiveData<List<ParticipantItem>> getParticipantsOfRoom(long roomId) {
        return participantDao.getParticipantsOfRoom(roomId);
    }

    public LiveData<List<ParticipantItem>> getAll() {
        return participantDao.getAll();
    }

    public void addParticipantEntry(ParticipantItem item) {
        new Thread(() -> {
            participantDao.insert(item);
        }).start();
    }


}

