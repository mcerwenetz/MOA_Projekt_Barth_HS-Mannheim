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

    @Query("SELECT * FROM  dbRoom WHERE id=:roomid")
    abstract LiveData<RoomItem> getById(long roomid);

    @Query("SELECT * FROM  dbRoom WHERE id=:roomid")
    abstract RoomItem getItemByIdNow(long roomid);

    @Query("DELETE FROM dbRoom WHERE endTime <(:timeNow-:timeSpanOfTwoWeeks)")
    abstract void deleteAllOlderTwoWeeks(long timeNow, long timeSpanOfTwoWeeks);

    @Query("SELECT * FROM dbRoom WHERE endTime <(:timeNow-:timeSpanOfTwoWeeks)")
    abstract LiveData<List<RoomItem>> getAllOlderTwoWeeks(long timeNow, long timeSpanOfTwoWeeks);

    @Query("SELECT * FROM  dbRoom")
    abstract LiveData<List<RoomItem>> getAll();

    @Query("SELECT * FROM  dbRoom WHERE open=1")
    public abstract List<RoomItem> getAllOpenRooms();

    @Query("SELECT * FROM  dbRoom WHERE open=0")
    public abstract List<RoomItem> getAllClosedRooms();

    @Query("SELECT * FROM  dbRoom WHERE eMail=:eMail OR host = :name OR phone =:phone")
    abstract LiveData<List<RoomItem>> getAllFromMeAsHost(String name, String phone, String eMail);

    @Query("SELECT * FROM  dbRoom WHERE eMail !=:eMail AND host != :name AND phone !=:phone ORDER BY startTime")
    abstract LiveData<List<RoomItem>> getAllFromExceptMeAsHost(String name, String phone, String eMail);

    @Query("UPDATE dbroom SET open=0 WHERE id=:roomId")
    public abstract void closeRoomById(long roomId);

    @Query("UPDATE dbroom SET open=1 WHERE id=:roomId")
    public abstract void openRoomById(long roomId);



}
