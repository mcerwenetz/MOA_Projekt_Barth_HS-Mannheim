package de.pbma.moa.createroomdemo.RoomParticipant;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Objects;

// Participants können mehrmals aber mit unterschiedelicher ruamid in der tabelle stehen

@Entity(tableName = "dbParticipant")
public class ParticipantItem {

    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo(name = "Name") // Vorname+Nachname
    public String name;

    @ColumnInfo(name = "extra")
    public String extra;

    @ColumnInfo(name = "eMail")
    public String eMail;

    @ColumnInfo(name = "phone")
    public String phone;

    @ColumnInfo(name = "roomId") //Id of room Participant is in
    public long roomId;

    public static ParticipantItem createParticipant(String name, String extra, String email, String phone, long roomId) {
        ParticipantItem participant = new ParticipantItem();
        participant.name = name;
        participant.extra = extra;
        participant.eMail = email;
        participant.phone = phone;
        participant.roomId = roomId;
        return participant;
    }

    @Override
    public String toString() {
        return "ParticipantItem{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", extra='" + extra + '\'' +
                ", eMail='" + eMail + '\'' +
                ", phone='" + phone + '\'' +
                ", roomId=" + roomId +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParticipantItem that = (ParticipantItem) o;
        return id == that.id &&
                roomId == that.roomId &&
                name.equals(that.name) &&
                Objects.equals(extra, that.extra) &&
                Objects.equals(eMail, that.eMail) &&
                Objects.equals(phone, that.phone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, extra, eMail, phone, roomId);
    }
}
