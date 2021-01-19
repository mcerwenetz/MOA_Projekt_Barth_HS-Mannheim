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
            ret.put("Name", teilnehmer.getName());
            ret.put("extra", teilnehmer.getExtra());
            ret.put("eMail", teilnehmer.getEmail());
            ret.put("phone", teilnehmer.getPhone());
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
//        Map<Object, String> teilnehmermap = populateMap(participantItem);
//        return new JSONObject(teilnehmermap);
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
            ret.put("id",roomItem.id);
            ret.put("roomName",roomItem.roomName);
            ret.put("open",roomItem.open);
            ret.put("host", roomItem.host);
            ret.put("eMail",roomItem.eMail);
            ret.put("phone", roomItem.phone);
            ret.put("place", roomItem.place);
            ret.put("address",roomItem.address);
            ret.put("extra",roomItem.extra);
            ret.put("startTime",roomItem.startTime);
            ret.put("endTime", roomItem.endTime);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  ret;
    }

    public static RoomItem createRoomItem(JSONObject jsonObject) {
        RoomItem roomItem = new RoomItem();

        try {
            String roomName = (String) jsonObject.get("roomName");
            boolean open = (boolean) jsonObject.get("open");
            String host = (String) jsonObject.get("host");
            String eMail = (String) jsonObject.get("eMail");
            String phone = (String) jsonObject.get("phone");
            String place = (String) jsonObject.get("place");
            String address = (String) jsonObject.get("address");
            String extra = (String) jsonObject.get("extra");
            long startTime = (long) jsonObject.get("startTime");
            long endTime = (long) jsonObject.get("endTime");

            roomItem.fremdId = (long) jsonObject.get("Id");
            roomItem.createRoom(roomName, open, host, eMail, phone, place, address, extra, startTime, endTime);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return roomItem;
    }

    public static ParticipantItem createParticipantItem(JSONObject jsonObject) {
        ParticipantItem participantItem = new ParticipantItem();


        try {
            String name = (String) jsonObject.get("Name");
            String extra = (String) jsonObject.get("extra");
            String eMail = (String) jsonObject.get("email");
            String phone = (String) jsonObject.get("phone");

//            long enterTime = (long) jsonObject.get("enterTime");
//            long exitTime = (long) jsonObject.get("exitTime");
            participantItem.createParticipant(name, extra, eMail, phone, 0, 0);
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
                createParticipantItem(jsonArray.getJSONObject(i));
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

}


