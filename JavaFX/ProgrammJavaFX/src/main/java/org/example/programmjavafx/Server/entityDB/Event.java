package org.example.programmjavafx.Server.entityDB;

import java.sql.Timestamp;

/** Класс Event - сущность событие, является таблицей event,
 * которая через поле plate_id связано с таблицей plates c полем id в таблице Lathe_plates
 **/
public class Event
{
    //region Fields
    private int id;
    private int plateId;
    private String eventType; // тип события (начало, нормальная работа, ошибка, предупреждение, конец работы с платой)
    private Timestamp eventTime; // время произошедшего события
    private String eventMessage; // сообщение о произошедшем событии
    //endregion

    //region Constructor
    public Event(int id, int plateId, String eventType, Timestamp eventTime, String eventMessage) {
        this.id = id;
        this.plateId = plateId;
        this.eventType = eventType;
        this.eventTime = eventTime;
        this.eventMessage = eventMessage;
    }

    public Event(int plateId, String eventType, Timestamp eventTime, String eventMessage) {
        this.plateId = plateId;
        this.eventType = eventType;
        this.eventTime = eventTime;
        this.eventMessage = eventMessage;
    }

    public Event() {
        // default constructor
    }
    //endregion

    //region Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setPlateId(int plateId) {
        this.plateId = plateId;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public void setEventTime(Timestamp eventTime) {
        this.eventTime = eventTime;
    }

    public void setEventMessage(String eventMessage) {
        this.eventMessage = eventMessage;
    }
    //endregion

    //region Getters
    public int getId() {
        return id;
    }

    public int getPlateId() {
        return plateId;
    }

    public String getEventType() {
        return eventType;
    }

    public Timestamp getEventTime() {
        return eventTime;
    }

    public String getEventMessage() {
        return eventMessage;
    }
    //endregion
}

