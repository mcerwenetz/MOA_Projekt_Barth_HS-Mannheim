package de.pbma.moa.createroomdemo.RoomParticipant;

import android.content.Context;

import androidx.lifecycle.LiveData;

import java.util.List;

public class ParticipantRepository {
    private ParticipantDao dao;
    private LiveData<List<ParticipantItem>> participantList;
    private Context context;

    public ParticipantRepository(Context context) {
        this.context = context;
        ParticipantDatabase participantDatabase = ParticipantDatabase.getInstance(context);
        dao = participantDatabase.dao();
    }

    public void deleteParticipantsOfRoom(long roomId) {
        new Thread(() -> {
            dao.deleteParticipantsOfRoom(roomId);
        }).start();
    }

    public LiveData<List<ParticipantItem>> getParticipantsOfRoom(long roomId) {
        return dao.getParticipantsOfRoom(roomId);
    }

    public void addEntry(ParticipantItem item) {
         dao.insert(item);
    }

}
