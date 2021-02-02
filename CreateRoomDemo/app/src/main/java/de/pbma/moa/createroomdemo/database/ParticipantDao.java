package de.pbma.moa.createroomdemo.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

/**
 * Alle Queries mit "Now" als Suffix liefern kein Livedata.
 */
@Dao
public abstract class ParticipantDao {
    @Insert
    abstract long insert(ParticipantItem item);

    @Update
    abstract void update(ParticipantItem item);

    @Delete
    abstract void delete(ParticipantItem item);

    /**
     * @return Alle Participants aus allen Räumen.
     */
    @Query("SELECT * FROM  dbParticipant")
    abstract LiveData<List<ParticipantItem>> getAll();

    /**
     * Löscht einen Participant aus der DB der in dem raum roomId drin is.
     */
    @Query("DELETE FROM dbParticipant WHERE roomId=:roomId")
    abstract void deleteParticipantsOfRoom(long roomId);

    /**
     * @return Wie {@link #getParticipantsOfRoomNow(long)} mit Livedata.
     */
    @Query("SELECT * FROM  dbParticipant WHERE roomId=:roomId")
    abstract LiveData<List<ParticipantItem>> getParticipantsOfRoom(long roomId);

    /**
     * @return Liste aller Participants im Raum roomId als Liste.
     */
    @Query("SELECT * FROM  dbParticipant WHERE roomId=:roomId")
    abstract List<ParticipantItem> getParticipantsOfRoomNow(long roomId);

    /**
     * @return Participant mit der email eMail im Raum roomId.
     */
    @Query("SELECT * FROM  dbParticipant WHERE roomId=:roomId AND eMail=:eMail ORDER BY id DESC")
    abstract ParticipantItem getPaticipantItemNow(long roomId,String eMail);

    /**
     * @return Anzahl der Teilnehmer eines raumes mit roomId
     */
    @Query("SELECT count(*) FROM  dbParticipant WHERE roomId=:roomId")
    public abstract int getCountOfExistingParticipantsInRoom(long roomId);


}
