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

import java.io.File;
import java.net.URLConnection;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
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
                }
            });
        }
    }

    private void updateRoom(RoomItem item) {
        if (item != null) {
            tvroomname.setText(String.valueOf(roomid));
            tvstatus.setText(String.valueOf("offen"));
            tvtimeout.setText(String.valueOf(item.endTime));
        }
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
