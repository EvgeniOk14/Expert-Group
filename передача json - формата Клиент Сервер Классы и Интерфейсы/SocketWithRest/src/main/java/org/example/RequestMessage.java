package org.example;

public class RequestMessage
{
    private String method;
    private String entity;
    private String data;

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

    }

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
}
