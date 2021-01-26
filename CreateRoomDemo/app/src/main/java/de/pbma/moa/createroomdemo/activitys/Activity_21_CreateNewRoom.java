package de.pbma.moa.createroomdemo.activitys;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.Locale;

import de.pbma.moa.createroomdemo.R;
import de.pbma.moa.createroomdemo.database.Repository;
import de.pbma.moa.createroomdemo.database.RoomItem;
import de.pbma.moa.createroomdemo.preferences.MySelf;
import de.pbma.moa.createroomdemo.preferences.PreferenceActivity;

public class Activity_21_CreateNewRoom extends AppCompatActivity {
    final static String TAG = Activity_21_CreateNewRoom.class.getCanonicalName();
    final static String BTNSD = "btnStartDate";
    final static String BTNST = "btnStartTime";
    final static String BTNED = "btnEndDate";
    final static String BTNET = "btnEndTime";

    Button btnStartTime, btnStartDate, btnEndTime, btnEndDate, btnCreate;
    EditText etTitel, etExtra, etOrt, etAdresse;
    DatePickerDialog datePickerDialog;
    TimePickerDialog timePickerDialog;
    Calendar calendar;
    int minute_start, hour_start, day_start, month_start, year_start;
    int minute_end, hour_end, day_end, month_end, year_end;
    Repository repo;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "OnCreate");
        repo = new Repository(this);

        calendar = Calendar.getInstance();
        year_end = calendar.get(Calendar.YEAR);
        year_start = calendar.get(Calendar.YEAR);
        month_end = calendar.get(Calendar.MONTH);
        month_start = calendar.get(Calendar.MONTH);
        day_end = calendar.get(Calendar.DAY_OF_MONTH);
        day_start = calendar.get(Calendar.DAY_OF_MONTH);
        hour_end = calendar.get(Calendar.HOUR_OF_DAY) + 1;
        hour_start = calendar.get(Calendar.HOUR_OF_DAY);
        minute_end = calendar.get(Calendar.MINUTE) + 5;
        minute_start = calendar.get(Calendar.MINUTE) + 5;


        setContentView(R.layout.page_21_create_room);

        btnCreate = findViewById(R.id.btn_21_create);
        btnEndDate = findViewById(R.id.btn_21_enddate);
        btnEndTime = findViewById(R.id.btn_21_endtime);
        btnStartDate = findViewById(R.id.btn_21_startdate);
        btnStartTime = findViewById(R.id.btn_21_starttime);

        btnEndDate.setText(String.format(Locale.GERMAN, "%02d.%02d.%02d", day_end, (month_end + 1), year_end));
        btnStartDate.setText(String.format(Locale.GERMAN, "%02d.%02d.%02d", day_start, (month_start + 1), year_start));
        btnEndTime.setText(String.format(Locale.GERMAN, "%02d:%02d", hour_end, minute_end));
        btnStartTime.setText(String.format(Locale.GERMAN, "%02d:%02d", hour_start, minute_start));


        etAdresse = findViewById(R.id.et_21_raumaddress);
        etExtra = findViewById(R.id.et_21_raumextra);
        etOrt = findViewById(R.id.et_21_raumort);
        etTitel = findViewById(R.id.et_21_raumtitel);

        btnStartTime.setOnClickListener(this::setBtnStartTimeClicked);
        btnStartDate.setOnClickListener(this::setBtnStartDateClicked);
        btnEndTime.setOnClickListener(this::setBtnEndTimeClicked);
        btnEndDate.setOnClickListener(this::setBtnEndDateClicked);
        btnCreate.setOnClickListener(this::setBtnCreateClicked);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharSequence(BTNED, btnEndDate.getText());
        outState.putCharSequence(BTNET, btnEndTime.getText());
        outState.putCharSequence(BTNSD, btnStartDate.getText());
        outState.putCharSequence(BTNST, btnStartTime.getText());
        Log.v(TAG, "onSaveInstanceState");
    }

    @Override
    protected void onRestoreInstanceState(Bundle saveInstanceState) {
        super.onRestoreInstanceState(saveInstanceState);
        btnStartDate.setText(saveInstanceState.getCharSequence(BTNSD));
        btnStartTime.setText(saveInstanceState.getCharSequence(BTNST));
        btnEndDate.setText(saveInstanceState.getCharSequence(BTNET));
        btnEndTime.setText(saveInstanceState.getCharSequence(BTNED));
        Log.v(TAG, "onRestoreInstanceState");

    }

    private void setBtnStartTimeClicked(View view) {
        Log.v(TAG, "setBtnStartTimeClicked");

        timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Log.v(TAG, "setBtnEndTimeClicked " + hourOfDay + " " + minute);
                btnStartTime.setText(String.format(Locale.GERMAN, "%02d:%02d", hourOfDay, minute));
                hour_start = hourOfDay;
                minute_start = minute;
            }
        }, hour_start, minute_start, true);
        timePickerDialog.show();
    }


    private void setBtnEndTimeClicked(View view) {
        Log.v(TAG, "setBtnEndTimeClicked");
        timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Log.v(TAG, "setBtnEndTimeClicked " + hourOfDay + " " + minute);
                btnEndTime.setText(String.format(Locale.GERMAN, "%02d:%02d", hourOfDay, minute));
                hour_end = hourOfDay;
                minute_end = minute;
            }
        }, hour_end, minute_end, true);
        timePickerDialog.show();
    }

    public void setBtnStartDateClicked(View v) {
        Log.v(TAG, "setBtnStartDateClicked");
        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Log.v(TAG, "setBtnStartDateClicked " + dayOfMonth + " " + monthOfYear + " " + year);
                btnStartDate.setText(String.format(Locale.GERMAN, "%02d.%02d.%02d", dayOfMonth, (monthOfYear + 1), year));
                year_start = year;
                day_start = dayOfMonth;
                month_start = monthOfYear;
            }
        }, year_start, month_start, day_start);
        datePickerDialog.show();

    }

    private void setBtnEndDateClicked(View v) {
        Log.v(TAG, "setBtnEndDateClicked");
        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Log.v(TAG, "setBtnEndDateClicked " + dayOfMonth + " " + monthOfYear + " " + year);
                btnEndDate.setText(String.format(Locale.GERMAN,"%02d.%02d.%02d", dayOfMonth, (monthOfYear + 1), year));
                year_end = year;
                day_end = dayOfMonth;
                month_end = monthOfYear;
            }
        }, year_end, month_end, day_end);
        datePickerDialog.show();

    }

    private void setBtnCreateClicked(View view) {
        Log.v(TAG, "setBtnCreateClicked");
        if (etTitel.getText().toString().equals("")) {
            Log.v(TAG, "Titel empty");
            Toast.makeText(this, R.string.fehlerhafter_titel, Toast.LENGTH_LONG).show();
            return;
        }
        if (etOrt.getText().toString().equals("")) {
            Log.v(TAG, "Ort empty");
            Toast.makeText(this, R.string.fehlerhafter_ort, Toast.LENGTH_LONG).show();
            return;
        }
        if (etAdresse.getText().toString().equals("")) {
            Log.v(TAG, "Address empty");
            Toast.makeText(this, R.string.fehlerhafte_adresse, Toast.LENGTH_LONG).show();
            return;
        }


        long now = Calendar.getInstance().getTime().getTime();
        calendar.set(year_start, month_start, day_start, hour_start, minute_start, 0);
        long start = calendar.getTime().getTime();
        calendar.set(year_end, month_end, day_end, hour_end, minute_end, 0);
        long end = calendar.getTime().getTime();

        if (now > start) {
            Log.v(TAG, "start time is in the past");
            Toast.makeText(this, R.string.fehlerhafte_startzeit, Toast.LENGTH_LONG).show();
            return;
        }
        if (start >= end) {
            Log.v(TAG, "endtime is earlier then starttime");
            Toast.makeText(this, R.string.fehlerhafte_endzeit, Toast.LENGTH_LONG).show();
            return;
        }

        MySelf me = new MySelf(Activity_21_CreateNewRoom.this);
        if (!me.isValide()) {
            Toast.makeText(this, R.string.fehlerhafte_settings, Toast.LENGTH_LONG).show();
            Log.v(TAG, "prefs not valide");
            Intent intent = new Intent(Activity_21_CreateNewRoom.this, PreferenceActivity.class);
            startActivity(intent);
            return;
        }
        Log.v(TAG, "createRoomItem");

        RoomItem item = RoomItem.createRoom(
                etTitel.getText().toString(),
                me.getFirstName() + " " + me.getName(),
                me.getEmail(),
                me.getPhone(),
                etOrt.getText().toString(),
                etAdresse.getText().toString(),
                etExtra.getText().toString(),
                start,
                end);

        repo.addRoomEntry(item, (newItem) -> {
            Activity_21_CreateNewRoom.this.runOnUiThread(() -> {
                Intent intent = new Intent(Activity_21_CreateNewRoom.this, Activity_22_RoomHostDetail.class);
                intent.putExtra(Activity_22_RoomHostDetail.ID, newItem.id);
                startActivity(intent);
                finish();
            });
        });
    }


}
