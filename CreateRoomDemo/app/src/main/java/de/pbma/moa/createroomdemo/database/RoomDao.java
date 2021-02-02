package de.pbma.moa.createroomdemo.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public abstract class RoomDao {

    @Insert
    abstract long insert(RoomItem item);

    @Update
    abstract void update(RoomItem item);

    @Delete
    abstract void delete(RoomItem item);

    /**
     * @param roomid id des raumes der gesucht werden soll
     * @return Roomitem mit der id roomid
     */
    @Query("SELECT * FROM  dbRoom WHERE id=:roomid")
    abstract LiveData<RoomItem> getById(long roomid);

    /**
     * Holt einen Room aus der Datenbank mit der RaumId roomId.
     */
    @Query("SELECT * FROM  dbRoom WHERE id=:roomid")
    abstract RoomItem getItemByIdNow(long roomid);

    /**
     * @return RaumId eines Raumes mit den Feldern aus den einzuelnen Feldern eines Roomtags.
     */
    @Query("SELECT id FROM dbRoom " +
            "WHERE roomName= :name AND eMail=:eMail AND fremdId=:fremdId " +
            "OR roomName= :name AND eMail=:eMail AND id=:fremdId AND fremdId IS NULL " +
            "ORDER BY id DESC")
    abstract long getIdOfRoomByRoomTagNow(String name, String eMail, long fremdId);

    /**
     * Löscht Alle Einträge die älter als jetzt + 2 Wochen sind.  Wird beim Starten der App
     * ausgeführt
     * @param timeNow jetzt in epoch time
     * @param timeSpanOfTwoWeeks 2 Wochen in Sekunden
     */
    @Query("DELETE FROM dbRoom WHERE endTime <(:timeNow-:timeSpanOfTwoWeeks)")
    abstract void deleteAllOlderTwoWeeks(long timeNow, long timeSpanOfTwoWeeks);

    /**
     * @return Liste aller Räume die seit mehr als 2 Wochen in der Db liegen. Siehe auch
     * {@link RoomDao#getAllOlderTwoWeeks(long, long)}
     */
    @Query("SELECT * FROM dbRoom WHERE endTime <(:timeNow-:timeSpanOfTwoWeeks)")
    abstract List<RoomItem> getAllOlderTwoWeeks(long timeNow, long timeSpanOfTwoWeeks);

    /**
     * @return LiveData aller Räume in der RaumDb.
     */
    @Query("SELECT * FROM  dbRoom")
    abstract LiveData<List<RoomItem>> getAll();

    /**
     * Diese Methode gibt nur Räume zurück die denm Status roomStatus haben und vom Host erstellt
     * wurden.<br><br>
     * Wird im LivecycleService aufgerufen. Nur der LiveCycleService des Hostes soll Räume durch
     * Ablaufen des Timeouts schließen können. Sonst könnten Participants mit falscher Uhrzeit den
     * Raum schon früher schließen.
     * @return eine Liste aller RoomItems die nicht von Participants erstellt wurden.
     */
    @Query("SELECT * FROM  dbRoom WHERE status =:roomStatus  AND fremdId IS NULL")
    public abstract List<RoomItem> getAllOwnRoomsWithRoomStatus(int roomStatus);

    /**
     * @return Alle Fremdräume der Participants mit dem dem Status roomStatus
     */
    @Query("SELECT * FROM dbRoom WHERE fremdId IS NOT NULL AND status =:roomStatus")
    public abstract List<RoomItem> getAllNotOwnRoomsWithRoomStatus(int roomStatus);

    /**
     * @return Liste aller Räume die keine FremdID haben, also nicht von Participants sondern vom
     * Host sind.
     */
    @Query("SELECT * FROM  dbRoom WHERE fremdId IS NULL")
    abstract LiveData<List<RoomItem>> getAllFromMeAsHost();

    /**
     * Gegenstück zu {@link RoomDao#getAllFromMeAsHost()}
     * @return Livedata mit einer Liste aller Räume die eine FremdId haben, also vom Participant
     * sind.
     */
    @Query("SELECT * FROM  dbRoom WHERE fremdId IS NOT NULL")
    abstract LiveData<List<RoomItem>> getAllFromExceptMeAsHost();

    @Query("SELECT * FROM  dbRoom")
    public abstract List<RoomItem> getAllNow();
}
