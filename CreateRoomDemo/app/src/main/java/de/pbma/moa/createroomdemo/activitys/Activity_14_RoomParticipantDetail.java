package de.pbma.moa.createroomdemo.activitys;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
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
import de.pbma.moa.createroomdemo.database.Repository;
import de.pbma.moa.createroomdemo.database.RoomItem;
import de.pbma.moa.createroomdemo.preferences.MySelf;
import de.pbma.moa.createroomdemo.service.MQTTService;

public class Activity_14_RoomParticipantDetail extends AppCompatActivity {
    final static String TAG = Activity_14_RoomParticipantDetail.class.getCanonicalName();
    public final static String ID = "RoomID";
    long roomId;
    private Button btnLeave, btnPartic;
    private TextView tvRoom, tvOpenClose, tvTimeout, tvHost;
    private RoomItem roomItem;
    private Repository repo;
    private LiveData<RoomItem> liveDataRoomItem;
    private TimeoutRefresherThread timeoutRefresherThread;
    private AtomicLong endtimeAtomic;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.v(TAG, "OnCreated RoomParticipantDetailActivity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_14_room_participant_detail_activity);
        bindUI();

        //Holt die Daten aus der Bank
        roomId = getIntent().getExtras().getLong(ID, -1);

        if (roomId != -1) {
            repo = new Repository(this);
            liveDataRoomItem = repo.getRoomByID(roomId);
            liveDataRoomItem.observe(this, new Observer<RoomItem>() {
                @Override
                public void onChanged(RoomItem roomItem) {
                    updateRoom(roomItem);
                    Activity_14_RoomParticipantDetail.this.roomItem = roomItem;
                    endtimeAtomic.set(Activity_14_RoomParticipantDetail.this.roomItem.endTime);
                    if (Activity_14_RoomParticipantDetail.this.roomItem.open) {
                        timeoutRefresherThread.initialStart();
                    }
                }
            });
        } else {
            finish();
        }


    }

    @Override
    protected void onResume() {
        mqttServiceBound = false;
        bindMQTTService();
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindMQTTService();
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
            tvHost.setText(item.host);
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
        if (!mqttServiceBound)
            return;
        mqttService.sendExitFromRoom(new MySelf(this), roomItem.getRoomTag());
        finish();
    }

    private void onClickBtnPartic(View view) {
        Intent intent = new Intent(Activity_14_RoomParticipantDetail.this, Activity_15_ParticipantViewParticipant.class);
//        intent.putExtra(ParticipantParticipantActivity.INTENT_ROOM_ID, item.id);
        startActivity(intent);
    }

    //MQTT gedönz
    private boolean mqttServiceBound;
    private MQTTService mqttService;

    private void bindMQTTService() {
        Log.v(TAG, "bindMQTTService");
        Intent intent = new Intent(this, MQTTService.class);
        intent.setAction(MQTTService.ACTION_PRESS);
        mqttServiceBound = bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        if (!mqttServiceBound) {
            Log.w(TAG, "could not try to bind service, will not be bound");
        }
    }

    private void unbindMQTTService() {
        Log.v(TAG, "unbindMQTTService");
        if (mqttServiceBound) {
            if (mqttService != null) {
                // deregister listeners, if there are any
            }
            mqttServiceBound = false;
            unbindService(serviceConnection);
        }
    }

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.v(TAG, "onServiceConnected");
            mqttService = ((MQTTService.LocalBinder) service).getMQTTService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // unintentionally disconnected
            Log.v(TAG, "onServiceDisconnected");
            unbindMQTTService(); // cleanup
        }
    };
}
