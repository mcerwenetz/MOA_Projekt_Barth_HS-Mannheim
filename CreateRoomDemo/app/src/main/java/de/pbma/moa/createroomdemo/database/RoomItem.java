package de.pbma.moa.createroomdemo.database;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity(tableName = "dbRoom")
public class RoomItem {

    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo(name = "fremdId")
    public Long fremdId;

    @ColumnInfo(name = "roomName")
    public String roomName;

    @ColumnInfo(name = "open")
    public boolean open;

    @ColumnInfo(name = "host") // Vorname+Nachname+Extra
    public String host;

    @ColumnInfo(name = "eMail")
    public String eMail;

    @ColumnInfo(name = "phone")
    public String phone;

    @ColumnInfo(name = "place") //(Kuerzle) + PLZ + Ort
    public String place;

    @ColumnInfo(name = "address") //Strasse + Nr
    public String address;

    @ColumnInfo(name = "extra")
    public String extra;

    @ColumnInfo(name = "startTime") // in ms since default
    public long startTime;

    @ColumnInfo(name = "endTime") // in ms since default
    public long endTime;


    public static RoomItem createRoom(String roomName,boolean open ,String host,String eMail,
                                      String phone, String place,String address, String extra,
                                      long startTime, long endTime) {
        RoomItem room = new RoomItem();
        room.roomName = roomName;
        room.open =open;
        room.host = host;
        room.eMail = eMail;
        room.phone = phone;
        room.place = place;
        room.address = address;
        room.extra = extra;
        room.startTime = startTime;
        room.endTime = endTime;
        return room;
    }
    public String getUri(){
        if(this.fremdId==0)
            return this.roomName + "/" + this.eMail + "/" + this.id;
        else
            return this.roomName + "/" + this.eMail + "/" + this.fremdId;
    }

    @Override
    public String toString() {
        return "RoomItem{" +
                "id=" + id +
                ", fremdId=" + fremdId +
                ", roomName='" + roomName + '\'' +
                ", open=" + open +
                ", host='" + host + '\'' +
                ", eMail='" + eMail + '\'' +
                ", phone='" + phone + '\'' +
                ", place='" + place + '\'' +
                ", address='" + address + '\'' +
                ", extra='" + extra + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoomItem roomItem = (RoomItem) o;
        return id == roomItem.id &&
                open == roomItem.open &&
                startTime == roomItem.startTime &&
                endTime == roomItem.endTime &&
                Objects.equals(fremdId, roomItem.fremdId) &&
                roomName.equals(roomItem.roomName) &&
                host.equals(roomItem.host) &&
                eMail.equals(roomItem.eMail) &&
                phone.equals(roomItem.phone) &&
                place.equals(roomItem.place) &&
                address.equals(roomItem.address) &&
                Objects.equals(extra, roomItem.extra);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, fremdId, roomName, open, host, eMail, phone, place, address, extra, startTime, endTime);
    }
}



