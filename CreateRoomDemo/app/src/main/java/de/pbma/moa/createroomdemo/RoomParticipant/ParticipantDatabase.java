package de.pbma.moa.createroomdemo.RoomParticipant;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {ParticipantItem.class}, version = 1, exportSchema = false)
public abstract class ParticipantDatabase extends androidx.room.RoomDatabase {
    private static ParticipantDatabase INSTANCE = null;

    private static ParticipantDatabase createInstance(Context context) {
        return Room.databaseBuilder(context, ParticipantDatabase.class, "dbRoom")
                .enableMultiInstanceInvalidation() //je Prozess eine Instanz,Synchronisation erforderlich und aktiviert
                .fallbackToDestructiveMigration() //DB löschen wenn sich Versionändert (böse) TODO entfernen ?
                .build();
    }

    public static synchronized ParticipantDatabase getInstance(Context context) {
        if (INSTANCE == null)
            INSTANCE = createInstance(context);
        return INSTANCE;
    }

    public abstract ParticipantDao dao();
}

