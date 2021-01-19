package de.pbma.moa.createroomdemo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.pbma.moa.createroomdemo.database.ParticipantItem;
import de.pbma.moa.createroomdemo.database.RoomItem;
import de.pbma.moa.createroomdemo.preferences.MySelf;

public class AdapterJsonMqtt {

    public static final String TEILNEHMER = "teilnehmer";
    public static final String ENTERTIME = "entertime";
    public static final String TYPE = "type";
    public static final String EXITTIME = "exittime";
    public static final String TEILNEHMERLIST = "teilnehmerlist";
    public static final String RAUM = "raum";

    public static JSONObject getAnmeldungJSON(MySelf teilnehmer, Long entertime) {
        JSONObject ret = new JSONObject();
        JSONObject teilnehmerAsJSON = getJSONMySelf(teilnehmer);
        try {
            ret.put(TYPE, JSONTypes.LOGIN);
            ret.put(TEILNEHMER, teilnehmerAsJSON);
            ret.put(ENTERTIME, entertime);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public static JSONObject getAbmeldungJSON(MySelf teilnehmer, Long exittime) {
        JSONObject ret = new JSONObject();
        JSONObject teilnehmerAsJSON = getJSONMySelf(teilnehmer);
        try {
            ret.put(TYPE, JSONTypes.LOGOUT);
            ret.put(TEILNEHMER, teilnehmerAsJSON);
            ret.put(EXITTIME, exittime);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ret;

    }

    //Funktion holt sich die Inhalte eines Teilnehmers aus den Myself Angaben
    private static JSONObject getJSONMySelf(MySelf teilnehmer) {
        JSONObject ret = new JSONObject();
        String name = teilnehmer.getFirstName() + " " + teilnehmer.getName();
        try {
            ret.put(JSONItemTypes.NAME.label, teilnehmer.getName());
            ret.put(JSONItemTypes.EXTRA.label, teilnehmer.getExtra());
            ret.put(JSONItemTypes.EMAIL.label, teilnehmer.getEmail());
            ret.put(JSONItemTypes.PHONE.label, teilnehmer.getPhone());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public static JSONObject getTeilnehmerListJSON(List<ParticipantItem> participants) {
        JSONArray teilnehmerliste = getTeilnehmerliste(participants);
        JSONObject ret = new JSONObject();
        try {
            ret.put(TYPE, JSONTypes.TEILNEHMER);
            ret.put(TEILNEHMERLIST, teilnehmerliste);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public static JSONArray getTeilnehmerliste(List<ParticipantItem> participants) {
        JSONArray teilnehmerliste = new JSONArray();
        for (ParticipantItem participant : participants) {
            teilnehmerliste.put(getJSONParticipant(participant));
        }
        return teilnehmerliste;
    }

    private static JSONObject getJSONParticipant(ParticipantItem participantItem) {
        JSONObject ret = new JSONObject();
        try {
            ret.put(JSONItemTypes.NAME.label, participantItem.name);
            ret.put(JSONItemTypes.EXTRA.label, participantItem.extra);
            ret.put(JSONItemTypes.EMAIL.label, participantItem.eMail);
            ret.put(JSONItemTypes.PHONE.label, participantItem.phone);
            ret.put(JSONItemTypes.ENTERTIME.label, participantItem.enterTime);
            ret.put(JSONItemTypes.EXITTIME.label, participantItem.exitTime);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public static JSONObject getRauminfoJSON(RoomItem roomItem) {
        JSONObject rauminfoJSON = new JSONObject();
        try {
            rauminfoJSON.put(TYPE, JSONTypes.RAUMINFO);
            rauminfoJSON.put(RAUM, getRoomJSON(roomItem));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return rauminfoJSON;
    }

    private static JSONObject getRoomJSON(RoomItem roomItem) {
        JSONObject ret = new JSONObject();
        try {
            ret.put(JSONItemTypes.ID.label, roomItem.id);
            ret.put(JSONItemTypes.ROOMNAME.label, roomItem.roomName);
            ret.put(JSONItemTypes.OPEN.label, roomItem.open);
            ret.put(JSONItemTypes.HOST.label, roomItem.host);
            ret.put(JSONItemTypes.EMAIL.label, roomItem.eMail);
            ret.put(JSONItemTypes.PHONE.label, roomItem.phone);
            ret.put(JSONItemTypes.PLACE.label, roomItem.place);
            ret.put(JSONItemTypes.ADDRESS.label, roomItem.address);
            ret.put(JSONItemTypes.EXTRA.label, roomItem.extra);
            ret.put(JSONItemTypes.ROOMSTARTTIME.label, roomItem.startTime);
            ret.put(JSONItemTypes.ROOMENDTIME.label, roomItem.endTime);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public static RoomItem createRoomItem(JSONObject jsonObject) {
        RoomItem roomItem = new RoomItem();

        try {
            String roomName = (String) jsonObject.get(JSONItemTypes.ROOMNAME.label);
            boolean open = (boolean) jsonObject.get(JSONItemTypes.OPEN.label);
            String host = (String) jsonObject.get(JSONItemTypes.HOST.label);
            String eMail = (String) jsonObject.get(JSONItemTypes.EMAIL.label);
            String phone = (String) jsonObject.get(JSONItemTypes.PHONE.label);
            String place = (String) jsonObject.get(JSONItemTypes.PLACE.label);
            String address = (String) jsonObject.get(JSONItemTypes.ADDRESS.label);
            String extra = (String) jsonObject.get(JSONItemTypes.EXTRA.label);
            long startTime = (long) jsonObject.get(JSONItemTypes.ROOMSTARTTIME.label);
            long endTime = (long) jsonObject.get(JSONItemTypes.ROOMENDTIME.label);

            roomItem = roomItem.createRoom(roomName, open, host, eMail, phone, place, address, extra, startTime, endTime);
            roomItem.fremdId = (long) jsonObject.get(JSONItemTypes.ID.label);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return roomItem;
    }

    public static ParticipantItem createParticipantItem(JSONObject jsonObject) {
        ParticipantItem participantItem = new ParticipantItem();
        try {
            String name = jsonObject.getString(JSONItemTypes.NAME.label);
            String extra = jsonObject.getString(JSONItemTypes.EXTRA.label);
            String eMail = jsonObject.getString(JSONItemTypes.EMAIL.label);
            String phone = jsonObject.getString(JSONItemTypes.PHONE.label);

//            long enterTime = (long) jsonObject.get("enterTime");
//            long exitTime = (long) jsonObject.get("exitTime");
            participantItem = participantItem.createParticipant(name, extra, eMail, phone, 0, 0);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return participantItem;
    }

    public static ArrayList<ParticipantItem> createParticipantItemList(JSONArray jsonArray) {
        ArrayList<ParticipantItem> participantList = new ArrayList<>();

//        Iterator<JSONObject> objectIterator = jsonArray.iterator();
//
//        //Fuer  ein dimensionales
//        while(objectIterator.hasNext()){
//            participantList.add(createParticipantItem(objectIterator.next()));
//
//        }
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                participantList.add(createParticipantItem(jsonArray.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return participantList;
    }

    //JasonTYpes koennen diese vier Types annehmen
    private enum JSONTypes {
        LOGIN("login"),
        LOGOUT("logout"),
        TEILNEHMER("teilnehmer"),
        RAUMINFO("rauminfo");
        public final String label;

        JSONTypes(String label) {
            this.label = label;
        }
    }

    //JasonTYpes koennen diese vier Types annehmen
    private enum JSONItemTypes {
        ID("ID"),
        NAME("NAME"),
        ROOMNAME("ROOMNAME"),
        OPEN("OPEN"),
        HOST("HOST"),
        EMAIL("EMAIL"),
        PHONE("PHONE"),
        PLACE("PLACE"),
        ADDRESS("ADDRESS"),
        EXTRA("EXTRA"),
        ROOMSTARTTIME("ROOMSTARTTIME"),
        ROOMENDTIME("ROOMENDTIME"),
        ENTERTIME("ENTERTIME"),
        EXITTIME("EXITTIME");
        public final String label;

        JSONItemTypes(String label) {
            this.label = label;
        }
    }
}


