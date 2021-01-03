package de.pbma.moa.createroomdemo.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import org.joda.time.DateTime;

import de.pbma.moa.createroomdemo.R;
import de.pbma.moa.createroomdemo.RoomRoom.RoomItem;
import de.pbma.moa.createroomdemo.RoomRoom.RoomRepository;

public class Activity_14_RoomParticipantDetail extends AppCompatActivity {
    final static String TAG = Activity_14_RoomParticipantDetail.class.getCanonicalName();
    final static String ID = "RoomID";

    private Button btnLeave, btnPartic;
    private TextView tvRoom, tvOpenClose, tvTimeout;

    private RoomItem itemPartic;
    private RoomRepository repoPartic;
    private LiveData<RoomItem> liveDataPartic;
    private TimeoutRefresherThread timeoutRefresherThread;

    long roomId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.v(TAG,"OnCreated RoomParticipantDetailActivity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_14_room_participant_detail_activity);
        bindUI();

        //Holt die Daten aus der Bank
        repoPartic = new RoomRepository(this);
        roomId = getIntent().getExtras().getLong(ID, -1);
        if (roomId != -1) {
            liveDataPartic = repoPartic.getID(roomId);
            liveDataPartic.observe(this, new Observer<RoomItem>() {
                @Override
                public void onChanged(RoomItem roomItem) {
                    updateRoom(roomItem);
                    Activity_14_RoomParticipantDetail.this.itemPartic = roomItem;
                    if (itemPartic.open) {
                        timeoutRefresherThread.endtimeChanged(itemPartic.endTime);
                    }
                }
            });
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        timeoutRefresherThread.stop();
    }

    private void updateRoom(RoomItem item) {
        if (item != null) {
            tvRoom.setText(item.roomName);
            if (item.open) {
                tvOpenClose.setText("offen");
            } else {
                tvOpenClose.setText("geschlossen");
            }
        }
    }

    private void bindUI(){
        //Button und Textview zuweisen
        btnLeave    = findViewById(R.id.btn_view_partic_leave);
        btnPartic   = findViewById(R.id.btn_view_partic_particlist);

        tvRoom      = findViewById(R.id.tv_view_partic_roomname);
        tvOpenClose = findViewById(R.id.tv_view_partic_statustext);
        tvTimeout   = findViewById(R.id.tv_view_partic_timeouttext);

        timeoutRefresherThread = new TimeoutRefresherThread(this, tvTimeout,
                DateTime.now().getMillis());

        btnLeave.setOnClickListener(this::onClickBtnLeave);
        btnPartic.setOnClickListener(this::onClickBtnPartic);
    }


    private void onClickBtnLeave(View view){
        //Todo so bald der Knopf aktiviert ist wird der Teilnehmner aus der Datenbank gelöscht
    }

    private void onClickBtnPartic(View view){
        Intent intent = new Intent(Activity_14_RoomParticipantDetail.this, Activity_15_ParticipantViewParticipant.class);
//        intent.putExtra(ParticipantParticipantActivity.INTENT_ROOM_ID, item.id);
        startActivity(intent);
    }
}
