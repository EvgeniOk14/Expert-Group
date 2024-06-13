package org.example.programmjavafx.Server.entities;

import com.google.gson.JsonObject;
import org.example.programmjavafx.Server.MyWebSocketHandler;
import org.example.programmjavafx.Server.interfaces.InterfaceMethods;
import java.io.IOException;

public class FileEntity implements InterfaceMethods
{
    private JsonObject response = new JsonObject();

    @Override
    public void get(Args args) throws IOException
    {
        System.out.println("В FileEntity сработал метод get!");

        MyWebSocketHandler.getFile(args.session, args.data);

        response.addProperty("ответ", "В FileEntity сработал метод get!");

        sendMessageFromFileEntity(args, response);
    }

    @Override
    public void update(Args args) throws IOException
    {
        System.out.println("В FileEntity сработал метод update!");

        MyWebSocketHandler.getFile(args.session, args.data);

        response.addProperty("ответ", "В FileEntity сработал метод Update!");

        sendMessageFromFileEntity(args, response);
    }

    @Override
    public void save(Args args) throws IOException
    {
        System.out.println("В FileEntity сработал метод save!");

        MyWebSocketHandler.getFile(args.session, args.data);

        response.addProperty("ответ", "В FileEntity сработал метод save!");

        sendMessageFromFileEntity(args, response);
    }

    @Override
    public void delete(Args args) throws IOException
    {
        System.out.println("В FileEntity сработал метод delete!");

        MyWebSocketHandler.getFile(args.session, args.data);

        response.addProperty("ответ", "В FileEntity сработал метод delete!");

        sendMessageFromFileEntity(args, response);
    }

    @Override
    public void create(Args args) throws IOException
    {
        System.out.println("В FileEntity сработал метод create!");

        MyWebSocketHandler.getFile(args.session, args.data);

        response.addProperty("ответ", "В FileEntity сработал метод create!");

        sendMessageFromFileEntity(args, response);
    }

    public  static void sendMessageFromFileEntity(Args args, JsonObject response)
    {
        try
        {
            args.session.getRemote().sendString(response.toString());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
