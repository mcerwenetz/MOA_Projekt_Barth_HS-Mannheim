package de.pbma.moa.createroomdemo.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.Observer;

import java.io.File;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.pbma.moa.createroomdemo.BuildConfig;
import de.pbma.moa.createroomdemo.ParticipantListAdapter;
import de.pbma.moa.createroomdemo.PdfClass;
import de.pbma.moa.createroomdemo.R;
import de.pbma.moa.createroomdemo.RoomParticipant.ParticipantItem;
import de.pbma.moa.createroomdemo.RoomParticipant.ParticipantRepository;
import de.pbma.moa.createroomdemo.RoomRoom.RoomItem;
import de.pbma.moa.createroomdemo.RoomRoom.RoomRepository;

public class ParticipantHostActivity extends AppCompatActivity {
    final static String TAG = ParticipantHostActivity.class.getCanonicalName();
    final static String INTENT_ROOM_ID = "roomId";
    private Long roomId = (long) 0;
    private RoomItem roomItem = null;
    private ArrayList<ParticipantItem> participantItemArrayList;
    private ParticipantRepository participantRepository;
    private RoomRepository roomRepository;

    private ListView lv;

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
        test(); //TODO zeigt zum testen einfach teilnehmer an
        setContentView(R.layout.page_participants_list);

        adapter = new ParticipantListAdapter(this, participantItemArrayList);
        lv = findViewById(R.id.lv_participant);
        lv.setAdapter(adapter);


        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            roomId = bundle.getLong(ParticipantHostActivity.INTENT_ROOM_ID);
        }

        //participantRepository = new ParticipantRepository(this);
       // participantRepository.getParticipantsOfRoom(roomId).observe(this, observer);
        roomRepository = new RoomRepository(ParticipantHostActivity.this);
        roomRepository.getID(roomId).observe(ParticipantHostActivity.this, new Observer<RoomItem>() {
            @Override
            public void onChanged(RoomItem roomItem) {
                ParticipantHostActivity.this.roomItem = roomItem;
            }
        });
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
                shareParticipants();
        }
        return super.onOptionsItemSelected(item);
    }

    private void shareParticipants() {

        //generate PDF with qrCode an room infos -> saved in external file system
        PdfClass pdf = new PdfClass(ParticipantHostActivity.this);
        if(roomItem==null)
            return;
        File file = pdf.createPdfParticipantInfos(this.participantItemArrayList,this.roomItem);

        Log.v(TAG, "showPDF(" + file.getName() + ")");
        if (!file.exists()) {
            Toast.makeText(this, "Something wrong \n " + "file: " + file.getPath(), Toast.LENGTH_LONG).show();
            return;
        }
        //start share Intent
        try {
            Uri uri = FileProvider.getUriForFile(ParticipantHostActivity.this, BuildConfig.APPLICATION_ID + ".provider", file);
            Intent intent = ShareCompat.IntentBuilder.from(ParticipantHostActivity.this)
                    .setType(URLConnection.guessContentTypeFromName(file.getName()))
                    .setStream(uri)
                    .setChooserTitle("Choose bar")
                    .createChooserIntent()
                    .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            this.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(ParticipantHostActivity.this, "Something wrong \n " + "file: " + file.getPath(), Toast.LENGTH_LONG).show();
            Log.e(TAG, e.getMessage());
        }
    }


    //TODO irgendwann entfernen
    private void test(){
        Date date = new Date();
        ArrayList<ParticipantItem> list = new ArrayList<ParticipantItem>();
        ParticipantItem item =  ParticipantItem.createParticipant("Raphael Barth", "1727882", "barthra@web.de", "+49 0176 42619753", 0, date.getTime());
        item.exitTime = date.getTime()+100000;

        list.add(item);list.add(item);list.add(item);list.add(item);list.add(item);list.add(item);list.add(item);list.add(item);list.add(item);list.add(item);
        list.add(item);list.add(item);list.add(item);list.add(item);list.add(item);list.add(item);list.add(item);list.add(item);list.add(item);list.add(item);
        list.add(item);list.add(item);list.add(item);list.add(item);list.add(item);list.add(item);list.add(item);list.add(item);list.add(item);list.add(item);
        list.add(item);list.add(item);list.add(item);list.add(item);list.add(item);list.add(item);list.add(item);list.add(item);list.add(item);list.add(item);

        this.participantItemArrayList = list;
    }
}
