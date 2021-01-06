package de.pbma.moa.createroomdemo.activitys;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import java.util.ArrayList;
import java.util.List;

import de.pbma.moa.createroomdemo.ListAdapter_15_ParticipantParticipant;
import de.pbma.moa.createroomdemo.R;
import de.pbma.moa.createroomdemo.database.ParticipantItem;
import de.pbma.moa.createroomdemo.database.RoomItem;
import de.pbma.moa.createroomdemo.database.Repository;

//Activity dient zur Ansicht der Teilnehmerliste aus der Ansicht eines Teinehmers hierfür wird nur der Name
//und die Matrikelnummer eines Teilnehmers angezeigt.

public class Activity_15_ParticipantViewParticipant extends AppCompatActivity {
    final static String TAG = Activity_15_ParticipantViewParticipant.class.getCanonicalName();
    final static String INTENT_ROOM_ID = "roomId";
    private ListView lv;
    private RoomItem roomItem = null;
    private ArrayList<ParticipantItem> participantItemArrayList;
    private Repository repository;
    private ListAdapter_15_ParticipantParticipant adapter;

    Observer<List<ParticipantItem>> observer = new Observer<List<ParticipantItem>>() {
        @Override
        public void onChanged(List<ParticipantItem> participantItems) {
            participantItemArrayList.clear();
            participantItemArrayList.addAll(participantItems);
            adapter.notifyDataSetChanged();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "OnCreate ParticipantParticipantActivity");
        setContentView(R.layout.page_15_participants_list_participants_view);

        participantItemArrayList = new ArrayList<>();
        adapter = new ListAdapter_15_ParticipantParticipant(this, participantItemArrayList);
        lv = findViewById(R.id.lv_15_participants);
        lv.setAdapter(adapter);

        //Holen der ID aus der Datenbank
        Long roomId = (long) 0;
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            roomId = bundle.getLong(Activity_23_HostViewParticipant.INTENT_ROOM_ID);
        }

        repository = new Repository(Activity_15_ParticipantViewParticipant.this);
        repository.getID(roomId).observe(Activity_15_ParticipantViewParticipant.this, new Observer<RoomItem>() {
            @Override
            public void onChanged(RoomItem roomItem) {
                Activity_15_ParticipantViewParticipant.this.roomItem = roomItem;
            }
        });


    }
}
