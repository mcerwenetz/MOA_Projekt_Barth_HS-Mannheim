package de.pbma.moa.createroomdemo.Activity;

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

import java.util.ArrayList;
import java.util.List;

import de.pbma.moa.createroomdemo.ParticipantListAdapter;
import de.pbma.moa.createroomdemo.R;
import de.pbma.moa.createroomdemo.RoomParticipant.ParticipantItem;
import de.pbma.moa.createroomdemo.RoomParticipant.ParticipantRepository;

public class ParticipantHostActivity extends AppCompatActivity {
    final static String TAG = ParticipantHostActivity.class.getCanonicalName();
    final static String INTENT_ROOM_ID = "roomId";
    private Long roomId = (long) 0;
    private ArrayList<ParticipantItem> participantItemArrayList;
    private ListView lv;
    private ParticipantRepository participantRepository;
    private ParticipantListAdapter adapter;

    Observer<List<ParticipantItem>> observer = new Observer<List<ParticipantItem>>() {
        @Override
        public void onChanged(List<ParticipantItem> changedTodos) {
            participantItemArrayList.clear();
            participantItemArrayList.addAll(changedTodos);
            adapter.notifyDataSetChanged();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "OnCreate");
        participantItemArrayList = new ArrayList<>();
        setContentView(R.layout.page_participants_list);

        adapter = new ParticipantListAdapter(this, participantItemArrayList);
        lv = findViewById(R.id.lv_highscore);
        lv.setAdapter(adapter);


        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            roomId = bundle.getLong(ParticipantHostActivity.INTENT_ROOM_ID);
        }
        participantRepository = new ParticipantRepository(this);
        participantRepository.getParticipantsOfRoom(roomId).observe(this, observer);
    }

    //Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.participant_host_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.export_participant_list:
                //TODO create PDF and share
        }
        return super.onOptionsItemSelected(item);
    }
}
