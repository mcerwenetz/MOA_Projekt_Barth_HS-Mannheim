package de.pbma.moa.createroomdemo.activitys;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.google.zxing.WriterException;

import org.joda.time.DateTime;

import java.io.File;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.concurrent.atomic.AtomicLong;

import de.pbma.moa.createroomdemo.BuildConfig;
import de.pbma.moa.createroomdemo.PdfClass;
import de.pbma.moa.createroomdemo.QrCodeManger;
import de.pbma.moa.createroomdemo.R;
import de.pbma.moa.createroomdemo.database.RoomItem;
import de.pbma.moa.createroomdemo.database.Repository;

//Activity dient zur Ansicht der Gastgeberinformationen eines Raumes. In Ihr werden Informationen über den Raum, Timeout
//und der Status des raus dargestellt.

public class Activity_22_RoomHostDetail extends AppCompatActivity {
    final static String TAG = Activity_22_RoomHostDetail.class.getCanonicalName();
    final static String ID = "RoomID";
    long roomid;
    private TextView tvtimeout, tvstatus, tvroomname, tvStartTime, tvEndTime, tvLocation;
    private Button btnopen, btntimeout, btnpartic;
    private RoomItem item;
    private Repository repo;
    private LiveData<RoomItem> liveData;
    private TimeoutRefresherThread timeoutRefresherThread;
    private AtomicLong endtimeAtomic;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "onCreated_Teilnehmer_Uebersicht");
        setContentView(R.layout.page_22_room_host_detail_activity);
        endtimeAtomic = new AtomicLong(0);
        bindUI();

        //Holt die Daten aus der Bank
        repo = new Repository(this);
        roomid = getIntent().getExtras().getLong(ID, -1);
        if (roomid != -1) {
            liveData = repo.getID(roomid);
            liveData.observe(this, new Observer<RoomItem>() {
                @Override
                public void onChanged(RoomItem roomItem) {
                    Activity_22_RoomHostDetail.this.item = roomItem;
                    updateRoom(roomItem);
                    //Speichern für Nebenläufigkeit
                    //So profitieren alle von Livedata
                    endtimeAtomic.set(item.endTime);
                    //der Timeoutrefresherthread wird nur gestartet wenn
                    //Der Raum offen ist.
                    if (item.open) {
                        timeoutRefresherThread.initialStart();
                        //Wenn der Raum nicht offen ist soll der Thread gestoppt
                        //werden. Aber nur wenn er läuft.
                    } else {
                        if (timeoutRefresherThread.isAlive()) {
                            timeoutRefresherThread.stop();
                            //Textview setzen nicht vergessen
                            tvtimeout.setText("00:00:00");
                        }
                    }
                }
            });
        }
    }

    private void bindUI() {
        tvroomname = findViewById(R.id.tv_22_roomname_value);
        tvstatus = findViewById(R.id.tv_22_roomstatus_value);
        tvtimeout = findViewById(R.id.tv_22_timeout_value);
        tvEndTime = findViewById(R.id.tv_22_endtime);
        tvStartTime = findViewById(R.id.tv_22_starttime);
        tvLocation=findViewById(R.id.tv_22_location);
        btnopen = findViewById(R.id.btn_22_closeroom);
        btntimeout = findViewById(R.id.btn_22_changetimeout);
        btnpartic = findViewById(R.id.btn_22_partiicipantlist);
        timeoutRefresherThread = new TimeoutRefresherThread(this, tvtimeout, endtimeAtomic);
        btnpartic.setOnClickListener(this::onViewParticipants);
        btnopen.setOnClickListener(this::onCloseRoom);
        btntimeout.setOnClickListener(this::onChangeTimeout);

    }

    @Override
    protected void onPause() {
        super.onPause();
        timeoutRefresherThread.stop();
    }

    private void updateRoom(RoomItem item) {
        if (item != null) {
            tvroomname.setText(item.roomName);
            if (item.open) {
                tvstatus.setText("offen");
            } else {
                tvstatus.setText("geschlossen");
            }
            DateFormat df = new SimpleDateFormat("dd.MM.yy HH:mm");
            tvStartTime.setText("Von: "+df.format(item.startTime));
            tvEndTime.setText("Bis: "+df.format(item.endTime));
            tvLocation.setText(item.place +"\n" + item.address);
        }
    }
    // Todo: Service: TimeoutChecker

    private void onCloseRoom(View view) {
        long now = DateTime.now().getMillis();
        timeoutRefresherThread.stop();
        item.endTime = now;
        item.open = false;
        repo.update(item);
        tvtimeout.setText("00:00:00");
    }

    private void onChangeTimeout(View view) {
        int hour = 0, minute = 0;
        TimePickerDialog.OnTimeSetListener otsl = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                DateTime now = new DateTime();
                DateTime timeout = new DateTime(now.year().get(), now.monthOfYear().get(),
                        now.dayOfMonth().get(), i, i1, 0);
                item.endTime = timeout.getMillis();
                repo.update(item);
            }
        };
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, otsl, hour,
                minute, true);
        timePickerDialog.show();
    }

    private void onViewParticipants(View view) {
        Intent intent = new Intent(Activity_22_RoomHostDetail.this, Activity_23_HostViewParticipant.class);
        intent.putExtra(Activity_23_HostViewParticipant.INTENT_ROOM_ID, item.id);
        startActivity(intent);
    }

    //Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_22_room_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem menuitem) {
        //TODO Hier müssen noch die einzelnen Funktionen ergaenzt werden
        switch (menuitem.getItemId()) {
            case R.id.menu_partic_share:
                shareRoom(this.item);
                return true;
            case R.id.menu_partic_qr:
                Display display = getWindowManager().getDefaultDisplay();
                int breite = display.getWidth();
                Drawable draw = new BitmapDrawable(getQR(this.item.getUri(), (breite / 2), (breite / 2)));
                callAlertDialog_QR(draw);
                return true;
            case R.id.menu_partic_uri:
                callAlertDialog_URI();
                return true;
            default:
                return super.onOptionsItemSelected(menuitem);
        }
    }


    private Bitmap getQR(String msg, int hight, int width) {
        //Generate QR-code as bitmap
        Bitmap qrCode = null;
        QrCodeManger qrCodeManager = new QrCodeManger(this);
        try {
            qrCode = qrCodeManager.createQrCode(msg, width, hight);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return qrCode;
    }

    private void shareRoom(RoomItem item) {

        //generate PDF with qrCode an room infos -> saved in external file system
        PdfClass pdf = new PdfClass(Activity_22_RoomHostDetail.this);
        File file = pdf.createPdfRoomInfos(item, getQR(item.getUri(), PdfClass.A4_WIDTH / 2, PdfClass.A4_HEIGHT / 2));

        Log.v(TAG, "showPDF(" + file.getName() + ")");
        if (!file.exists()) {
            Toast.makeText(this, "Something wrong \n " + "file: " + file.getPath(), Toast.LENGTH_LONG).show();
            return;
        }
        //start share Intent
        try {
            Uri uri = FileProvider.getUriForFile(Activity_22_RoomHostDetail.this, BuildConfig.APPLICATION_ID + ".provider", file);
            Intent intent = ShareCompat.IntentBuilder.from(Activity_22_RoomHostDetail.this)
                    .setType(URLConnection.guessContentTypeFromName(file.getName()))
                    .setStream(uri)
                    .setChooserTitle("Choose bar")
                    .createChooserIntent()
                    .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            this.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(Activity_22_RoomHostDetail.this, "Something wrong \n " + "file: " + file.getPath(), Toast.LENGTH_LONG).show();
            Log.e(TAG, e.getMessage());
        }
    }

    public void callAlertDialog_QR(Drawable draw) {
        LayoutInflater qrDialogInflater = LayoutInflater.from(this);
        View view = qrDialogInflater.inflate(R.layout.pop_up_22_qr, null);

        TextView tvQrUri = view.findViewById(R.id.tv_qr_show_uri);
        ImageView ivQr = view.findViewById(R.id.qr_code_show);
        ivQr.setImageDrawable(draw);
        tvQrUri.setText("URI: " + item.getUri());

        AlertDialog alertDialogQR = new AlertDialog.Builder(this).setView(view).create();
        alertDialogQR.show();
    }

    public void callAlertDialog_URI() {
        LayoutInflater uriDialogInflater = LayoutInflater.from(this);
        View view = uriDialogInflater.inflate(R.layout.pop_up_22_uri, null);

        TextView tvUri = view.findViewById(R.id.tv_show_uri);
        tvUri.setText("URI: " + item.getUri());

        AlertDialog alertDialogUri = new AlertDialog.Builder(this).setView(view).create();
        alertDialogUri.show();
    }

}
