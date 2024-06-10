package org.example.programmjavafx.Server.entities;

import com.google.gson.JsonObject;
import org.example.programmjavafx.Server.MyWebSocketHandler;
import org.example.programmjavafx.Server.interfaces.InterfaceMethods;

import java.io.IOException;

public class FileEntity implements InterfaceMethods
{


    @Override
    public void get(Args args) throws IOException
    {
        JsonObject response = new JsonObject();

        MyWebSocketHandler.getFile(args.session, args.data);

        response.addProperty("status", "success");
        response.addProperty("data", "Log entity data.");
        try
        {
            args.session.getRemote().sendString(response.toString());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Args args)
    {
        System.out.println("сработал метод update!");
    }

    @Override
    public void save(Args args)
    {
        // в случае необходимости метод нужно переопределить
    }

    @Override
    public void delete(Args args)
    {
        // в случае необходимости метод нужно переопределить
    }

    @Override
    public void create(Args args)
    {
        // в случае необходимости метод нужно переопределить
    }
}
