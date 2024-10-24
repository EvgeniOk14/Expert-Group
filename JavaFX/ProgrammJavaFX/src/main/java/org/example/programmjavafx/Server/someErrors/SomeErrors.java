package org.example.programmjavafx.Server.someErrors;

import com.google.gson.JsonObject;
import org.eclipse.jetty.websocket.api.Session;
import java.io.IOException;
import java.util.Map;

/**
 * Класс SomeErrors, содержит методы обработки ошибок
 * применяется в классе MyWebsocketHandler
 * **/
public class SomeErrors
{
    //ошибка преобразования Json формата (недопустимый формат)
    public static void JsonForbiddenFormatError(Session session, String message, Map<Session, String> sessions) throws IOException
    {
        System.err.println("Не удалось разобрать сообщение: " + message);
        JsonObject errorResponse = new JsonObject();
        errorResponse.addProperty("status", "error");
        errorResponse.addProperty("message", "Недопустимый формат JSON");
        session.getRemote().sendString(sessions.get(session) + errorResponse.toString());
    }

    // внутренняя ошибка сервера
    public static void innerErrorFromServer(Session session, JsonObject response, Map<Session, String> sessions, Exception e) throws IOException
    {
        System.err.println("Запрос на обработку ошибки: " + e.getMessage());
        response.addProperty("статус ", "ошибка");
        response.addProperty("ответ ", "Внутренняя ошибка сервера.");
        session.getRemote().sendString(sessions.get(session) + response.toString());
    }

    // отсутствие method в сообщении от клиента
    public static void errorAbsenceMethodInClientMessage(JsonObject response, Session session) throws IOException
    {
        response.addProperty("status", "error");
        response.addProperty("ошибка", "в сообщении клиента отсутствует метод!");
        session.getRemote().sendString(response.toString());
    }

    // отсутствие entity в сообщении от клиента
    public static void errorAbsenceEntityInClientMessage(JsonObject response, Session session) throws IOException
    {
        response.addProperty("status", "error");
        response.addProperty("ошибка", "в сообщении клиента отсутствует сущьность entity!");
        session.getRemote().sendString(response.toString());
    }

    // отсутствие data в сообщении от клиента
    public static  void errorAbsenceDataInClientMessage(JsonObject response, Session session, Map<Session, String> sessions, String entity) throws IOException
    {
        response.addProperty("статус ", "ошибка");
        response.addProperty("ответ ", "в сообщении от клиента, либо отстутствуют данные, либо Неизвестные данные (номер платы): " + entity);
        session.getRemote().sendString(sessions.get(session) + response.toString());
    }
}