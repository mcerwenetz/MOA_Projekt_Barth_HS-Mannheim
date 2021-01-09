package de.pbma.moa.createroomdemo.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public abstract class ParticipantDao {
    @Insert
    abstract long insert(ParticipantItem item);

    @Update
    abstract void update(ParticipantItem item);

    @Delete
    abstract void delete(ParticipantItem item);

    @Query("SELECT * FROM  dbParticipant")
    abstract LiveData<List<ParticipantItem>> getAll();

    @Query("DELETE FROM dbParticipant WHERE roomId=:roomId")
    abstract void deleteParticipantsOfRoom(long roomId);

    @Query("SELECT * FROM  dbParticipant WHERE roomId=:roomId")
    abstract LiveData<List<ParticipantItem>> getParticipantsOfRoom(long roomId);

    @Query("SELECT * FROM  dbParticipant WHERE roomId=:roomId AND eMail=:eMail")
    abstract ParticipantItem getPaticipantItemNow(long roomId,String eMail);

}
