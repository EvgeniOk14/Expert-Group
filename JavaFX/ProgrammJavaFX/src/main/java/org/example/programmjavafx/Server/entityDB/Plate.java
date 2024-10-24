package org.example.programmjavafx.Server.entityDB;

/**
 * Класс Plate - сущность платы, таблицы Lathe_plates,
 * у которой есть номер plateNumber и порядковый номер в таблице id,,
 * связано через первичный ключ id с полем plateId в таблице events
 * **/
public class Plate
{
    //region Fields
    private int id; // первичный ключ в таблице plates
    private String plateNumber; // номер платы станка
    //endregion

    //region Constructor
    public Plate(int id, String plateNumber)
    {
        this.id = id;
        this.plateNumber = plateNumber;
    }

    public Plate(String plateNumber)
    {
        this.plateNumber = plateNumber;
    }

    public Plate()
    {
        //default constructor
    }
    //endregion

    //region Setters
    public void setId(int id)
    {
        this.id = id;
    }

    public void setPlateNumber(String plateNumber)
    {
        this.plateNumber = plateNumber;
    }
    //endregion

    //region Getters
    public int getId()
    {
        return id;
    }

    public String getPlateNumber()
    {
        return plateNumber;
    }
    //endregion
}
