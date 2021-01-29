package de.pbma.moa.createroomdemo.database;

import android.content.Context;

import androidx.lifecycle.LiveData;

import java.util.List;


/**
 * Enthält alle Methoden für die Benutzung der Datenbank.<br>
 * Alle Methoden die Now als Suffix haben geben kein live data zurück und dürfen nur in einem extra
 * thread verwendet werden.<br>
 * Alle Funktionen die Own enthalten geben Selbsterstellte Räume, also Räume des hostes, zurück.
 */
public class Repository {
    private final RoomDao roomDao;
    private final ParticipantDao participantDao;

    public Repository(Context context) {
        AppDatabase appDatabase = AppDatabase.getInstance(context);
        roomDao = appDatabase.roomDao();
        participantDao = appDatabase.participantDao();
    }

    /**
     * Ruft {@link ParticipantDao#getCountOfExistingParticipantsInRoom(long)}
     */
    public int getCountOfExistingParticipantsInRoomNow(long roomId) {
        return participantDao.getCountOfExistingParticipantsInRoom(roomId);
    }

    /**
     * Ruft {@link ParticipantDao#setParticipantExitTime(long, long)}
     */
    public void kickOutParticipants(RoomItem room, long currentTimeMillis) {
        new Thread(() -> participantDao.setParticipantExitTime(room.id, currentTimeMillis)).start();
    }

    /**
     * Ruft {@link ParticipantDao#insert(ParticipantItem)}
     */
    public long addParticipantEntryNow(ParticipantItem item) {
        return participantDao.insert(item);
    }

    /**
     * Löscht alle Räume in allen Räumen die älter als zwei Wochen sind
     */
    public void DeleteRoomAndParticipantOlderTwoWeeks() {
        new Thread(() -> {
            long currentTime = System.currentTimeMillis();
            final long timeSpanOfTwoWeeks = 1209600000;
            List<RoomItem> roomItems = roomDao.getAllOlderTwoWeeks(System.currentTimeMillis(),
                    timeSpanOfTwoWeeks);
            for (RoomItem item : roomItems) {
                participantDao.deleteParticipantsOfRoom(item.id);
            }
            roomDao.deleteAllOlderTwoWeeks(currentTime, timeSpanOfTwoWeeks);
        }).start();
    }

    /**
     * Ruft {@link ParticipantDao#update(ParticipantItem)}
     */
    public void updateParticipantItem(ParticipantItem item) {
        new Thread(() -> participantDao.update(item)).start();
    }

    /**
     * Ruft {@link ParticipantDao#getPaticipantItemNow(long, String)}
     */
    public ParticipantItem getPaticipantItemNow(Long roomId, String email) {
        return participantDao.getPaticipantItemNow(roomId, email);
    }

    /**
     * Ruft {@link ParticipantDao#getParticipantsOfRoom(long)}
     *
     * @param roomId Id eines Raumes.
     * @return Liste der Teilnehmer des Raumes mit roomId.
     */
    public LiveData<List<ParticipantItem>> getParticipantsOfRoom(long roomId) {
        return participantDao.getParticipantsOfRoom(roomId);
    }

    /**
     * Ruft {@link ParticipantDao#getParticipantsOfRoomNow(long)}
     */
    public List<ParticipantItem> getParticipantsOfRoomNow(long roomId) {
        return participantDao.getParticipantsOfRoomNow(roomId);
    }

    /**
     * Ruft {@link ParticipantDao#insert(ParticipantItem)}
     */
    public void addParticipantEntry(ParticipantItem item) {
        new Thread(() -> participantDao.insert(item)).start();
    }

    //Ab hier nur noch RoomDao Methoden

    /**
     * @param item        Das RoomItem das hinzugefügt werden soll.
     * @param afterInsert Callback die ausgeführt wenn das Item eingefügt werden soll. Muss von
     *                    außen mitgeben und implementiert werden.
     */
    public void addRoomEntry(RoomItem item, AfterInsert afterInsert) {
        new Thread(() -> {
            long id = roomDao.insert(item);
            RoomItem newItem = roomDao.getItemByIdNow(id);
            if (afterInsert != null) {
                afterInsert.inserted(newItem);
            }
        }).start();
    }

    /**
     * Ruft {@link RoomDao#update(RoomItem)}
     */
    public void updateRoomItem(RoomItem item) {
        new Thread(() -> roomDao.update(item)).start();
    }

    /**
     * Holt aus der Datenbank ein Raum mit den Feldern aus dem roomTag.
     */
    public long getIdOfRoomByRoomTagNow(String roomTag) {
        String[] elements = roomTag.split("/");
        long Id = Long.parseLong(elements[2]);
        return roomDao.getIdOfRoomByRoomTagNow(elements[0], elements[1], Id);
    }

    /**
     * Ruft {@link RoomDao#getById(long)}
     */
    public LiveData<RoomItem> getRoomByID(long searchid) {
        return roomDao.getById(searchid);
    }

    /**
     * Ruft {@link RoomDao#getItemByIdNow(long)}
     */
    public RoomItem getRoomItemByIdNow(long searchid) {
        return roomDao.getItemByIdNow(searchid);
    }

    /**
     * Ruft {@link RoomDao#getAllOwnRoomsWithRoomStatus(int)}
     */
    public List<RoomItem> getAllOwnRoomsWithRoomStatus(int status) {
        return roomDao.getAllOwnRoomsWithRoomStatus(status);
    }

    /**
     * Ruft {@link RoomDao#getAllOwnRoomsWithRoomStatus(int)}
     */
    public List<RoomItem> getAllNotOwnRoomsWithRoomStatus(int status) {
        return roomDao.getAllNotOwnRoomsWithRoomStatus(status);
    }

    /**
     * Ruft {@link RoomDao#getAllFromExceptMeAsHost()}
     */
    public LiveData<List<RoomItem>> getAllRoomsWithMeAsHost() {
        return roomDao.getAllFromMeAsHost();
    }

    /**
     * Ruft {@link RoomDao#getAllFromExceptMeAsHost()}
     */
    public LiveData<List<RoomItem>> getAllRoomsWithoutMeAsHost() {
        return roomDao.getAllFromExceptMeAsHost();
    }

    /**
     * Interface für {@link #addRoomEntry(RoomItem, AfterInsert)}. Nachdem ein Raum eingefügt wurde
     * wird {@link AfterInsert#inserted(RoomItem)} aufgerufen.
     */
    public interface AfterInsert {
        void inserted(RoomItem item);
    }

}

