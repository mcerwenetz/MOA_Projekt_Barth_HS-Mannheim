package de.pbma.moa.createroomdemo.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {RoomItem.class}, version = 1, exportSchema = false)
public abstract class DatabaseRoom extends RoomDatabase {
    private static DatabaseRoom INSTANCE = null;

    private static DatabaseRoom createInstance(Context context) {
        return Room.databaseBuilder(context, DatabaseRoom.class, "dbRoom")
                .enableMultiInstanceInvalidation() //je Prozess eine Instanz,Synchronisation erforderlich und aktiviert
                .fallbackToDestructiveMigration() //DB löschen wenn sich Versionändert (böse) TODO entfernen ?
                .build();
    }

    public static synchronized DatabaseRoom getInstance(Context context) {
        if (INSTANCE == null)
            INSTANCE = createInstance(context);
        return INSTANCE;
    }

    public abstract DaoRoom dao();
}
