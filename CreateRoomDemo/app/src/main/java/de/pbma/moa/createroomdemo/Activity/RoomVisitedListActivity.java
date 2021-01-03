package de.pbma.moa.createroomdemo.Activity;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import de.pbma.moa.createroomdemo.R;

public class RoomVisitedListActivity extends AppCompatActivity {
    final static String TAG = RoomVisitedListActivity.class.getCanonicalName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "OnCreated RoomVisitedListActivity");
        setContentView(R.layout.page_roomlist);

        //Todo nochmal abklären was jetzt hier genau hin muss


    }
}
