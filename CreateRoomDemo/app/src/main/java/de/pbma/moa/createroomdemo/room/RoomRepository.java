package de.pbma.moa.createroomdemo.room;

import android.content.Context;

import androidx.lifecycle.LiveData;

import java.util.List;

public class RoomRepository {
    private DaoRoom dao;
    private LiveData<List<RoomItem>> liveHighsocore;
    private Context context;
    public RoomRepository(Context context){
        this.context = context;
        DatabaseRoom databaseRoom = DatabaseRoom.getInstance(context);
        dao = databaseRoom.dao();
        liveHighsocore = dao.getAll();
    }
    public void clearDb(){
        new Thread(()->{
            dao.deleteAll();
        }).start();;
    }
    public void addEntry(RoomItem item){
        new Thread(()->{
            dao.insert(item);
        }).start();
    }
    public  LiveData<List<RoomItem>> getDbAll(){
        return liveHighsocore;
    }
}

