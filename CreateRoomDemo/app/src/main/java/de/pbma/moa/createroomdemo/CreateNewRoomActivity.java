package de.pbma.moa.createroomdemo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import de.pbma.moa.createroomdemo.Preferences.MySelf;
import de.pbma.moa.createroomdemo.room.RoomItem;

public class CreateNewRoomActivity extends AppCompatActivity {
    final static String TAG = CreateNewRoomActivity.class.getCanonicalName();
    Button btnStartTime, btnStartDate, btnEndTime, btnEndDate, btnCreate;
    EditText etTitel, etExtra, etOrt, etAdresse;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "OnCreate");

        setContentView(R.layout.create_room_page);

        btnCreate = findViewById(R.id.btn_raum_create);
        btnEndDate = findViewById(R.id.btn_raum_end_date);
        btnEndTime = findViewById(R.id.btn_raum_end_time);
        btnStartDate = findViewById(R.id.btn_raum_start_date);
        btnStartTime = findViewById(R.id.btn_raum_start_time);

        etAdresse = findViewById(R.id.et_raum_address);
        etExtra = findViewById(R.id.et_raum_extra);
        etOrt = findViewById(R.id.et_raum_ort);
        etTitel = findViewById(R.id.et_raum_titel);


        btnStartTime.setOnClickListener(this::setBtnStartTimeClicked);
        btnStartDate.setOnClickListener(this::setBtnStartDateClicked);
        btnEndTime.setOnClickListener(this::setBtnEndTimeClicked);
        btnEndDate.setOnClickListener(this::setBtnEndDateClicked);
    }

    private void setBtnStartTimeClicked(View view) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    private void setBtnEndTimeClicked(View view) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public void setBtnStartDateClicked(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    private void setBtnEndDateClicked(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    private void setBtnCreateClicked(View view) {
        if (etTitel.getText().equals("")) {
            return;
        }
        if (etOrt.getText().equals("")) {
            return;
        }
        if (etAdresse.getText().equals("")) {
            return;
        }
        if (btnStartDate.getText().equals("")) {
            return;
        }
        if (btnStartTime.getText().equals("")) {
            return;
        }
        if (btnEndDate.getText().equals("")) {
            return;
        }
        if (btnEndTime.getText().equals("")) {
            return;
        }
        MySelf me = new MySelf(this);
        if (me.isValide()) {
            //TODO preferences öffen um  seine daten zu bearbeiten
            return;
        }
//        RoomItem.createRoom(etTitel.getText(),
//                me.getFirstName() + me.getName(),
//                me.getEmail(),
//                me.getPhone(),
//                etOrt.getText(),
//                etAdresse.getText(),
//                etExtra.getText(),
//                )

    }
}
