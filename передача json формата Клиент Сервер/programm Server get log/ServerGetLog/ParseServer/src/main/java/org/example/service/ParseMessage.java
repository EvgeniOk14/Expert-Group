package org.example.service;

import org.eclipse.jetty.websocket.api.Session;
import org.example.service.create.HandleCreateAction;
import org.example.service.delete.HandleDeleteAction;
import org.example.service.get.HandleGetAction;
import org.example.service.post.HandlePostAction;
import org.example.service.save.HandleSaveAction;
import org.example.service.send.HandleSendAction;
import org.example.service.update.HandleUpdateAction;
import java.io.IOException;

public class ParseMessage
{
    //region Fields
    private HandleGetAction handleGetAction;
    private HandleCreateAction handleCreateAction;
    private HandleUpdateAction handleUpdateAction;
    private HandleDeleteAction handleDeleteAction;
    private HandlePostAction handlePostAction;
    private HandleSaveAction handleSaveAction;
    private HandleSendAction handleSendAction;
    //endregion

    //region Constructor
    public ParseMessage(HandleGetAction handleGetAction,
                        HandleCreateAction handleCreateAction,
                        HandleUpdateAction handleUpdateAction,
                        HandleDeleteAction handleDeleteAction,
                        HandlePostAction handlePostAction,
                        HandleSaveAction handleSaveAction,
                        HandleSendAction handleSendAction)
    {
        this.handleGetAction = handleGetAction;
        this.handleCreateAction = handleCreateAction;
        this.handleUpdateAction = handleUpdateAction;
        this.handleDeleteAction = handleDeleteAction;
        this.handlePostAction = handlePostAction;
        this.handleSaveAction = handleSaveAction;
        this.handleSendAction = handleSendAction;
    }
    //endregion

    public void parseMessage(String message, Session session) throws IOException
    {
        String[] parts = message.split(" "); // Парсинг сообщения по пробелам

        if (parts.length < 3) // проверка правильности запроса (должно быть не менее трёх параметров в запросе)
        {
            session.getRemote().sendString("Error: Invalid message format."); // не корректный запрос от клиента
            return;
        }

        String method = parts[0].toLowerCase(); // Извлечение метода из массива строк
        String action = parts[1].toLowerCase(); // Извлечение действия из массива строк
        String parameter = parts[2];            // Извлечение метода параметра из массива строк

        switch (method) // Обработка команды в зависимости от метода и действия
        {
            case "get":
                handleGetAction.handleGetAction(session, action, parameter); // вызов метода обработки метода get
                break;

            case "save":
                handleSaveAction.save(session, action, parameter); // вызов метода обработки метода get
                break;

            case "send":
                handleSendAction.send(session, action, parameter); // вызов метода обработки метода get
                break;

            case "delete":
                handleDeleteAction.delete(session, action, parameter); // вызов метода обработки метода get
                break;

            case "update":
                handleUpdateAction.update(session, action, parameter); // вызов метода обработки метода get
                break;

            case "create":
                handleCreateAction.create(session, action, parameter); // вызов метода обработки метода get
                break;

            case "post":
                handlePostAction.post(session, action, parameter); // вызов метода обработки метода get
                break;

            default:
                session.getRemote().sendString("Error: Unknown method " + method + ".");
        }
    }

}
