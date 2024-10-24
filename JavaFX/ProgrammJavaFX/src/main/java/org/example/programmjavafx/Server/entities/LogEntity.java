package org.example.programmjavafx.Server.entities;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.example.programmjavafx.Server.interfaces.InterfaceMethods;
import org.example.programmjavafx.Server.service.Service;
import java.io.IOException;

/** Класс описывает сущность LogEntity и переопределяет её методы **/
public class LogEntity implements InterfaceMethods
{
    //region Fields
    private static Gson gson = new Gson();
    private JsonObject response = new JsonObject();
    //endregion

    @Override
    public void get(InterfaceMethods.Args args) throws IOException
    {
        System.out.println("В LogEntity сработал метод get");

        args.setPlateNumber(args.getPlateNumber()); // устанавливаем номер платы равный data

        System.out.println("Установлен номер платы: " + args.plateNumber);

        Service.findLogByBoardNumber(args.session, args.data); // вызов метода нахождения лог-файла

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

    /**
     * метод отвечает за отправку сообщения клиенту
     * в виде JSON-объекта через WebSocket-соединение
     * принимает на вход необходимый параметр(сессию) и ответ в виде Json объекта
     * **/
    public static void sendMessage(InterfaceMethods.Args args, JsonObject response)
    {
        try
        {
            // получает объект WebSocket-сессии
            // возвращает удаленную конечную точку этой WebSocket-сессии
            // преобразования объекта JsonObject в строку формата JSON
            // преобразует объект JsonObject в строку формата JSON с помощью библиотеки Gson
            args.session.getRemote().sendString(gson.toJson(response));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}


