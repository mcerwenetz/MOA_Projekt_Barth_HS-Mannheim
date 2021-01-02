package de.pbma.moa.createroomdemo.RoomRoom;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;

@Database(entities = {RoomItem.class}, version = 2, exportSchema = false)
public abstract class RoomDatabase extends androidx.room.RoomDatabase {
    private static RoomDatabase INSTANCE = null;

    private static RoomDatabase createInstance(Context context) {
        return Room.databaseBuilder(context, RoomDatabase.class, "dbRoom")
                .enableMultiInstanceInvalidation() //je Prozess eine Instanz,Synchronisation erforderlich und aktiviert
                .fallbackToDestructiveMigration() //DB löschen wenn sich Versionändert (böse) TODO entfernen ?
                .build();
    }

    public static synchronized RoomDatabase getInstance(Context context) {
        if (INSTANCE == null)
            INSTANCE = createInstance(context);
        return INSTANCE;
    }

    public abstract RoomDao dao();
}
