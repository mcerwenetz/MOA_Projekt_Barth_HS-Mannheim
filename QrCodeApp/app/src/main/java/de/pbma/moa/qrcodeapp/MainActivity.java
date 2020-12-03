package de.pbma.moa.qrcodeapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class MainActivity extends AppCompatActivity {
    final static String TAG = MainActivity.class.getCanonicalName();
    Button button;
    ImageView imageView;
    TextView textView;
    String qrData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "OnCreate");

        setContentView(R.layout.main_layout);
        textView = findViewById(R.id.tv_2);
        imageView = findViewById(R.id.im_1);
        button = findViewById(R.id.btn_1);
        button.setOnClickListener(this::callScanner);
    }

    private void callScanner(View view) {
        Log.v(TAG, "callScanner()");
        try {
            IntentIntegrator scanIntegrator = new IntentIntegrator(this);
            scanIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
            scanIntegrator.setPrompt("");
            //scanIntegrator.setTorchEnabled(true); //Flashlight
            scanIntegrator.setBeepEnabled(true);
            scanIntegrator.setOrientationLocked(true);
            scanIntegrator.setBarcodeImageEnabled(true);
            scanIntegrator.setTimeout(10000);   //10ec
            scanIntegrator.initiateScan();
        } catch (Exception e) {
            Log.v(TAG, "callScanner() " + e.getMessage());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.v(TAG, "onActivityResult()");
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (scanningResult != null) {
            qrData = scanningResult.getContents();
            Log.v(TAG, "Scan successfully " + qrData);
            textView.setText(qrData);
            createQrCode();
        } else {
            Log.v(TAG, "Scan failed");
        }
    }

    private void createQrCode() {
        Log.v(TAG, "createQrCode()");
        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
        try {
            Bitmap bitmap = barcodeEncoder.encodeBitmap(qrData, BarcodeFormat.QR_CODE, 400, 400);
            imageView.setImageBitmap(bitmap);
            Log.v(TAG, "QrCode drawn ");
        } catch (WriterException e) {
            Log.v(TAG, "createQrCode() " + e.getMessage());
            return;
        }

    }
}
