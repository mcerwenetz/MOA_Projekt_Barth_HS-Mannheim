package de.pbma.moa.createroomdemo.Activity;

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

//    private void enterRoom(){
//        //und was hier halt dann mit MQTT noch so rein muss
//        Intent intent = new Intent(EnterViaQrNfcActivity.this,/*TODO*/.this);
//        startActivity(intent);
//    }
}
