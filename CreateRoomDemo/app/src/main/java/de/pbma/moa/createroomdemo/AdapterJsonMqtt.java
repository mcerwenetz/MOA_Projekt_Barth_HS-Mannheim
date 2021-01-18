package de.pbma.moa.createroomdemo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.pbma.moa.createroomdemo.database.ParticipantItem;
import de.pbma.moa.createroomdemo.database.RoomItem;
import de.pbma.moa.createroomdemo.preferences.MySelf;

public class AdapterJsonMqtt {

    public static final String TEILNEHMER = "teilnehmer";
    public static final String ENTERTIME = "entertime";
    public static final String TYPE = "type";
    public static final String EXITTIME ="exittime";
    public static final String TEILNEHMERLIST = "teilnehmerlist" ;
    public static final String RAUM = "raum";

    private ParticipantItem participantItem;

    private enum JSONTypes {
        LOGIN("login"),
        LOGOUT("logout"),
        TEILNEHMER("teilnehmer"),
        RAUMINFO("rauminfo")
        ;
        public final String label;
        JSONTypes(String label){
            this.label = label;
        }

    }



    public static JSONObject getAnmeldungJSON(MySelf teilnehmer, Long entertime){
        JSONObject ret = new JSONObject();
        JSONObject teilnehmerAsJSON = getJSONMySelf(teilnehmer);
        try {
            ret.put(TYPE, JSONTypes.LOGIN);
            ret.put(TEILNEHMER,teilnehmerAsJSON);
            ret.put(ENTERTIME,entertime);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  ret;
    }

    public static JSONObject getAbmeldungJSON(MySelf teilnehmer, Long exittime){
        JSONObject ret = new JSONObject();
        JSONObject teilnehmerAsJSON = getJSONMySelf(teilnehmer);
        try {
            ret.put(TYPE, JSONTypes.LOGOUT);
            ret.put(TEILNEHMER,teilnehmerAsJSON);
            ret.put(EXITTIME, exittime);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ret;

    }

    public static JSONObject getTeilnehmerListJSON(List<ParticipantItem> participants){
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

    public static JSONArray getTeilnehmerliste(List<ParticipantItem> participants){
        JSONArray teilnehmerliste = new JSONArray();
        for(ParticipantItem participant : participants){
            teilnehmerliste.put(getJSONParticipant(participant));
        }
        return teilnehmerliste;
    }

    private static JSONObject getJSONMySelf(MySelf teilnehmer){
        Map<Object, String> teilnehmermap = populateMap(teilnehmer);
        return new JSONObject(teilnehmermap);
    }

    private static JSONObject getJSONParticipant(ParticipantItem participantItem){
        Map<Object, String> teilnehmermap = populateMap(participantItem);
        return new JSONObject(teilnehmermap);
    }

    public static JSONObject getRauminfoJSON(RoomItem roomItem){
        JSONObject rauminfoJSON = new JSONObject();
        try {
            rauminfoJSON.put(TYPE,JSONTypes.RAUMINFO);
            rauminfoJSON.put(RAUM, getRoomJSON(roomItem));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return rauminfoJSON;
    }

    private static JSONObject getRoomJSON(RoomItem roomItem){
        Map<Object, String> roomMap = populateMap(roomItem);
        return new JSONObject(roomMap);
    }

    public static Map<Object, String> populateMap(final Object o){
        Map<Object, String> result = new HashMap<>();
        Field[] fields = o.getClass().getDeclaredFields();
        for(Field field : fields){
            String nicefield = field.toString();
            String classString = o.getClass().getName();
            int startposition = nicefield.indexOf(classString);
            nicefield = nicefield.substring(startposition, nicefield.length());
            nicefield = nicefield.substring(nicefield.indexOf(".")+1,nicefield.length());
            try {
                result.put(nicefield, field.get(o).toString());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return result;
    }


    public static RoomItem createRoom(JSONObject jsonObject) throws JSONException {
        RoomItem roomItem = new RoomItem();
        //Auslesen der JsonFiles
        String roomName = (String)jsonObject.get("roomName");
        boolean open    = (boolean)jsonObject.get("open");
        String host     = (String)jsonObject.get("host");
        String eMail    = (String)jsonObject.get("host");
        String phone    = (String)jsonObject.get("eMail");
        String place    = (String)jsonObject.get("place");
        String address  = (String)jsonObject.get("address");
        String extra    = (String)jsonObject.get("extra");

        long startTime  = (long)jsonObject.get("startTime");
        long endTime    = (long)jsonObject.get("endTime");

        roomItem.createRoom(roomName,open,host,eMail,phone,place,address,extra,startTime,endTime);
        //TODO Fremdid nochmal nachfragen wie der Gedanke dahinter ist
        return roomItem;
    }

    public static ParticipantItem createParticipant(JSONObject jsonObject) throws JSONException {
        ParticipantItem participantItem = new ParticipantItem();
        //Auslesen der JsonFiles
        String name     = (String)jsonObject.get("Name");
        String extra    = (String)jsonObject.get("extra");

        String eMail    = (String)jsonObject.get("extra");
        String phone    = (String)jsonObject.get("extra");

        //raumid von Teiler
        long  roomid    = (long)jsonObject.get("roomId");
        long  enterTime = (long)jsonObject.get("enterTime");
//        long  exitTime  = (long)jsonObject.get("exitTime");

        //Uebergeben der Items an die
        participantItem.createParticipant(name,extra,eMail,phone,roomid,enterTime);
        return participantItem;
    }

    public static List<ParticipantItem> createParticipantList(JSONObject jsonObject){
        return null;

        //mit record bzw mit Iterator über die Liste iterieren und diese in die Paritcipantlist einfügen
    }

}


