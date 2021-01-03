package de.pbma.moa.createroomdemo.RoomRoom;

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
    abstract  LiveData<RoomItem> getById (long roomid);

    @Query("SELECT * FROM  dbRoom WHERE id=:roomid")
    abstract  RoomItem getItemByIdNow (long roomid);

    @Query("DELETE FROM dbRoom WHERE endTime <(:timeNow-:timeSpanOfTwoWeeks)")
    abstract void deleteAllOlderTwoWeeks(long timeNow,long timeSpanOfTwoWeeks );

    @Query("SELECT * FROM  dbRoom")
    abstract LiveData<List<RoomItem>> getAll();

//    @Query("UPDATE dbRoom SET open=0 WHERE id=:roomid")
//    abstract void closeRoomById(long roomid);
//
//    @Query("UPDATE dbRoom SET endTime=:endtime")
//    abstract void updateTimeout(long endtime);


}
