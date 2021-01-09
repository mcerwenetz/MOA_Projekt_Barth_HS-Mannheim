package de.pbma.moa.createroomdemo.activitys;

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

import java.util.concurrent.atomic.AtomicLong;

import de.pbma.moa.createroomdemo.R;
import de.pbma.moa.createroomdemo.database.RoomItem;
import de.pbma.moa.createroomdemo.database.Repository;

public class Activity_14_RoomParticipantDetail extends AppCompatActivity {
    final static String TAG = Activity_14_RoomParticipantDetail.class.getCanonicalName();
    final static String ID = "RoomID";
    long roomId;
    private Button btnLeave, btnPartic;
    private TextView tvRoom, tvOpenClose, tvTimeout,tvHost;
    private RoomItem itemPartic;
    private Repository repoPartic;
    private LiveData<RoomItem> liveDataPartic;
    private TimeoutRefresherThread timeoutRefresherThread;
    private AtomicLong endtimeAtomic;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.v(TAG, "OnCreated RoomParticipantDetailActivity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_14_room_participant_detail_activity);
        bindUI();

        //Holt die Daten aus der Bank
        if (roomId != -1) {
            liveDataPartic = repoPartic.getRoomByID(roomId);
            repoPartic = new Repository(this);
            roomId = getIntent().getExtras().getLong(ID, -1);
            liveDataPartic.observe(this, new Observer<RoomItem>() {
                @Override
                public void onChanged(RoomItem roomItem) {
                    updateRoom(roomItem);
                    Activity_14_RoomParticipantDetail.this.itemPartic = roomItem;
                    endtimeAtomic.set(itemPartic.endTime);
                    if (itemPartic.open) {
                        timeoutRefresherThread.initialStart();
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

    private void bindUI() {
        //Button und Textview zuweisen
        btnLeave = findViewById(R.id.btn_14_leave);
        btnPartic = findViewById(R.id.btn_14_particpantlist);

        tvHost = findViewById(R.id.tv_14_kontakt_daten);
        tvRoom = findViewById(R.id.tv_14_roomname_value);
        tvOpenClose = findViewById(R.id.tv_14_status_value);
        tvTimeout = findViewById(R.id.tv_14_timeout_value);
        endtimeAtomic = new AtomicLong(0);
        timeoutRefresherThread = new TimeoutRefresherThread(this, tvTimeout, endtimeAtomic);

        btnLeave.setOnClickListener(this::onClickBtnLeave);
        btnPartic.setOnClickListener(this::onClickBtnPartic);

    }


    private void onClickBtnLeave(View view) {
        //Todo so bald der Knopf aktiviert ist wird der Teilnehmner aus der Datenbank gelöscht
    }

    private void onClickBtnPartic(View view) {
        Intent intent = new Intent(Activity_14_RoomParticipantDetail.this, Activity_15_ParticipantViewParticipant.class);
//        intent.putExtra(ParticipantParticipantActivity.INTENT_ROOM_ID, item.id);
        startActivity(intent);
    }
}
