package de.pbma.moa.createroomdemo.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.room.Room;
import androidx.room.Update;

import com.google.zxing.WriterException;

import java.io.File;
import java.net.URLConnection;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import de.pbma.moa.createroomdemo.BuildConfig;
import de.pbma.moa.createroomdemo.PdfClass;
import de.pbma.moa.createroomdemo.QrCodeManger;
import de.pbma.moa.createroomdemo.R;
import de.pbma.moa.createroomdemo.RoomParticipant.ParticipantRepository;
import de.pbma.moa.createroomdemo.RoomRoom.RoomDao;
import de.pbma.moa.createroomdemo.RoomRoom.RoomItem;
import de.pbma.moa.createroomdemo.RoomRoom.RoomRepository;

public class ParticipantViewActivity extends AppCompatActivity {
    final static String TAG = ParticipantViewActivity.class.getCanonicalName();
    final static String ID = "RoomID";

    private TextView tvtimeout, tvstatus, tvroomname;
    private Button btnopen, btntimeout, btnpartic;

    private RoomItem item;
    private RoomRepository repo;
    private LiveData<RoomItem> liveData;
    long roomid;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "onCreated_Teilnehmer_Uebersicht");
        setContentView(R.layout.page_participant_dataview);

        tvroomname  = findViewById(R.id.tv_view_partic_roomname);
        tvstatus    = findViewById(R.id.tv_view_partic_statustext);
        tvtimeout   = findViewById(R.id.tv_view_partic_timeouttext);

        btnopen     = findViewById(R.id.btn_view_partic_open);
        btntimeout  = findViewById(R.id.btn_view_partic_timechange);
        btnpartic   = findViewById(R.id.btn_view_partic_particlist);

        //Holt die Daten aus der Bank
        try{
            repo        = new RoomRepository(this);
            roomid      = getIntent().getExtras().getLong(ID, -1);
            if(roomid != -1){
                liveData    = repo.getID(roomid);
                liveData.observe(this, new Observer<RoomItem>() {
                    @Override
                    public void onChanged(RoomItem roomItem) {
                        updateRoom(roomItem);
                    }
                });

            }
        }catch (Exception e){
            Log.v(TAG,"Das ist nicht gut gegangen");
        };
    }

    private void updateRoom(RoomItem item){
        if(item != null){
            tvroomname.setText(String.valueOf(roomid));
            tvstatus.setText(String.valueOf("offen"));
            tvtimeout.setText(String.valueOf(item.endTime));
        }
    }

    private void setBtnopen(View view){

    }

    private void setBtntimeout(View view){

    }

    private void setBtnpartic(View view){

    }

    //Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.participant_dataview_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem menuitem) {
        //TODO Hier müssen noch die einzelnen Funktionen ergaenzt werden
        switch(menuitem.getItemId()){
            case R.id.menu_partic_share:
                test(this.item);
                return true;
            case R.id.menu_partic_qr:
                return true;
            case R.id.menu_partic_uri:
                return true;
            default:
                return super.onOptionsItemSelected(menuitem);
        }
    }

    //TODO function test und sharePDF  muss später in RaumDetailsActivity (hier nur testzweck und beispiel hafte verwendung)
    void test(RoomItem item) {
        //Generate QR-code as bitmap
        QrCodeManger qrCodeManager = new QrCodeManger(this);
        Bitmap qrCode = null;
        try {
            qrCode = qrCodeManager.createQrCode(item.getUri());
        } catch (WriterException e) {
            e.printStackTrace();
        }

        //generate PDF with qrCode an room infos -> saved in external file system
        PdfClass pdf = new PdfClass(this);
        File file = pdf.createPdfRoomInfos(item, qrCode);

        // pdf teilen
        if (sharePDF(file, this))
            Toast.makeText(this, "Something wrong \n " + "file: " + file.getPath(), Toast.LENGTH_LONG).show();
    }

    public boolean sharePDF(File file, Context context) {
        Log.v(TAG, "showPDF(" + file.getName() + ")");
        if (!file.exists())
            return false;
        try {
            Uri uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file);
            Intent intent = ShareCompat.IntentBuilder.from((Activity) context)
                    .setType(URLConnection.guessContentTypeFromName(file.getName()))
                    .setStream(uri)
                    .setChooserTitle("Choose bar")
                    .createChooserIntent()
                    .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return false;
        }
        return true;
    }
}
