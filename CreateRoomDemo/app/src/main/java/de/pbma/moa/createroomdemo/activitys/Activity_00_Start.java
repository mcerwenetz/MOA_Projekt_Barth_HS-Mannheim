package de.pbma.moa.createroomdemo.activitys;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import de.pbma.moa.createroomdemo.R;
import de.pbma.moa.createroomdemo.database.Repository;
import de.pbma.moa.createroomdemo.preferences.MySelf;
import de.pbma.moa.createroomdemo.preferences.PreferenceActivity;
import de.pbma.moa.createroomdemo.service.MQTTService;
import de.pbma.moa.createroomdemo.service.RoomLivecycleService;

/**
 * Die MAIN Activity der App. Hier kann man auswählen ob man die App als Teilnehmer oder Host
 * nutzen möchte. Das eigene Profil kann bearbeitet werden. Die besuchten Räume können angezeigt
 * werden.
 */
public class Activity_00_Start extends AppCompatActivity {
    final static String TAG = Activity_00_Start.class.getCanonicalName();
    private Button btnHost, btnParticipant;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.v(TAG, "OnCreate");
        super.onCreate(savedInstanceState);
        this.setTitle("Kontaktverwaltung");
        setContentView(R.layout.page_00_start);

        btnHost = findViewById(R.id.btn_00_start_host);
        btnParticipant = findViewById(R.id.btn_00_start_participant);

        btnHost.setOnClickListener(Activity_00_Start.this::iAmHost);
        btnParticipant.setOnClickListener(Activity_00_Start.this::iAmParticipant);


//        TestKlassen um beim debuggen funktionalitäten testen zu können
//        TestClass test = new TestClass(this);
//        test.TestAdapterJsonMqtt();
//        test.addDBfremdRaum();

        //Anfangscheck ob Internet vorhanden
        if (!isConnected()) {
            Log.v(TAG, "Network not connected. Starting Network Error Page.");
            Intent networkErrorIntent = new Intent(this, Activity_000_NetworkError.class);
            networkErrorIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(networkErrorIntent);
        } else {
            Log.v(TAG, "Network connected. Starting MQTT-Service.");
            onStartMqttService();
            Intent intent = new Intent(this, RoomLivecycleService.class);
            startService(intent);
        }


        //remove DB entries older two weeks
        deleteOldEntries();
    }

    /**
     * Checkt ob das Device connected ist. Wird in der {@link #onCreate(Bundle)} aufgerufen. Wird dazu genutzt
     * um im Falle eines Netzwerkausfalls beim Starten der App eine Fehlermeldung anzuzeigen.
     * @return true wenn connected, false wenn nicht connected
     */
    private boolean isConnected() {
        boolean ret;
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Activity_00_Start.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            ret = true;
        } else
            ret = false;
        return ret;
    }

    /**
     * Bei onDestroy wird der RoomLiveCycleService beendet damit er nicht mehr läuft wenn die App
     * geschlossen wurde.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent(this, RoomLivecycleService.class);
        stopService(intent);
        onStopMqttService();
    }

    /**
     *
     * @param menu Menu das vom Framework übergeben wird
     * @return true wenn das Menu angezeigt werden soll. Übernimmt allerdings das Framework.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_00_start, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Wird gerufen sobald ein Item im Menü geklickt wurde. Je nachdem welcher Eintrag ausgewählt
     * wurde werden entweder die Settings oder die besuchten Räume gezeigt.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent;
        int itemid = item.getItemId();
        if (itemid == R.id.menu_start_pref) {
            Log.v(TAG, "onOptionsItemSelected() Settings");
            intent = new Intent(Activity_00_Start.this, PreferenceActivity.class);
            startActivity(intent);
            return true;
        } else if (itemid == R.id.menu_start_history) {
            Log.v(TAG, "onOptionsItemSelected() History");
            if (!checkSelf())
                return true;
            intent = new Intent(Activity_00_Start.this, Activity_10_RoomListVisited.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * OnButtonClickListener für den Button "Host". Ruft {@link #checkSelf()} auf und startet die Host
     * Activity.
     */
    private void iAmHost(View view) {
        Log.v(TAG, "iAmHost() clicked");
        if (!checkSelf())
            return;
        Intent intent = new Intent(Activity_00_Start.this, Activity_20_RoomListHost.class);
        startActivity(intent);
    }

    /**
     * OnButtonClickListener für den Button "Teilnehmer". Ruft {@link #checkSelf()} auf und startet
     * die Teilnehmer Activity.
     */
    private void iAmParticipant(View view) {
        Log.v(TAG, "iAmParticipant() clicked");
        if (!checkSelf())
            return;
        Intent intent = new Intent(Activity_00_Start.this, Activity_11_EnterViaQrNfc.class);
        startActivity(intent);
    }

    /**
     * Ruft {@link MySelf#isValide()} des Objektes MySelf auf. Diese checkt ob die eingetragenen personenbezogenen
     * Daten in MySelf korrekt eingetragen wurden. Falls nicht wird die PreferenceActivity gestartet
     * damit der User seine Daten nocheinmal eingeben muss.
     * @return true wenn die personenbezogenen Daten ok sind, false, wenn die personenbezogenen
     * Daten nicht ok sind.
     */
    private boolean checkSelf() {
        Log.v(TAG, "Check mySelfe()");
        MySelf me = new MySelf(Activity_00_Start.this);
        if (!me.isValide()) {
            Toast.makeText(this, R.string.fehlerhafte_settings, Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Activity_00_Start.this, PreferenceActivity.class);
            startActivity(intent);
            Log.v(TAG, "Check mySelf: false");
            return false;
        }
        Log.v(TAG, "Check mySelfe(): true");
        return true;
    }

    /**
     * Löscht alle Einträger in der Datenbank die älter als 14 Tage sind
     */
    private void deleteOldEntries() {
        Log.v(TAG, "Deleting Data > 14 Days");
        Repository repository = new Repository(this);
        repository.DeleteRoomAndParticipantOlderTwoWeeks();
    }

    /**
     * Startet den Mqtt Service
     */
    public void onStartMqttService() {
        Log.v(TAG, "starting mqtt service");
        Intent intent = new Intent(this, MQTTService.class);
        intent.setAction(MQTTService.ACTION_START);
        startService(intent);
    }

    /**
     * Stopp den MQTT Service
     */
    public void onStopMqttService() {
        Log.v(TAG, "stopping mqtt service");
        Intent intent = new Intent(this, MQTTService.class);
        intent.setAction(MQTTService.ACTION_STOP);
        startService(intent); // to stop
    }

}
