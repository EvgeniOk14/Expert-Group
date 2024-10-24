package org.example.programmjavafx.Server.entities;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.example.programmjavafx.Server.interfaces.InterfaceMethods;
import org.example.programmjavafx.Server.service.Service;
import org.example.programmjavafx.Server.someErrors.SomeErrors;
import java.io.IOException;

/** Класс описывает сущность FeederEntity и переопределяет её методы **/
public class FeederEntity implements InterfaceMethods
{
    //region Fields
    private JsonObject response = new JsonObject();
    private static final int maxNumber = 100;
    //endregion

    @Override
    public void get(Args args) throws Exception
    {
        System.out.println("сработал метод get: в классе: FeederEntity");
        try
        {
            Service.checkNumber(args.data, maxNumber, args.session); // проверка числа на превышение максимального индекса массива

            response.addProperty("ответ клиенту", "в классе: FeederEntity сработал метод: get");

            sendMessageFromFeederEntity(args, response);
        }
        catch (Exception e)
        {
            SomeErrors.errorAbsenceDataInClientMessage(response, args.session, args.sessions, args.entity);
        }
    }

    @Override
    public void save(Args args) throws Exception
    {
        //it`s a temporary empty method
    }

    @Override
    public void delete(Args args) throws Exception
    {
        //it`s a temporary empty method
    }

    @Override
    public void create(Args args) throws Exception
    {
        //it`s a temporary empty method
    }

    @Override
    public void update(Args args) throws Exception
    {
        //it`s a temporary empty method
    }

    public  static void sendMessageFromFeederEntity(Args args, JsonObject response)
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
