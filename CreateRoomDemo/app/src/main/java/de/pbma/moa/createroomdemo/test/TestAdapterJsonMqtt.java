package de.pbma.moa.createroomdemo.test;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.pbma.moa.createroomdemo.AdapterJsonMqtt;
import de.pbma.moa.createroomdemo.database.ParticipantItem;
import de.pbma.moa.createroomdemo.database.RoomItem;
import de.pbma.moa.createroomdemo.preferences.MySelf;

public class TestAdapterJsonMqtt {
    public TestAdapterJsonMqtt(Context context) {
        //test Adapter
        JSONObject jsonObject;
        List<ParticipantItem> list = new ArrayList<ParticipantItem>();
        ParticipantItem participantItem;
        RoomItem roomItem;
        MySelf me = new MySelf(context);

        try {
            jsonObject = AdapterJsonMqtt.getAbmeldungJSON(me, System.currentTimeMillis());
            participantItem = AdapterJsonMqtt.createParticipantItem(jsonObject.getJSONObject(AdapterJsonMqtt.TEILNEHMER));


            participantItem = null;
            jsonObject = AdapterJsonMqtt.getAnmeldungJSON(me, System.currentTimeMillis());
            participantItem = AdapterJsonMqtt.createParticipantItem(jsonObject.getJSONObject(AdapterJsonMqtt.TEILNEHMER));
        } catch (JSONException e) {
            e.printStackTrace();
        }


        jsonObject = AdapterJsonMqtt.getRauminfoJSON(RoomItem.createRoom(
                "Test",
                false,
                "Raphael Barth",
                "test@moagm.de",
                "123456789",
                "22 Mannheim",
                "test22",
                "Raum S12",
                123,
                1234));

        try {
            roomItem = AdapterJsonMqtt.createRoomItem(jsonObject.getJSONObject(AdapterJsonMqtt.RAUM));
        } catch (JSONException e) {
            e.printStackTrace();
        }


        participantItem = ParticipantItem.createParticipant(
                "Barth",
                "123456",
                "good@failed.org",
                "123456789",
                123,
                System.currentTimeMillis());

        for (int i = 0; i < 10; i++) {
            list.add(participantItem);
        }
        jsonObject = AdapterJsonMqtt.getTeilnehmerListJSON(list);
        try {
            list = null;
            list = AdapterJsonMqtt.createParticipantItemList(jsonObject.getJSONArray(AdapterJsonMqtt.TEILNEHMERLIST));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
