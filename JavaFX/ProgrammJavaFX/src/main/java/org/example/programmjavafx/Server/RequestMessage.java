package org.example.programmjavafx.Server;

/**
 * класс описывает объект RequestMessage,
 * который включает в себя элементы запроса от клиента
 *  **/
public class RequestMessage
{
    //region Fields
    private String method;  // поле - метод
    private String entity; //  поле - сущность
    private String data;  //   поле - данные
    //end region

    //region Constructors
    public RequestMessage(String method, String entity, String data)
    {
        this.method = method;
        this.entity = entity;
        this.data = data;
    }

    public RequestMessage(String method, String entity)
    {
        this.method = method;
        this.entity = entity;
    }

    public RequestMessage(String method)
    {
        this.method = method;
    }

    public RequestMessage()
    {
        //default constructor
    }
    //endregion

    //region Getters & Setters
    public String getMethod()
    {
        return method;
    }

    public void setMethod(String method)
    {
        this.method = method;
    }

    public String getEntity()
    {
        return entity;
    }

    public void setEntity(String entity)
    {
        this.entity = entity;
    }

    public String getData()
    {
        return data;
    }

    public void setData(String data)
    {
        this.data = data;
    }
    //endregion
}
