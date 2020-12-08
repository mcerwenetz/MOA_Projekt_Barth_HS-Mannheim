package de.mc.nfcdemo;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class NfcActivity extends AppCompatActivity {
    TextView tv;

    private static final String TAG = "NFC Activity" ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitynfc);
        tv = findViewById(R.id.tv);
        Intent intent = getIntent();
        if(NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())){
            Log.v(TAG, "New NFC Tag discovered");
            Parcelable[] rawMessanges=
                    intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if(rawMessanges!=null){
                NdefMessage[] messages = new NdefMessage[rawMessanges.length];
                for (int i = 0; i < rawMessanges.length; i++){
                    messages[i] = (NdefMessage) rawMessanges[i];
                }
                NdefRecord[] recs = messages[0].getRecords();
                String first = new String(recs[0].getPayload());
                tv.setText(first);
            }
        }
    }

}
