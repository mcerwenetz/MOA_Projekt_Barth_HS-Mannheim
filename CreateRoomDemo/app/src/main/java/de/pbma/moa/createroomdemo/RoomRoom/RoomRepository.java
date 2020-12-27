package de.pbma.moa.createroomdemo.RoomRoom;

import android.content.Context;

import androidx.lifecycle.LiveData;

import java.util.List;

public class RoomRepository {
    private RoomDao dao;
    private LiveData<List<RoomItem>> liveHighsocore;
    private Context context;
    public RoomRepository(Context context){
        this.context = context;
        RoomDatabase roomDatabase = RoomDatabase.getInstance(context);
        dao = roomDatabase.dao();
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

