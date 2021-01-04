package de.pbma.moa.createroomdemo.Activity;

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
import de.pbma.moa.createroomdemo.RoomParticipant.ParticipantItem;
import de.pbma.moa.createroomdemo.RoomParticipant.ParticipantRepository;
import de.pbma.moa.createroomdemo.RoomRoom.RoomItem;
import de.pbma.moa.createroomdemo.RoomRoom.RoomRepository;

//Activity dient zur Ansicht der Teilnehmerliste aus der Ansicht eines Teinehmers hierfür wird nur der Name
//und die Matrikelnummer eines Teilnehmers angezeigt.

public class Activity_15_ParticipantViewParticipant extends AppCompatActivity {
    final static String TAG = Activity_15_ParticipantViewParticipant.class.getCanonicalName();
    final static String INTENT_ROOM_ID = "roomId";
    ListView lv;
    private Long roomId = (long) 0;
    private RoomItem roomItem = null;
    private ArrayList<ParticipantItem> participantItemArrayList;
    private ParticipantRepository participantRepository;
    private RoomRepository roomRepository;
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

        adapter = new ListAdapter_15_ParticipantParticipant(this, participantItemArrayList);
        lv = findViewById(R.id.lv_15_participants);
        lv.setAdapter(adapter);

        //Holen der ID aus der Datenbank
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            roomId = bundle.getLong(Activity_23_HostViewParticipant.INTENT_ROOM_ID);
        }

        roomRepository = new RoomRepository(Activity_15_ParticipantViewParticipant.this);
        roomRepository.getID(roomId).observe(Activity_15_ParticipantViewParticipant.this, new Observer<RoomItem>() {
            @Override
            public void onChanged(RoomItem roomItem) {
                Activity_15_ParticipantViewParticipant.this.roomItem = roomItem;
            }
        });
    }

//    private void test(){
//        Date date = new Date();
//        ArrayList<ParticipantItem> list = new ArrayList<ParticipantItem>();
//        ParticipantItem item =  ParticipantItem.createParticipant("Raphael Barth", "1727882", "barthra@web.de", "+49 0176 42619753", 0, date.getTime());
//        item.exitTime = date.getTime()+100000;
//
//        list.add(item);list.add(item);list.add(item);list.add(item);list.add(item);list.add(item);list.add(item);list.add(item);list.add(item);list.add(item);
//        list.add(item);list.add(item);list.add(item);list.add(item);list.add(item);list.add(item);list.add(item);list.add(item);list.add(item);list.add(item);
//        list.add(item);list.add(item);list.add(item);list.add(item);list.add(item);list.add(item);list.add(item);list.add(item);list.add(item);list.add(item);
//        list.add(item);list.add(item);list.add(item);list.add(item);list.add(item);list.add(item);list.add(item);list.add(item);list.add(item);list.add(item);
//
//        this.participantItemArrayList = list;
//    }
}
