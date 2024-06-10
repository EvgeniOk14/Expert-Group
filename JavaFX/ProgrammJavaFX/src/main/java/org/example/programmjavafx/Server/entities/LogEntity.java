package org.example.programmjavafx.Server.entities;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.example.programmjavafx.Server.MyWebSocketHandler;
import org.example.programmjavafx.Server.interfaces.InterfaceMethods;
import java.io.IOException;

public class LogEntity implements InterfaceMethods
{
    private Gson gson = new Gson();

    @Override
    public void get(InterfaceMethods.Args args) throws IOException
    {
        JsonObject response = new JsonObject();

        MyWebSocketHandler.findLogByBoardNumber(args.session, args.data); // вызов метода нахождения лог-файла

        response.addProperty("status", "success");

        response.addProperty("data", "Log entity data.");

        try
        {
            args.session.getRemote().sendString(gson.toJson(response)); // отправка соощения клиенту
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void update(InterfaceMethods.Args args)
    {
        System.out.println("сработал метод update!");

        JsonObject response = new JsonObject();

        response.addProperty("status", "success");

        response.addProperty("ответ", "Сработал метод update");

        try {
            args.session.getRemote().sendString("сработал метод update!");
            args.session.getRemote().sendString(gson.toJson(response));

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    @Override
    public void save(InterfaceMethods.Args args)
    {
        // в случае необходимости метод нужно переопределить
    }

    @Override
    public void delete(InterfaceMethods.Args args)
    {
        // в случае необходимости метод нужно переопределить
    }

    @Override
    public void create(InterfaceMethods.Args args)
    {
        // в случае необходимости метод нужно переопределить
    }
}


