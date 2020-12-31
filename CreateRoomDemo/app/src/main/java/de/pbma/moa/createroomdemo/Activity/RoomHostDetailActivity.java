package de.pbma.moa.createroomdemo.Activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
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
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.io.File;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicBoolean;

import de.pbma.moa.createroomdemo.BuildConfig;
import de.pbma.moa.createroomdemo.PdfClass;
import de.pbma.moa.createroomdemo.QrCodeManger;
import de.pbma.moa.createroomdemo.R;
import de.pbma.moa.createroomdemo.RoomRoom.RoomItem;
import de.pbma.moa.createroomdemo.RoomRoom.RoomRepository;

public class RoomHostDetailActivity extends AppCompatActivity {
    final static String TAG = RoomHostDetailActivity.class.getCanonicalName();
    final static String ID = "RoomID";
    long roomid;
    private TextView tvtimeout, tvstatus, tvroomname;
    private Button btnopen, btntimeout, btnpartic;
    private RoomItem item;
    private RoomRepository repo;
    private LiveData<RoomItem> liveData;
    private AtomicBoolean timeOutUpdaterThreadAlreadyRunning;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        timeOutUpdaterThreadAlreadyRunning = new AtomicBoolean(false);
        super.onCreate(savedInstanceState);
        Log.v(TAG, "onCreated_Teilnehmer_Uebersicht");
        setContentView(R.layout.page_room_host_detail_activity);
        tvroomname = findViewById(R.id.tv_view_partic_roomname);
        tvstatus = findViewById(R.id.tv_view_partic_statustext);
        tvtimeout = findViewById(R.id.tv_view_partic_timeouttext);
        btnopen = findViewById(R.id.btn_view_partic_open);
        btntimeout = findViewById(R.id.btn_view_partic_timechange);
        btnpartic = findViewById(R.id.btn_view_partic_particlist);

        btnpartic.setOnClickListener(this::setBtnpartic);

        //Holt die Daten aus der Bank
        repo = new RoomRepository(this);
        roomid = getIntent().getExtras().getLong(ID, -1);
        if (roomid != -1) {
            liveData = repo.getID(roomid);
            liveData.observe(this, new Observer<RoomItem>() {
                @Override
                public void onChanged(RoomItem roomItem) {
                    updateRoom(roomItem);
                    RoomHostDetailActivity.this.item = roomItem;
                    startTimeOutRefresherThread();
                }
            });
        }
    }
//Todo: Thread wird (warhscheinlich) nie beendet. Das ist nicht effizient.
    private void startTimeOutRefresherThread(){
        if(!timeOutUpdaterThreadAlreadyRunning.get()) {
            Thread t = new Thread() {
                final long endTime = item.endTime;

                @Override
                public void run() {
                    super.run();
                    while (true) {
                        RoomHostDetailActivity.this.runOnUiThread(() -> tvtimeout.setText(formatTimeOut(endTime)));
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            t.start();
            timeOutUpdaterThreadAlreadyRunning.set(true);
        }
    }

    private void updateRoom(RoomItem item) {
        if (item != null) {
            tvroomname.setText(String.valueOf(roomid));
            tvstatus.setText("offen");
            String timeOutAsString = formatTimeOut(item.endTime);
            tvtimeout.setText(timeOutAsString);
        }
    }
// Todo: check ob timeout abgelaufen. Besser als Service wahrscheinlich.
    private String formatTimeOut(long endtime){
        DateTime now = new DateTime();
        DateTime endTimeDateTime = new DateTime(endtime);
        Period period = new Period(now, endTimeDateTime);
        PeriodFormatter formatter = new PeriodFormatterBuilder()
                .appendDays().appendSuffix("d ")
                .appendHours().appendSuffix("h ")
                .appendMinutes().appendSuffix("m ")
                .appendSeconds().appendSuffix("s ")
                .printZeroNever()
                .toFormatter();
        return formatter.print(period);
    }

    //TODO muessen noch gesetzt werden
    private void setBtnopen(View view) {

    }

    private void setBtntimeout(View view) {

    }

    private void setBtnpartic(View view) {
        Intent intent = new Intent(RoomHostDetailActivity.this,ParticipantHostActivity.class);
        intent.putExtra(ParticipantHostActivity.INTENT_ROOM_ID,item.id);
        startActivity(intent);
    }

    //Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.room_host_detail_activity_menu, menu);
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
                Drawable draw = new BitmapDrawable(getQR(this.item.getUri()));
                callAlertDialog_QR(draw);
                return true;
            case R.id.menu_partic_uri:
                callAlertDialog_URI();
                return true;
            default:
                return super.onOptionsItemSelected(menuitem);
        }
    }


    private Bitmap getQR(String msg,int hight,int width) {
        //Generate QR-code as bitmap
        Bitmap qrCode = null;
        QrCodeManger qrCodeManager = new QrCodeManger(this);
        try {
            qrCode = qrCodeManager.createQrCode(msg,width,hight);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return qrCode;
    }

    private void shareRoom(RoomItem item) {

        //generate PDF with qrCode an room infos -> saved in external file system
        PdfClass pdf = new PdfClass(RoomHostDetailActivity.this);
        File file = pdf.createPdfRoomInfos(item, getQR(item.getUri(),(int)(PdfClass.A4_WIDTH/2),(int)(PdfClass.A4_HEIGHT/2)));

        Log.v(TAG, "showPDF(" + file.getName() + ")");
        if (!file.exists()) {
            Toast.makeText(this, "Something wrong \n " + "file: " + file.getPath(), Toast.LENGTH_LONG).show();
            return;
        }
        //start share Intent
        try {
            Uri uri = FileProvider.getUriForFile(RoomHostDetailActivity.this, BuildConfig.APPLICATION_ID + ".provider", file);
            Intent intent = ShareCompat.IntentBuilder.from(RoomHostDetailActivity.this)
                    .setType(URLConnection.guessContentTypeFromName(file.getName()))
                    .setStream(uri)
                    .setChooserTitle("Choose bar")
                    .createChooserIntent()
                    .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            this.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(RoomHostDetailActivity.this, "Something wrong \n " + "file: " + file.getPath(), Toast.LENGTH_LONG).show();
            Log.e(TAG, e.getMessage());
        }
    }

    public void callAlertDialog_QR(Drawable draw){
        LayoutInflater qrDialogInflater = LayoutInflater.from(this);
        View view = qrDialogInflater.inflate(R.layout.qr_pop_up, null);

        TextView tvQrUri = view.findViewById(R.id.tv_qr_show_uri);
        ImageView ivQr   = view.findViewById(R.id.qr_code_show);
        ivQr.setImageDrawable(draw);
        tvQrUri.setText("URI: "+item.getUri());

        AlertDialog alertDialogQR = new AlertDialog.Builder(this).setView(view).create();
        alertDialogQR.show();
    }

    public void callAlertDialog_URI(){
        LayoutInflater uriDialogInflater = LayoutInflater.from(this);
        View view = uriDialogInflater.inflate(R.layout.uri_pop_up, null);

        TextView tvUri = view.findViewById(R.id.tv_show_uri);
        tvUri.setText("URI: "+item.getUri());

        AlertDialog alertDialogUri = new AlertDialog.Builder(this).setView(view).create();
        alertDialogUri.show();
    }

}
