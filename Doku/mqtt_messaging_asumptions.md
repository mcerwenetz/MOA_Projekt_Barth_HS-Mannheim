# Messenging
Für den Nachrichtenaustausch werden json-files als messanges gesendet. Teilnehmer und Host senden auf 2 unterschiedlichen topics verschiedene Steuernachrichten.

# Topics
Es gibt dpro Raum jeweils ein:
*Hosttopic
*Participant-Topic

## Hosttopic
Hier werden alle Nachrichten von Teilnehmern an den Host übermittelt

## Participant-Topic
Hier werden alle Nachrichten von dem Host an die Teilnehmer übermittelt

# JSONs
Es gibt die JSON-Files/message-arten:
*anmeldung.json
*abmeldung.json
*teilnehmer.json
*rauminfo.json

## anmeldung.json
Eine anmeldung.json enthält die Felder:
*Teilnehmer
*enterTime

Eine Anmeldung wird auf das host-topic gesendet, also an den host. Sie wird gesendet wenn ein Teilnehmer sich per mqtt-service auf ein topic connected.

### Teilnehmer
Hier steht das MySelf des Teilnehmers als String oder in anderer serialisierter Form

### enterTime
Das ist die Zeit zu der der Teilnehmer den Raum betritt.

## abmeldung.json
Eine abmeldung.json enthält die Felder:
*Teilnehmer
*exitTime

Eine Abmeldung wird auf das host-topic gesendet, also an den host. Sie wird gesendet wenn ein Teilnehmer sich per mqtt-service von einem topic disconnected. 

### Teilnehmer
Hier steht das MySelf des Teilnehmers als String oder in anderer serialisierter Form

# exitTime
Das ist die Zeit zu der der Teilnehmer den Raum verlässt.

## teilnehmer.json
Eine anmeldung.json enthält das Feld:
*Teilnehmerliste

Eine Teilnehmer.json wird auf das participant-topic gesendet, also an den participant. 
Sie wird gesendet wenn sich ein neuer Teilnehmer an einen Raum angemeldet hat. 
Der Host wartet auf ein Anmeldung.json. Erhält er es trägt er den Teilnehmer in die Datenbank ein und sendet ein Teilnehmer.json damit die anderen Teilnehmer um die Anmeldung des neuen Teilnehmers bescheid wissen.

### Teilnehmerliste
In der Teilnehmerliste stehen alle Teilnehmer des aktuellen Raums drin.


## Rauminfo
Eine anmeldung.json enthält das Feld:
*Raum

Eine Teilnehmer.json wird auf das participant-topic gesendet, also an den participant.
Sie wird gesendet wenn sich die Umstände des Raumes ändern.
Datunter zählen:
*neues Timeout
*raumstatus hat sich von offen auf geschlossen oder vice versa geändert

### Raum
Im Feld Raum steht die aktuelle Fassung des Raumes.

wenn die msg ein feld "RAUM" hat, dann wurde eine "Raum.json" gesendet
eine "Raum.json" wird vom Host an das teilnehmer-topic gesendet, damit
alle Teilnehmer die aktuellen Raum-Infos haben.
Sie wird gesendet, wenn sich z.B.das Timeout geändert hat, aber auch wenn sich
ein Teilnehmer neu einwähllt