package org.example.programmjavafx.Server.entities;

import com.google.gson.JsonObject;
import org.example.programmjavafx.Server.definitionOfPath.GetPlatformSpecificPath;
import org.example.programmjavafx.Server.interfaces.InterfaceMethods;
import org.example.programmjavafx.Server.service.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

/** Класс описывает сущность FileEntity и переопределяет её методы **/
public class FileEntity implements InterfaceMethods
{

    @Override
    public void get(Args args) throws IOException
    {
        try {
            System.out.println("В FileEntity сработал метод get!");

            if (args.data.equalsIgnoreCase("array.json"))
            {
                JsonObject response = new JsonObject();
                response.addProperty("ответ","метод get");
                args.session.getRemote().sendString(response.toString());
                Service.getJsonFile(args.session, args.data); // получение файла по заданному пути
            }
            else
            {
                Service.getLogFile(args.session, args.data, args.logFileDirectory);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Args args) throws IOException
    {
        JsonObject response = new JsonObject();
        try
        {
            response.addProperty("Ответ", "метод update");
            args.session.getRemote().sendString(response.toString());

            Path filePath = GetPlatformSpecificPath.getPlatformSpecificPath(args.jsonFileDirectory, args.data);

            String content = Service.getFileContentByWebSocketClientMessage(filePath.toString());


            System.out.println("Содержимое файла array.json: " + content); // вывод в консоль содержимого файла

            response.addProperty("content", content); // ответ клиенту

            args.session.getRemote().sendString(content); // отправка ответа клиенту
        }
        catch (Exception e)
        {
            response.addProperty("error", e.getMessage());
            System.err.println("Ошибка при чтении файла: " + e.getMessage());
        }
    }

    @Override
    public void save(Args args) throws IOException
    {


        System.out.println("В FileEntity сработал метод save!");

        Service.getLogFile(args.session, args.data, args.logFileDirectory);

        JsonObject response = new JsonObject();
        response.addProperty("ответ", "В FileEntity сработал метод save!");

        sendMessageFromFileEntity(args, response);
    }

    @Override
    public void delete(Args args) throws IOException
    {


        System.out.println("В FileEntity сработал метод delete!");

        Service.getLogFile(args.session, args.data, args.logFileDirectory);

        JsonObject response = new JsonObject();
        response.addProperty("ответ", "В FileEntity сработал метод delete!");

        sendMessageFromFileEntity(args, response);
    }

    @Override
    public void create(Args args) throws IOException
    {
        System.out.println("В FileEntity сработал метод create!");

        Service.getLogFile(args.session, args.data, args.logFileDirectory);

        JsonObject response = new JsonObject();
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
