package de.pbma.moa.createroomdemo;

import org.joda.time.DateTime;
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

    private static final String TEILNEHMER = "teilnehmer";
    private static final String ENTERTIME = "entertime";
    private static final String TYPE = "type";
    private static final String EXITTIME ="exittime";
    private static final String TEILNEHMERLIST = "teilnehmerlist" ;
    private static final String RAUM = "raum";

    private enum JSONTypes {
        LOGIN("login"),
        LOGOUT("logout"),
        TEILNEHMER("teilnehmer"),
        RAUMINFO("rauminfo")
        ;
        public final String label;
        private JSONTypes(String label){
            this.label = label;
        }

    };


    private ParticipantItem participantItem;

    public static JSONObject getAnmeldungJSON(MySelf teilnehmer, DateTime entertime){
        JSONObject ret = new JSONObject();
        try {
            ret.put(TYPE, JSONTypes.LOGIN);
            JSONObject teilnehmerAsJSON = getJSONTeilnehmer(teilnehmer);
            ret.put(TEILNEHMER,teilnehmerAsJSON);
            ret.put(ENTERTIME,entertime.getMillis());
        } catch (JSONException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return  ret;
    }

    public static JSONObject getAbmeldungJSON(MySelf teilnehmer, DateTime exittime){
        JSONObject ret = new JSONObject();
        try {
            JSONObject teilnehmerAsJSON = getJSONTeilnehmer(teilnehmer);
            ret.put(TYPE, JSONTypes.LOGOUT);
            ret.put(TEILNEHMER,teilnehmerAsJSON);
            ret.put(EXITTIME, exittime.getMillis());

        } catch (JSONException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return ret;

    }

    public static JSONObject getTeilnehmerJSON(List<MySelf> teilnehmende) throws JSONException, IllegalAccessException {
        JSONArray teilnehmerliste = getTeilnehmerliste(teilnehmende);
        JSONObject ret = new JSONObject();
        ret.put(TYPE, JSONTypes.TEILNEHMER);
        ret.put(TEILNEHMERLIST, teilnehmerliste);
        return ret;
    }

    private static JSONArray getTeilnehmerliste(List<MySelf> teilnehmende) throws JSONException, IllegalAccessException {
        JSONArray teilnehmerliste = new JSONArray();
        for(MySelf teilnehmer : teilnehmende){
            teilnehmerliste.put(getJSONTeilnehmer(teilnehmer));
        }
        return teilnehmerliste;
    }

    private static JSONObject getJSONTeilnehmer(MySelf teilnehmer) throws JSONException, IllegalAccessException {
        Map<Object, String> teilnehmermap = populateMap(teilnehmer);
        JSONObject jsonTeilnehmer = new JSONObject(teilnehmermap);
        return jsonTeilnehmer;
    }

    public static JSONObject getRauminfoJSON(RoomItem roomItem) throws Exception {
        JSONObject rauminfoJSON = new JSONObject();
        rauminfoJSON.put(TYPE,JSONTypes.RAUMINFO);
        rauminfoJSON.put(RAUM, getRoomJSON(roomItem));
        return rauminfoJSON;
    }

    private static JSONObject getRoomJSON(RoomItem roomItem) throws IllegalAccessException {
        Map<Object, String> roomMap = populateMap(roomItem);
        JSONObject raumJSON = new JSONObject(roomMap);
        return raumJSON;
    }

    public static Map<Object, String> populateMap(final Object o) throws IllegalAccessException {
        Map<Object, String> result = new HashMap<>();
        Field[] fields = o.getClass().getDeclaredFields();
        for(Field field : fields){
            String nicefield = field.toString();
            String classString = o.getClass().getName();
            Integer startposition = nicefield.indexOf(classString);
            nicefield = nicefield.substring(startposition, nicefield.length());
            nicefield = nicefield.substring(nicefield.indexOf(".")+1,nicefield.length());
            result.put(nicefield, field.get(o).toString());
        }
        return result;
    }


    public static RoomItem createRoom(JSONObject jsonObject) throws JSONException {
        RoomItem roomItem = new RoomItem();
        //Auslesen der JsonFiles
        String roomName = (String)jsonObject.get("roomName");
        boolean open     = (boolean)jsonObject.get("open");
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
    }

}


