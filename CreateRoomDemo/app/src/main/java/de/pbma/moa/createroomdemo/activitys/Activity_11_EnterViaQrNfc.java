package de.pbma.moa.createroomdemo.activitys;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.tech.NfcF;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
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
import de.pbma.moa.createroomdemo.preferences.MySelf;
import de.pbma.moa.createroomdemo.service.MQTTService;

public class Activity_11_EnterViaQrNfc extends AppCompatActivity {
    final static String TAG = Activity_11_EnterViaQrNfc.class.getCanonicalName();
    public static final String NFC_INTENT_ACTION = "NEW_ROOM_TAG_DISCOVERED";
    private String toSend;
    private boolean mqttServiceBound;
    private MQTTService mqttService;
    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.v(TAG, "onServiceConnected");
            mqttService = ((MQTTService.LocalBinder) service).getMQTTService();
            if (toSend != null)
                enterRoom(toSend);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // unintentionally disconnected
            Log.v(TAG, "onServiceDisconnected");
            unbindMQTTService(); // cleanup
        }
    };

    /**
     * Bindet dem MQTT service
     * Loggt, falls er nicht gebunden werden konnte.
     * Wird bei enterRoomIfMqttAvailable() gerufen
     */
    private void bindMQTTService() {
        Log.v(TAG, "bindMQTTService");
        Intent intent = new Intent(this, MQTTService.class);
        intent.setAction(MQTTService.ACTION_PRESS);
        mqttServiceBound = bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        if (!mqttServiceBound) {
            Log.w(TAG, "could not try to bind service, will not be bound");
        }
    }

    /**
     * Löst die Verbindung zum MQTT, falls er gebunden war
     * Wird bei onPause() und onServiceDisconnected() aufgerufen.
     */
    private void unbindMQTTService() {
        Log.v(TAG, "unbindMQTTService");
        if (mqttServiceBound) {
            mqttServiceBound = false;
            unbindService(serviceConnection);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "OnCreate");
        //Aendern des Activity Titels
        this.setTitle("Treten Sie per QR oder NFC bei");

        setContentView(R.layout.page_11_qr_nfc);
        Button btnNfc = findViewById(R.id.btn_11_nfc);
        Button btnQr = findViewById(R.id.btn_11_qr);

        btnQr.setOnClickListener(this::btnQrClicked);
        btnNfc.setOnClickListener(this::btnNfcClicked);
        mqttServiceBound = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        String action = intent.getAction();
        if(action != null){
            if(intent.getAction().equals(NFC_INTENT_ACTION)) {
                enterRoomViaNFC(intent);
            }
        }
    }

    /**
     * Wird gerufen wenn die App pausiert wird.
     * Löst die Verbindung zum MQTT Service.
     */
    @Override
    protected void onPause() {
        unbindMQTTService();
        super.onPause();
    }

    /**
     * Diese Methode ruft enter room auf und fängt ab dass der mqtt service noch nicht gebunden
     * ist
     * @param roomtag Das ist der roomtag über den der Raum betreten wird
     */
    private void enterRoomIfMqttAvailable(String roomtag) {
        bindMQTTService();
        if (mqttService == null)
            toSend = roomtag;
        else {
            enterRoom(roomtag);
        }
    }

    /**
     * Wird gerufen, falls der QR Button gedrückt wurde.<br>
     * Der QR-Code-Scanner wird gestartet. Erkennt er einen Code wird onActivityResult() ausgeführt
     * und der Raum betreten.
     */
    private void btnQrClicked(View v) {
        QrCodeManger qrCodeManger = new QrCodeManger(this);
        qrCodeManger.callScanner();
    }

    /**
     * Wird gerufen, falls der QR Scanner erfolgreich war und einen Code erkannt hat.<br><br>
     * Der Roomtag steht dann im Code.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.v(TAG, "onActivityResult()");
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (scanningResult != null) {
            String scanResult = scanningResult.getContents();
            enterRoomIfMqttAvailable(scanResult);
            Log.v(TAG, "Scan successfully " + scanResult);
        } else {
            Log.v(TAG, "Scan failed");
        }
//        enterRoom();
    }
    /**
     * Wird gerufen, falls der NFC Button gedrückt wurde.<br>
     * Ruft armNFCAdapter() auf und schaltet dadurch den NFC Adapter scharf. Erkennt dieser ein
     * TAG schreibt er die Records auf dem Tag in einen neuen Intent der diese Activity nocheinmal
     * startet. In onResume werden der erste aller Records verwendet um den Raum zu betreten.
     */
    private void btnNfcClicked(View v) {
        armNFCAdapter();
//        displayAlertDialog();
    }

    /**
     * Wird gerufen, falls der NFC Button gedrückt wurde.<br><br>
     *
     * Schaltet den NFC Adapter scharf auf NFC Tags für die Record-Mime-Types: "text/plain".
     * Dadurch ist das Betreten eines Raumes über NFC nicht von jeder App aus möglich, da der
     * Intent-Filter nicht im Manifest statisch drinstehen muss.<br><br>
     *
     * Wird ein NFC-Tag erkannt, wird die aktuelle Activity mit einem Intent neugestartet
     * in dem als Action "NEW_ROOM_TAG_DISCOVERED" steht. In onResume() wird das abgefangen und
     * die erkannten Records werden aus dem Intent ausgelesen.
     */
    private void armNFCAdapter() {
        NfcAdapter adapter = NfcAdapter.getDefaultAdapter(this);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0,
                        new Intent(this, this.getClass()).setAction(NFC_INTENT_ACTION), 0);

        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        try {
            ndef.addDataType("text/plain");
        } catch (IntentFilter.MalformedMimeTypeException e) {
            e.printStackTrace();
        }
        IntentFilter[] intentFilters = new IntentFilter[]{
                ndef,
        };
        String[][] tech = new String[][]{
                new String[]{
                        NfcF.class.getName()
                }
        };

        adapter.enableForegroundDispatch(this, pendingIntent, intentFilters, tech);
    }

    /**
     * Wird gerufen wenn ein Intent mit der Action NEW_ROOM_TAG_DISCOVERED die aktuelle Activity
     * gestartet hat.<br><br>
     * Dann wurde ein NFC Tag discovered auf dem ein Raumtag steht. In diesem Intent sind alle
     * Records die auf dem NFC Tag waren zu finden.
     * Der erste dieser Records wird benutzt um den Raum zu betreten.
     * @param intent - Der Intent in dem die Records stehen.
     */
    private void enterRoomViaNFC(Intent intent) {
        Parcelable[] rawMessages =
                intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        if (rawMessages != null) {
            NdefMessage[] messages = new NdefMessage[rawMessages.length];
            for (int i = 0; i < rawMessages.length; i++) {
                messages[i] = (NdefMessage) rawMessages[i];
            }
            NdefRecord[] recs = messages[0].getRecords();
            String roomtag = new String(recs[0].getPayload());
            roomtag = roomtag.substring(3);
            enterRoomIfMqttAvailable(roomtag);
        }
    }

    /**
     * Startet die Raumteilnahme des Participants.
     * @param roomtag Der Tag der als eindeutiger Identifier für den Raum dient.
     */
    private void enterRoom(String roomtag) {
        //enter room via mqtt
        mqttService.sendEnterRoom(new MySelf(this), roomtag);
        //add room to repo and enter details page
        Repository repository = new Repository(Activity_11_EnterViaQrNfc.this);
        String[] lis = roomtag.split("/");
        RoomItem roomItem = RoomItem.createRoom(lis[0], null, lis[1], null, null, null, null, 0, 0);
        roomItem.fremdId = Long.parseLong(lis[2]);
        repository.addRoomEntry(roomItem, (newItem) -> {
            Activity_11_EnterViaQrNfc.this.runOnUiThread(() -> {
                Intent intent = new Intent(Activity_11_EnterViaQrNfc.this, Activity_14_RoomParticipantDetail.class);
                intent.putExtra(Activity_14_RoomParticipantDetail.ID, newItem.id);
                startActivity(intent);
                finish();
            });
        });
    }

}
