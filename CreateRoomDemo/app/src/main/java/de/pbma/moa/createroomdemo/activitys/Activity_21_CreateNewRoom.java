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

/**
 * Der Host stellt hier alle möglichen Raumparameter ein und
 * läuft dann in die {@link Activity_22_RoomHostDetail}
 */
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
    Calendar calendarStart, calendarEnd;

    Repository repo;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "OnCreate");
        this.setTitle("Erstellung eines Events");
        repo = new Repository(this);

        //Calender basteln für Start und Endzeit
        calendarStart = Calendar.getInstance();
        calendarEnd = Calendar.getInstance();

        setStartCalender();
        setEndCalender();


        setContentView(R.layout.page_21_create_room);
        //Binde buttons
        btnCreate = findViewById(R.id.btn_21_create);
        btnEndDate = findViewById(R.id.btn_21_enddate);
        btnEndTime = findViewById(R.id.btn_21_endtime);
        btnStartDate = findViewById(R.id.btn_21_startdate);
        btnStartTime = findViewById(R.id.btn_21_starttime);

        updateBtnText();


        //binde edittexts
        etAdresse = findViewById(R.id.et_21_raumaddress);
        etExtra = findViewById(R.id.et_21_raumextra);
        etOrt = findViewById(R.id.et_21_raumort);
        etTitel = findViewById(R.id.et_21_raumtitel);

        //Setze ocls für die Buttons
        btnStartTime.setOnClickListener(this::setBtnStartTimeClicked);
        btnStartDate.setOnClickListener(this::setBtnStartDateClicked);
        btnEndTime.setOnClickListener(this::setBtnEndTimeClicked);
        btnEndDate.setOnClickListener(this::setBtnEndDateClicked);
        btnCreate.setOnClickListener(this::setBtnCreateClicked);
    }

    private void setEndCalender() {
        calendarEnd.setTimeInMillis(calendarStart.getTimeInMillis());
        calendarEnd.add(Calendar.HOUR_OF_DAY, 1);
    }

    private void setStartCalender() {
        calendarStart.add(Calendar.MINUTE, 5);
    }

    private void updateBtnText() {
        int minute_start, hour_start, day_start, month_start, year_start;
        year_start = calendarStart.get(Calendar.YEAR);
        month_start = calendarStart.get(Calendar.MONTH);
        day_start = calendarStart.get(Calendar.DAY_OF_MONTH);
        hour_start = calendarStart.get(Calendar.HOUR_OF_DAY);
        minute_start = calendarStart.get(Calendar.MINUTE);

        int minute_end, hour_end, day_end, month_end, year_end;
        year_end = calendarEnd.get(Calendar.YEAR);
        month_end = calendarEnd.get(Calendar.MONTH);
        day_end = calendarEnd.get(Calendar.DAY_OF_MONTH);
        hour_end = calendarEnd.get(Calendar.HOUR_OF_DAY);
        minute_end = calendarEnd.get(Calendar.MINUTE);

        //Setze Start und Endzeit Buttons unten auf Calendar Werte
        btnEndDate.setText(String.format(Locale.GERMAN, "%02d.%02d.%02d", day_end,
                (month_end + 1), year_end));
        btnStartDate.setText(String.format(Locale.GERMAN, "%02d.%02d.%02d", day_start,
                (month_start + 1), year_start));
        btnEndTime.setText(String.format(Locale.GERMAN, "%02d:%02d", hour_end, minute_end));
        btnStartTime.setText(String.format(Locale.GERMAN, "%02d:%02d", hour_start,
                minute_start));
    }

    /**
     * Speichert Datumsangaben falls das Smartphone gedreht wird.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharSequence(BTNED, btnEndDate.getText());
        outState.putCharSequence(BTNET, btnEndTime.getText());
        outState.putCharSequence(BTNSD, btnStartDate.getText());
        outState.putCharSequence(BTNST, btnStartTime.getText());
        Log.v(TAG, "onSaveInstanceState");
    }

    /**
     * Restored die gespeicherten Daten nach dem Drehen des Smartphones.
     */
    @Override
    protected void onRestoreInstanceState(Bundle saveInstanceState) {
        super.onRestoreInstanceState(saveInstanceState);
        btnStartDate.setText(saveInstanceState.getCharSequence(BTNSD));
        btnStartTime.setText(saveInstanceState.getCharSequence(BTNST));
        btnEndDate.setText(saveInstanceState.getCharSequence(BTNET));
        btnEndTime.setText(saveInstanceState.getCharSequence(BTNED));
        Log.v(TAG, "onRestoreInstanceState");

    }

    /**
     * Startet einen Timepickerdialog um die Startzeit zu setzen.
     */
    private void setBtnStartTimeClicked(View view) {
        Log.v(TAG, "setBtnStartTimeClicked");

        timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Log.v(TAG, "setBtnEndTimeClicked " + hourOfDay + " " + minute);
                calendarStart.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendarStart.set(Calendar.MINUTE, minute);
                setEndCalender();
                updateBtnText();
            }
        }, calendarStart.get(Calendar.HOUR_OF_DAY), calendarStart.get(Calendar.MINUTE), true);
        timePickerDialog.show();
    }

    /**
     * Startet einen Timepickerdialog um die Endzeit zu setzen.
     */
    private void setBtnEndTimeClicked(View view) {
        Log.v(TAG, "setBtnEndTimeClicked");
        timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Log.v(TAG, "setBtnEndTimeClicked " + hourOfDay + " " + minute);
                calendarEnd.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendarEnd.set(Calendar.MINUTE, minute);
                updateBtnText();
            }
        }, calendarEnd.get(Calendar.HOUR_OF_DAY), calendarEnd.get(Calendar.MINUTE), true);
        timePickerDialog.show();
    }

    /**
     * Startet einen DatePickerDialog um das StartDatum zu setzen.
     */
    public void setBtnStartDateClicked(View v) {
        Log.v(TAG, "setBtnStartDateClicked");
        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Log.v(TAG, "setBtnStartDateClicked " + dayOfMonth + " " + monthOfYear + " " +
                        year);
                btnStartDate.setText(String.format(Locale.GERMAN, "%02d.%02d.%02d", dayOfMonth,
                        (monthOfYear + 1), year));
                calendarStart.set(Calendar.YEAR, year);
                calendarStart.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                calendarStart.set(Calendar.MONTH, monthOfYear);
                setEndCalender();
                updateBtnText();
            }
        }, calendarStart.get(Calendar.YEAR), calendarStart.get(Calendar.MONTH), calendarStart.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();

    }

    /**
     * Startet einen DatePickerDialog um das EndDatum zu setzen.
     */
    private void setBtnEndDateClicked(View v) {
        Log.v(TAG, "setBtnEndDateClicked");
        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Log.v(TAG, "setBtnEndDateClicked " + dayOfMonth + " " + monthOfYear + " " +
                        year);
                btnEndDate.setText(String.format(Locale.GERMAN, "%02d.%02d.%02d", dayOfMonth,
                        (monthOfYear + 1), year));
                calendarEnd.set(Calendar.YEAR, year);
                calendarEnd.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                calendarEnd.set(Calendar.MONTH, monthOfYear);
                updateBtnText();
            }
        }, calendarEnd.get(Calendar.YEAR), calendarEnd.get(Calendar.MONTH), calendarEnd.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();

    }

    /**
     * Checkt ob mandatory einträge eingetragen wurden, ob die Datumsangaben logisch sind,
     * ob das Hostprofil valid ist und startet dann den Intent auf
     * {@link Activity_22_RoomHostDetail}
     */
    private void setBtnCreateClicked(View view) {
        Log.v(TAG, "setBtnCreateClicked");
        //check raumname leer
        if (etTitel.getText().toString().equals("")) {
            Log.v(TAG, "Titel empty");
            Toast.makeText(this, R.string.fehlerhafter_titel, Toast.LENGTH_LONG).show();
            return;
        }
        //check ob ort leer
        if (etOrt.getText().toString().equals("")) {
            Log.v(TAG, "Ort empty");
            Toast.makeText(this, R.string.fehlerhafter_ort, Toast.LENGTH_LONG).show();
            return;
        }
        //check ob adresse leer
        if (etAdresse.getText().toString().equals("")) {
            Log.v(TAG, "Address empty");
            Toast.makeText(this, R.string.fehlerhafte_adresse, Toast.LENGTH_LONG).show();
            return;
        }


        long now = Calendar.getInstance().getTime().getTime();
        calendarStart.set(Calendar.SECOND,0);
        calendarStart.set(Calendar.MILLISECOND,0);
        calendarEnd.set(Calendar.SECOND,0);
        calendarEnd.set(Calendar.MILLISECOND,0);
        long start = calendarStart.getTimeInMillis();
        long end = calendarEnd.getTimeInMillis();

        //startzeit zu früh
        if (now > start) {
            Log.v(TAG, "start time is in the past");
            Toast.makeText(this, R.string.fehlerhafte_startzeit,
                    Toast.LENGTH_LONG).show();
            return;
        }
        //öffnungszeit negativ oder 0
        if (start >= end) {
            Log.v(TAG, "endtime is earlier then starttime");
            Toast.makeText(this, R.string.fehlerhafte_endzeit,
                    Toast.LENGTH_LONG).show();
            return;
        }
        MySelf me = new MySelf(Activity_21_CreateNewRoom.this);
        //check ob die Profildaten stimmen.
        if (!me.isValide()) {
            Toast.makeText(this, R.string.fehlerhafte_settings,
                    Toast.LENGTH_LONG).show();
            Log.v(TAG, "prefs not valide");
            Intent intent = new Intent(Activity_21_CreateNewRoom.this,
                    PreferenceActivity.class);
            startActivity(intent);
            return;
        }
        Log.v(TAG, "createRoomItem");

        //Alles ok. Raum erstellen, Raum eintragen und Intent starten.
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

        repo.addRoomEntry(item, (newItem) ->
                Activity_21_CreateNewRoom.this.runOnUiThread(() -> {
                    Intent intent = new Intent(Activity_21_CreateNewRoom.this,
                            Activity_22_RoomHostDetail.class);
                    intent.putExtra(Activity_22_RoomHostDetail.ID, newItem.id);
                    startActivity(intent);
                    finish();
                }));
    }


}
