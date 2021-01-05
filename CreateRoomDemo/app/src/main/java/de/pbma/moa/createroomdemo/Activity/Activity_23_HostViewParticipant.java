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
import java.util.List;

import de.pbma.moa.createroomdemo.BuildConfig;
import de.pbma.moa.createroomdemo.ListAdapter_23_HostParticipant;
import de.pbma.moa.createroomdemo.PdfClass;
import de.pbma.moa.createroomdemo.R;
import de.pbma.moa.createroomdemo.RoomRoom.ParticipantItem;
import de.pbma.moa.createroomdemo.RoomRoom.RoomItem;
import de.pbma.moa.createroomdemo.RoomRoom.Repository;

public class Activity_23_HostViewParticipant extends AppCompatActivity {
    final static String TAG = Activity_23_HostViewParticipant.class.getCanonicalName();
    final static String INTENT_ROOM_ID = "roomId";
    private RoomItem roomItem = null;

    private ArrayList<ParticipantItem> participantItemArrayList;
    private Repository repository;
    private ListView lv;
    private ListAdapter_23_HostParticipant adapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "OnCreate");
        setContentView(R.layout.page_23_participants_list_host_view);

        participantItemArrayList = new ArrayList<>();
        adapter = new ListAdapter_23_HostParticipant(Activity_23_HostViewParticipant.this, participantItemArrayList);
        lv = findViewById(R.id.lv_23_participant);
        lv.setAdapter(adapter);

        repository = new Repository(Activity_23_HostViewParticipant.this);

        Long roomId = (long) 0;
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            roomId = bundle.getLong(Activity_23_HostViewParticipant.INTENT_ROOM_ID);
        }

        repository.getParticipantsOfRoom(roomId).observe(this, new Observer<List<ParticipantItem>>() {
            @Override
            public void onChanged(List<ParticipantItem> participantItems) {
                participantItemArrayList.clear();
                participantItemArrayList.addAll(participantItems);
                adapter.notifyDataSetChanged();
            }
        });
        repository.getID(roomId).observe(Activity_23_HostViewParticipant.this, new Observer<RoomItem>() {
            @Override
            public void onChanged(RoomItem roomItem) {
                Activity_23_HostViewParticipant.this.roomItem = roomItem;
            }
        });
    }

    //Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_23_participants, menu);
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
        PdfClass pdf = new PdfClass(Activity_23_HostViewParticipant.this);
        if (roomItem == null)
            return;
        File file = pdf.createPdfParticipantInfos(this.participantItemArrayList, this.roomItem);

        Log.v(TAG, "showPDF(" + file.getName() + ")");
        if (!file.exists()) {
            Toast.makeText(this, "Something wrong \n " + "file: " + file.getPath(), Toast.LENGTH_LONG).show();
            return;
        }
        //start share Intent
        try {
            Uri uri = FileProvider.getUriForFile(Activity_23_HostViewParticipant.this, BuildConfig.APPLICATION_ID + ".provider", file);
            Intent intent = ShareCompat.IntentBuilder.from(Activity_23_HostViewParticipant.this)
                    .setType(URLConnection.guessContentTypeFromName(file.getName()))
                    .setStream(uri)
                    .setChooserTitle("Choose bar")
                    .createChooserIntent()
                    .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            this.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(Activity_23_HostViewParticipant.this, "Something wrong \n " + "file: " + file.getPath(), Toast.LENGTH_LONG).show();
            Log.e(TAG, e.getMessage());
        }
    }
}
