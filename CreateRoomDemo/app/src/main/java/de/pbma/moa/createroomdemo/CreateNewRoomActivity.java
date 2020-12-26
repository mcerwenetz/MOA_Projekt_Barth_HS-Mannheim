package de.pbma.moa.createroomdemo;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.WriterException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import de.pbma.moa.createroomdemo.room.RoomItem;
import de.pbma.moa.createroomdemo.room.RoomRepository;

public class CreateNewRoomActivity extends AppCompatActivity {
    final static String TAG = CreateNewRoomActivity.class.getCanonicalName();
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
    boolean startTime, startDate, endTime, endDate = false;
    RoomRepository repo;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "OnCreate");
        repo = new RoomRepository(this);

        calendar = Calendar.getInstance();
        year_end = calendar.get(Calendar.YEAR);
        year_start = calendar.get(Calendar.YEAR);
        month_end = calendar.get(Calendar.MONTH);
        month_start = calendar.get(Calendar.MONTH);
        day_end = calendar.get(Calendar.DAY_OF_MONTH);
        day_start = calendar.get(Calendar.DAY_OF_MONTH);
        hour_end = calendar.get(Calendar.HOUR_OF_DAY);
        hour_start = calendar.get(Calendar.HOUR_OF_DAY);
        minute_end = calendar.get(Calendar.MINUTE);
        minute_start = calendar.get(Calendar.MINUTE);


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
                btnStartTime.setText(String.format("%02d:%02d", hourOfDay, minute));
                hour_start = hourOfDay;
                minute_start = minute;
                startTime = true;
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
                btnEndTime.setText(String.format("%02d:%02d", hourOfDay, minute));
                hour_end = hourOfDay;
                minute_end = minute;
                endTime = true;
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
                btnStartDate.setText(String.format("%02d.%02d.%02d", dayOfMonth, (monthOfYear + 1), year));
                year_start = year;
                day_start = dayOfMonth;
                month_start = monthOfYear;
                startDate = true;
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
                btnEndDate.setText(String.format("%02d.%02d.%02d", dayOfMonth, (monthOfYear + 1), year));
                year_end = year;
                day_end = dayOfMonth;
                month_end = monthOfYear;
                endDate = true;
            }
        }, year_end, month_end, day_end);
        datePickerDialog.show();

    }

    private void setBtnCreateClicked(View view) {
        Log.v(TAG, "setBtnCreateClicked");
        if (etTitel.getText().toString().equals("")) {
            Log.v(TAG, "Titel empty");
            return;
        }
        if (etOrt.getText().toString().equals("")) {
            Log.v(TAG, "Ort empty");
            return;
        }
        if (etAdresse.getText().toString().equals("")) {
            Log.v(TAG, "Address empty");
            return;
        }
        if (!(startDate && startTime && endDate && endTime)) {
            Log.v(TAG, "Start and End Time not set");
            return;
        }

        calendar.set(year_start, month_start, day_start, hour_start, minute_start);
        long start = calendar.getTime().getTime();
        calendar.set(year_end, month_end, day_end, hour_end, minute_end);
        long end = calendar.getTime().getTime();
        if (start >= end) {
            Log.v(TAG, "endtime is earlier then starttime");
            return;
        }
        long now = Calendar.getInstance().getTime().getTime();
        if (now >= start) {
            Log.v(TAG, "start time is in the past");
            return;
        }
//        MySelf me = new MySelf(this);
//        if (!me.isValide()) {
//            Log.v(TAG, "prefs not valide");
//            //TODO preferences öffen um  seine daten zu bearbeiten
//            return;
//        }
        Log.v(TAG, "createRoomItem");

        RoomItem item = RoomItem.createRoom(
                etTitel.getText().toString(),
//                me.getFirstName() + me.getName(),
                "Raphael Barth",
//                me.getEmail(),
                "barthra@web.de",
//                me.getPhone(),
                "+49 176 42619753",
                etOrt.getText().toString(),
                etAdresse.getText().toString(),
                etExtra.getText().toString(),
                start,
                end);

        //repo.addEntry(item);
        //TODO activity showing room infos and pop current activity from stack
//        Intent intent = new Intent(this,);
//        startActivity(intent);
        test(item);
    }

    //TODO function muss später teilweise in RaumDetailsActivity (nur zu test zwechen hier)
    void test(RoomItem item) {
//        RoomItem item = RoomItem.createRoom(
//                "TestRaum",
//                "Raphael Barth",
//                "barthra@web.de",
//                "+49 176 42619753",
//                "73630 Remshalden",
//                "Fronäckerstr.40",
//                "Das ist ein Testraum",
//                Calendar.getInstance().getTime().getTime() + 0,
//                Calendar.getInstance().getTime().getTime() + 60000);
        QrCodeManger qrCodeManager = new QrCodeManger(this);
        Bitmap qrCode = null;
        try {
            qrCode = qrCodeManager.createQrCode(item.roomName + "/" + item.eMail + "/" + item.id);//TODO function zum erstellen der QR URI siehe auch PDF Creator
        } catch (WriterException e) {
            e.printStackTrace();
        }
        PdfClass pdf = new PdfClass(this);
        File file = pdf.createPdfRoomInfos(item, qrCode);
       // pdf.showPDF(file);

    }


}
