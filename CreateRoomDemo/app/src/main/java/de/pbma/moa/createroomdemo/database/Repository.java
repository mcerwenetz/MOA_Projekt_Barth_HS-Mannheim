package de.pbma.moa.createroomdemo.database;

import android.content.Context;

import androidx.lifecycle.LiveData;

import java.util.List;


//Alle Funktionen die NOW enthlten geben kein live data zurück und dürfen nur in einem extra thread verwendet werden
//Alle Funktionen die Own enthalten geben Selbsterstellte Räume, also Räume des hostes, zurück.
public class Repository {
    private final RoomDao roomDao; //final hinzugefuegt

    private final ParticipantDao participantDao;
    private LiveData<List<RoomItem>> roomList;
    private final Context context;

    public int getCountOfExistingParticipantsInRoomNow(long roomId) {
        return participantDao.getCountOfExistingParticipantsInRoom(roomId);
    }


    public void kickOutParticipants(RoomItem room, long currentTimeMillis) {
        new Thread(() -> {
            participantDao.setParticipantExitTime(room.id, currentTimeMillis);
        }).start();
    }

    public long addParticipantEntryNow(ParticipantItem item) {
        return participantDao.insert(item);
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

    //Diese Funktion wird im LivecycleService aufgerufen, damit ein Host alle Räume die er selbst
    //erstellt hat erhält. Es wird sichergestellt dass Teilnehmer diese Räume nicht durch ihren
    //eigenen LivecycleService schließen können.
    public List<RoomItem> getAllOwnRoomsWithRoomStatus(int status) {
        return roomDao.getAllOwnRoomsWithRoomStatus(status);
    }

    public List<RoomItem> getAllNotOwnRoomsWithRoomStatus(int status) {
        return roomDao.getAllNotOwnRoomsWithRoomStatus(status);
    }


    public LiveData<List<RoomItem>> getAllRoomsWithMeAsHost() {
        return roomDao.getAllFromMeAsHost();
    }

    public LiveData<List<RoomItem>> getAllRoomsWithoutMeAsHost() {
        return roomDao.getAllFromExceptMeAsHost();
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

