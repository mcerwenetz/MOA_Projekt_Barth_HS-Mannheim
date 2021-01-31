package de.pbma.moa.createroomdemo.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import java.util.ArrayList;
import java.util.List;

import de.pbma.moa.createroomdemo.ListAdapter_20_HostRoom;
import de.pbma.moa.createroomdemo.preferences.MySelf;
import de.pbma.moa.createroomdemo.R;
import de.pbma.moa.createroomdemo.database.RoomItem;
import de.pbma.moa.createroomdemo.database.Repository;

/**
 * Diese Klasse zeigt für Hosts alle Räume an die offen sind oder mal offen waren.
 */
public class Activity_20_RoomListHost extends AppCompatActivity {
    final static String TAG = Activity_20_RoomListHost.class.getCanonicalName();
    private ArrayList<RoomItem> roomList;
    private ListAdapter_20_HostRoom adapter;
    Observer<List<RoomItem>> observer = new Observer<List<RoomItem>>() {
        @Override
        public void onChanged(List<RoomItem> changedTodos) {
            roomList.clear();
            roomList.addAll(changedTodos);
            adapter.notifyDataSetChanged();
        }
    };
    private AdapterView.OnItemClickListener oicl = (parent, view, position, id) -> {
        Long roomid = (Long) view.getTag();
        Intent intent = new Intent(Activity_20_RoomListHost.this,
                Activity_22_RoomHostDetail.class);
        intent.putExtra(Activity_22_RoomHostDetail.ID, roomid);
        startActivity(intent);
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "OnCreate");
        this.setTitle("Übersicht der erstellten Events");
        roomList = new ArrayList<>();
        setContentView(R.layout.page_20_roomlist);
        adapter = new ListAdapter_20_HostRoom(this, roomList);
        ListView lv = findViewById(R.id.lv_20_room);
        lv.setAdapter(adapter);
        Repository roomRepo = new Repository(this);
        roomRepo.getAllRoomsWithMeAsHost().observe(this, observer);
        lv.setOnItemClickListener(oicl); //Erweiterung um einen onClickedListener
    }

    //Menü
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_21_create_room, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Wird gerufen wenn neuer Raum erstellt werden soll.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if(itemId == R.id.create_newRoom){
            Log.v(TAG, "onOptionsItemSelected() create new Room");
            Intent intent = new Intent(this, Activity_21_CreateNewRoom.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
