package de.pbma.moa.createroomdemo.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import de.pbma.moa.createroomdemo.QrCodeManger;
import de.pbma.moa.createroomdemo.R;
import de.pbma.moa.createroomdemo.database.Repository;
import de.pbma.moa.createroomdemo.database.RoomItem;

public class Activity_11_EnterViaQrNfc extends AppCompatActivity {
    final static String TAG = Activity_11_EnterViaQrNfc.class.getCanonicalName();
    private Button btnNfc, btnQr;
    private String uri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "OnCreate");

        setContentView(R.layout.page_11_qr_nfc);
        btnNfc = findViewById(R.id.btn_11_nfc);
        btnQr = findViewById(R.id.btn_11_qr);

        btnQr.setOnClickListener(this::btnQrClicked);
        btnNfc.setOnClickListener(this::btnNfcClicked);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.v(TAG, "onActivityResult()");
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (scanningResult != null) {
            uri = scanningResult.getContents();
            enterRoom(uri);
            Log.v(TAG, "Scan successfully " + uri);
        } else {
            Log.v(TAG, "Scan failed");
        }
//        enterRoom();
    }

    private void btnQrClicked(View v) {
        QrCodeManger qrCodeManger = new QrCodeManger(this);
        qrCodeManger.callScanner();
    }

    //TODO @marius dein Spielplatz
    private void btnNfcClicked(View v) {

    }

    private void enterRoom(String uri) {
        //TODO und was hier halt dann mit MQTT noch so rein muss

        Repository repository = new Repository(Activity_11_EnterViaQrNfc.this);
        String[] lis = uri.split("/");
        RoomItem roomItem = RoomItem.createRoom(lis[0], false, null, lis[1], null, null, null, null, 0, 0);
        roomItem.fremdId = Long.parseLong(lis[2]);
        repository.addRoomEntry(roomItem, (newItem) -> {
            Activity_11_EnterViaQrNfc.this.runOnUiThread(() -> {
                Intent intent = new Intent(Activity_11_EnterViaQrNfc.this, Activity_14_RoomParticipantDetail.class);
                intent.putExtra(Activity_14_RoomParticipantDetail.ID,newItem.id);
                startActivity(intent);
                finish();
            });
        });
    }
}
