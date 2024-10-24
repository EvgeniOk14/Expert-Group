package org.example.programmjavafx.Server;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.example.programmjavafx.Server.interfaces.InterfaceMethods;
import org.example.programmjavafx.Server.jsonService.TransformationToJsonFormat;
import org.example.programmjavafx.Server.maps.MethodRegistry;
import org.example.programmjavafx.Server.reflectionMethods.InvokeMethods;
import org.example.programmjavafx.Server.someErrors.SomeErrors;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/** класс обработчик сообщений от клиента **/
@WebSocket
public class MyWebSocketHandler
{
    //region Fields
    private static final Map<Session, String> sessions = new ConcurrentHashMap<>();
    private final InvokeMethods invokeMethods = new InvokeMethods();
    private static String messageFromClient;
    //endregion

    /**
     * метод отвечает за соединение с клиентом
     **/
    @OnWebSocketConnect
    public void onConnect(Session session)
    {
        System.out.println("Метод onConnect в классе MyWebSocketHandler вызван в: " + new Date(System.currentTimeMillis()));

        String userName = "Клиент, с URL: " + session.getRemoteAddress().getAddress(); // создаём именя клиента используя его адрес

        sessions.put(session, userName);

        System.out.println("Присоединён новый WebsocketClient, URL: " + session.getRemoteAddress().getAddress());
    }

    /**
     * метод отвечает за обработку сообщений от клиента
     *  В зависимости от полученного сообщения от клиента
     *  определяет, какой метод нужно вызвать,
     *  и использует invokeMethod для выполнения этого вызова
     **/
    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException
    {
        messageFromClient = message;
        System.out.println("От клиента c URL: " + sessions.get(session) + "получено сообщение: " + message);

        RequestMessage requestMessage;

        try
        {
            // преобразование входящего сообщения json формата в Java объект, конкретно в объект класса RequestMessage:
            requestMessage = TransformationToJsonFormat.transformationToJsonFormat(message);
        }
        catch (JsonSyntaxException e) // обработка недопустимого формата Json
        {
            SomeErrors.JsonForbiddenFormatError(session, message, sessions);
            return;
        }

        /** получаем из Класса RequestMessage данные **/
        String method = requestMessage.getMethod();
        String entity = requestMessage.getEntity();
        String data = requestMessage.getData();

        JsonObject response = new JsonObject();

        if (method == null) // если method отсутствует в сообщение от клмента
        {   // обработка ошибки: отсутствие method в сообщении от клиента
            SomeErrors.errorAbsenceMethodInClientMessage(response, session);
        }
        if (entity == null) // если entity отсутствует в сообщение от клмента
        {   // обработка ошибки: отсутствие entity в сообщении от клиента
            SomeErrors.errorAbsenceEntityInClientMessage(response, session);
        }
        if (data == null) // если data отсутствует в сообщение от клмента
        {   //  обработка ошибки: отсутствие data в сообщении от клиента
            SomeErrors.errorAbsenceDataInClientMessage(response, session, sessions, entity);
        }

        try
        {
            String key = entity.toLowerCase();
            InterfaceMethods foundEntity = MethodRegistry.getMap(key); // получаем класс (сущьность), который будет предоставлять метод для обработки

            if (foundEntity != null)
            {
                InterfaceMethods.Args args = new InterfaceMethods.Args(session, data); // создаём клас с необходимыми аргументами session и data

                args.setPlateNumber(data); // устанавливаем номер платы станка равной data

                invokeMethods.invokeMethod(foundEntity, method, args, response); // вызов нужного метода с использованием рефлексии
            }
        }
        catch (Exception e) // обработка внутренней ошибки сервера
        {
            SomeErrors.innerErrorFromServer(session, response, sessions, e);
        }
    }

    /**
     * метод отвечает за закрытие соединений
     **/
    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason)
    {
        sessions.remove(session);
        System.out.println("разорвано соединение с (WebSocket Client) клиентом: " + session.getRemoteAddress().getAddress());
    }

    /**
     * метод отвечает за ошибки с сообщениями
     **/
    @OnWebSocketError
    public void onError(Session session, Throwable throwable)
    {
        System.err.println("Ошибка соединения с (WebSocket Client) Клиентом: " + throwable.getMessage());
    }

    /**
     * метод обходит все сессии и отправляет сообщения всем подключённым клиентам
     **/
    public static void broadcastMessage(String message)
    {
        // итерация по сессиям
        for (Map.Entry<Session, String> currentSession : sessions.entrySet())
        {
            Session session = currentSession.getKey(); // получаем сессию
            String userName = currentSession.getValue(); // получаем имя пользователя
            if (session.isOpen()) // если сессия открыта, то:
            {
                try
                {
                    session.getRemote().sendString(userName + ": " + message); // посылаем сообщение пользователю
                    System.out.println(userName + ": " + message); // Логирование сообщения с именем пользователя
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * метод для получения номера платы из сообщения клиента
     **/
    public static String getPlateNumberFromWebSocketClientMessage(String message)
    {
        try
        {
            RequestMessage requestMessage = TransformationToJsonFormat.transformationToJsonFormat(message);
            return requestMessage.getData(); // предполагается, что номер платы содержится в поле data
        }
        catch (JsonSyntaxException e)
        {
            System.err.println("Ошибка преобразования сообщения в формат JSON: " + e.getMessage());
            return null;
        }
    }

    /** метод получения номера платы из сообщения **/
    public static String getClientMessage()
    {
        return getPlateNumberFromWebSocketClientMessage(messageFromClient);
    }
}














