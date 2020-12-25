package de.pbma.moa.createroomdemo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.room.Room;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.pbma.moa.createroomdemo.room.RoomItem;
import de.pbma.moa.createroomdemo.room.RoomRepository;

public class MainActivity extends AppCompatActivity {
    final static String TAG = MainActivity.class.getCanonicalName();

    private ArrayList<RoomItem> roomList;
    private ListView lv;
    private RoomRepository roomRepo;
    private RoomListAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "OnCreate");

        setContentView(R.layout.roomlist_page);

    }
    //Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.room_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.create_newRoom:
                Log.v(TAG,"onOptionsItemSelected() create new Room");
                //TODO create Class for new Room
//               Intent intent = new Intent(this, );
//               startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    Observer<List<RoomItem>> observer = new Observer<List<RoomItem>>() {
        @Override
        public void onChanged(List<RoomItem> changedTodos) {
            lv.clear();
            lv.addAll(changedTodos);
            adapter.notifyDataSetChanged();
        }
    };
}
