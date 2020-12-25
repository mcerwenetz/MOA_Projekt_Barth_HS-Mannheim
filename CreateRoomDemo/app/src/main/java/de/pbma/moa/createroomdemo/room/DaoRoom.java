package de.pbma.moa.createroomdemo.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public abstract class DaoRoom {
    @Insert
    abstract long insert(RoomItem item);
    @Update
    abstract void update(RoomItem item);
    @Delete
    abstract void delete(RoomItem item);
    @Query("DELETE FROM dbRoom")
    abstract  void deleteAll();
    @Query("SELECT * FROM  dbRoom")
    abstract LiveData<List<RoomItem>> getAll();

}
