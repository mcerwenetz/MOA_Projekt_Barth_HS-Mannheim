package de.pbma.moa.createroomdemo.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

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

public class Activity_10_RoomListVisited extends AppCompatActivity {
    final static String TAG = Activity_10_RoomListVisited.class.getCanonicalName();
    private ArrayList<RoomItem> roomList;
    private ListView lv;
    private Repository roomRepo;
    private ListAdapter_20_HostRoom adapter;
    Observer<List<RoomItem>> observer = new Observer<List<RoomItem>>() {
        @Override
        public void onChanged(List<RoomItem> changedTodos) {
            roomList.clear();
            roomList.addAll(changedTodos);
            adapter.notifyDataSetChanged();
        }
    };
    private AdapterView.OnItemClickListener oicl = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Long roomid = (Long) view.getTag();
            Intent intent = new Intent(Activity_10_RoomListVisited.this,
                    Activity_14_RoomParticipantDetail.class);
            intent.putExtra(Activity_14_RoomParticipantDetail.ID, roomid);
            startActivity(intent);
        }
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "OnCreated RoomVisitedListActivity");
        setContentView(R.layout.page_10_visited_roomlist);

        roomList = new ArrayList<>();
        adapter = new ListAdapter_20_HostRoom(this, roomList);
        lv = findViewById(R.id.lv_10_rooms);
        lv.setAdapter(adapter);
        roomRepo = new Repository(this);
        MySelf me = new MySelf(this);
        roomRepo.getDbAllFromExceptMeAsHost(
                me.getFirstName(),
                me.getName(),
                me.getEmail(),
                me.getPhone()).observe(this, observer);
        lv.setOnItemClickListener(oicl);
    }
}
