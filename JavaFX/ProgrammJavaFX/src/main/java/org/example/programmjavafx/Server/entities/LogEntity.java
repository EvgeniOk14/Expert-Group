package org.example.programmjavafx.Server.entities;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.example.programmjavafx.Server.MyWebSocketHandler;
import org.example.programmjavafx.Server.interfaces.InterfaceMethods;
import java.io.IOException;

public class LogEntity implements InterfaceMethods
{
    private static Gson gson = new Gson();
    private JsonObject response = new JsonObject();

    @Override
    public void get(InterfaceMethods.Args args) throws IOException
    {
        System.out.println("В LogEntity сработал метод get");

        MyWebSocketHandler.findLogByBoardNumber(args.session, args.data); // вызов метода нахождения лог-файла

        response.addProperty("ответ", "В LogEntity сработал метод get");

        sendMessage(args, response);

    }

    @Override
    public void update(InterfaceMethods.Args args)
    {
        System.out.println("В LogEntity сработал метод update!");

        response.addProperty("ответ", "В LogEntity cработал метод update");

        sendMessage(args, response);

    }

    @Override
    public void save(InterfaceMethods.Args args)
    {
        System.out.println("В LogEntity сработал метод save!");

        response.addProperty("ответ", "В LogEntity сработал метод save");

        sendMessage(args, response);
    }

    @Override
    public void delete(InterfaceMethods.Args args)
    {
        System.out.println("В LogEntity сработал метод dalete!");

        response.addProperty("ответ", "В LogEntity сработал метод delete");

        sendMessage(args, response);
    }

    @Override
    public void create(InterfaceMethods.Args args)
    {
        System.out.println("В LogEntity сработал метод create!");

        response.addProperty("ответ", "В LogEntity  сработал метод create");

        sendMessage(args, response);
    }

    public static void sendMessage(InterfaceMethods.Args args, JsonObject response)
    {
        try {
            //args.session.getRemote().sendString("сработал метод update!");
            args.session.getRemote().sendString(gson.toJson(response));

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}


