package de.pbma.moa.createroomdemo;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.integration.android.IntentIntegrator;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class QrCodeManger {

    final static String TAG = QrCodeManger.class.getCanonicalName();
    private Context context;

    public QrCodeManger(Context context) {
        this.context = context;

    }

    public void callScanner(View view) {
        Log.v(TAG, "callScanner()");
        try {
            IntentIntegrator scanIntegrator = new IntentIntegrator((Activity)context);
            scanIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
            scanIntegrator.setPrompt("");
            //scanIntegrator.setTorchEnabled(true); //Flashlight  //TODO optional enable via light sensor ??
            scanIntegrator.setBeepEnabled(true);
            scanIntegrator.setOrientationLocked(true);
            scanIntegrator.setBarcodeImageEnabled(true);
            scanIntegrator.setTimeout(10000);   //10ec
            scanIntegrator.initiateScan();
        } catch (Exception e) {
            Log.v(TAG, "callScanner() " + e.getMessage());
        }
    }
//TODO daten an die Activity weiter leiten / onReslutfixen

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        Log.v(TAG, "onActivityResult()");
//        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
//        if (scanningResult != null) {
//            String qrData = scanningResult.getContents();
//            Log.v(TAG, "Scan successfully " + qrData);
//        } else {
//            Log.v(TAG, "Scan failed");
//        }
//    }

    public Bitmap createQrCode(String message) throws WriterException {
        Log.v(TAG, "createQrCode()");
        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
        return barcodeEncoder.encodeBitmap(message, BarcodeFormat.QR_CODE, 600, 600);
    }
}
