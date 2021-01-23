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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    private TextView tvRoom, tvStatus, tvTimeout, tvHost, tvHosteMail, tvHostPhone, tvEndTime, tvStartTime, tvPlace, tvAddress;
    private RoomItem classRoomItem;
    private Repository repo;
    private LiveData<RoomItem> liveDataRoomItem;
    private Boolean toSend = false;
    private TimeoutRefresherThread timeoutRefresherThread;
    private AtomicLong endtimeAtomic, startTimeAtomic;

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
                    classRoomItem = roomItem;
                    endtimeAtomic.set(classRoomItem.endTime);
                    startTimeAtomic.set(classRoomItem.startTime);
                    if (roomItem.status == RoomItem.ROOMISOPEN) {
                        timeoutRefresherThread.initialStart();
                        btnLeave.setEnabled(true);
                    } else if (roomItem.status == RoomItem.ROOMWILLOPEN) {
                        btnLeave.setEnabled(false);
//                      btnLeave.setAlpha(.5f); //transparent
                    } else {
                        //Wenn der Raum nicht offen ist soll der Thread gestoppt
                        //werden. Aber nur wenn er läuft.
                        if (timeoutRefresherThread.isAlive()) {
                            timeoutRefresherThread.stop();
                        }
                        //Tasten für Raum schließen disable
                        btnLeave.setEnabled(false);
                    }
                }
            });
        } else {
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        timeoutRefresherThread = new TimeoutRefresherThread(this, tvTimeout, endtimeAtomic, startTimeAtomic);
        timeoutRefresherThread.initialStart();
        bindMQTTService();
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
            DateFormat df = new SimpleDateFormat("dd.MM.yy HH:mm");
            tvStartTime.setText("Von: " + df.format(item.startTime));
            tvEndTime.setText("Bis: " + df.format(item.endTime));
            if (item.status == RoomItem.ROOMISOPEN) {
                tvStatus.setText("schließt in");
            } else if (item.status == RoomItem.ROOMWILLOPEN) {
                tvStatus.setText("öffnet in");
            } else {
                tvStatus.setText("geschlossen");
            }
            tvHost.setText(item.host);
            tvHosteMail.setText(item.eMail);
            tvHostPhone.setText(item.phone);
            tvPlace.setText(item.place);
            tvAddress.setText(item.address);
        }
    }

    private void bindUI() {
        //Button und Textview zuweisen
        btnLeave = findViewById(R.id.btn_14_leave);
        btnPartic = findViewById(R.id.btn_14_particpantlist);

        tvHost = findViewById(R.id.tv_14_kontakt_host_value);
        tvHosteMail = findViewById(R.id.tv_14_kontakt_hostemail);
        tvHostPhone = findViewById(R.id.tv_14_kontakt_hosttelefon);
        tvRoom = findViewById(R.id.tv_14_roomname_value);
        tvStatus = findViewById(R.id.tv_14_status_value);
        tvTimeout = findViewById(R.id.tv_14_status_timeout);
        tvStartTime = findViewById(R.id.tv_14_date_value_start);
        tvEndTime = findViewById(R.id.tv_14_date_value_end);
        tvPlace = findViewById(R.id.tv_14_location_place);
        tvAddress = findViewById(R.id.tv_14_location_address);
        endtimeAtomic = new AtomicLong(0);
        startTimeAtomic = new AtomicLong(0);


        btnLeave.setOnClickListener(this::onClickBtnLeave);
        btnPartic.setOnClickListener(this::onClickBtnPartic);

    }


    private void onClickBtnLeave(View view) {
        if (mqttService==null)
            toSend=true;
        else{
            mqttService.sendExitFromRoom(new MySelf(this), classRoomItem.getRoomTag());
        }
        finish();
    }

    private void onClickBtnPartic(View view) {
        Intent intent = new Intent(Activity_14_RoomParticipantDetail.this, Activity_15_ParticipantViewParticipant.class);
        intent.putExtra(Activity_15_ParticipantViewParticipant.INTENT_ROOM_ID, this.roomId);
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
            if(toSend)
                mqttService.sendExitFromRoom(new MySelf(Activity_14_RoomParticipantDetail.this), classRoomItem.getRoomTag());
            toSend = false;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // unintentionally disconnected
            Log.v(TAG, "onServiceDisconnected");
            unbindMQTTService(); // cleanup
        }
    };
}
