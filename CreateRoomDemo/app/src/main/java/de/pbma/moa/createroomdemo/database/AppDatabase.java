package de.pbma.moa.createroomdemo.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;

/**
 * Datenbank Klasse der App. Die Tables sind die roomTable und participantTable.
 */
@Database(entities = {RoomItem.class, ParticipantItem.class}, version = 5, exportSchema = false)
public abstract class AppDatabase extends androidx.room.RoomDatabase {
    private static AppDatabase INSTANCE = null;

    private static AppDatabase createInstance(Context context) {
        return Room.databaseBuilder(context, AppDatabase.class, "dbRoom")
                .enableMultiInstanceInvalidation() //je Prozess eine Instanz,Synchronisation erforderlich und aktiviert
                .fallbackToDestructiveMigration() //DB löschen wenn sich Versionändert (böse)
                .build();
    }

    public static synchronized AppDatabase getInstance(Context context) {
        if (INSTANCE == null)
            INSTANCE = createInstance(context);
        return INSTANCE;
    }

    public abstract RoomDao roomDao();
    public abstract ParticipantDao participantDao();
}
