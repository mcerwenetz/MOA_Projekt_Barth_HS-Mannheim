package de.pbma.moa.createroomdemo.database;

import android.content.Context;

import androidx.lifecycle.LiveData;

import java.util.List;

public class Repository {
    private final RoomDao roomDao; //final hinzugefuegt

    private final ParticipantDao participantDao;
    private LiveData<List<RoomItem>> roomList;
    private final Context context;

    public int getCountOfExistingParticipantsInRoomNow(long roomId) {
        return participantDao.getCountOfExistingParticipantsInRoom(roomId);
    }

    public void setParticipantExitTime(RoomItem item, long currentTimeMillis) {
        new Thread(() -> {
            participantDao.setParticipantExitTime(item.id, currentTimeMillis);
        }).start();
    }

    public List<RoomItem> getAllNotOwnNotClosedRoomsNow(long currentTimeMillis) {
        return roomDao.getAllNotOwnNotClosedRoomsNow(currentTimeMillis);

    }

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

    public long getIdOfRoomByRoomTagNow(String roomTag) {
        String[] elements = roomTag.split("/");
        long Id = Long.parseLong(elements[2]);
        return roomDao.getIdOfRoomByRoomTagNow(elements[0], elements[1], Id);
    }

    public LiveData<RoomItem> getRoomByID(long searchid) {
        return roomDao.getById(searchid);
    }

    public ParticipantItem getPaticipantItemNow(Long roomId, String email) {
        return participantDao.getPaticipantItemNow(roomId, email);
    }

    public RoomItem getRoomItemByIdNow(long searchid) {
        return roomDao.getItemByIdNow(searchid);
    }

    public LiveData<List<RoomItem>> getAllRooms() {
        return roomList;
    }

    public List<RoomItem> getAllClosedRoomsNow() {
        return roomDao.getAllClosedRooms();
    }
    public List<RoomItem> getAllFutureRoomsNow(){
        return roomDao.getAllFutureRoomsNow();

    }

    public List<RoomItem> getAllOpenRoomsNow() {
        return roomDao.getAllOpenRooms();
    }

    public List<RoomItem> getAllOwnNotClosedRoomsNow(long currenMs) {
        return roomDao.getAllOwnNotClosedRoomsNow(currenMs);
    }

    public LiveData<List<RoomItem>> getAllRoomsWithMeAsHost() {
        return roomDao.getAllFromMeAsHost();
    }

    public LiveData<List<RoomItem>> getAllRoomsWithoutMeAsHost() {
        return roomDao.getAllFromExceptMeAsHost();
    }

    public void closeRoomById(long roomId) {
        new Thread(() -> {
            roomDao.closeRoomById(roomId);
        }).start();
    }

    public void openRoomById(long roomId) {
        new Thread(() -> {
            roomDao.openRoomById(roomId);
        }).start();
    }

    public LiveData<List<ParticipantItem>> getParticipantsOfRoom(long roomId) {
        return participantDao.getParticipantsOfRoom(roomId);
    }

    public List<ParticipantItem> getParticipantsOfRoomNow(long roomId) {
        return participantDao.getParticipantsOfRoomNow(roomId);
    }

    public LiveData<List<ParticipantItem>> getAllParticipants() {
        return participantDao.getAll();
    }

    public void addParticipantEntry(ParticipantItem item) {
        new Thread(() -> {
            participantDao.insert(item);
        }).start();
    }


}

