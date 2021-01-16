package de.pbma.moa.createroomdemo;

import org.json.JSONObject;

import java.util.List;

import de.pbma.moa.createroomdemo.database.ParticipantDao;
import de.pbma.moa.createroomdemo.database.ParticipantItem;
import de.pbma.moa.createroomdemo.database.RoomItem;

public class AdapterJsonMqtt {
    private ParticipantItem participantItem;

    public static JSONObject msgToJson(String msg){

    }

    public static RoomItem createRoom(JSONObject jsonObject){
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

    public static ParticipantItem createParticipant(JSONObject jsonObject){
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


