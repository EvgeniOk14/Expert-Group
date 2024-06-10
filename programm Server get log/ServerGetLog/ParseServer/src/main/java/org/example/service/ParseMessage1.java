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

public class ParseMessage1
{
    private static volatile ParseMessage1 instance; // volatile гарантирует,
                                                    // что изменения переменной будут видны всем
                                                    // потокам.

    private HandleGetAction handleGetAction;
    private HandleCreateAction handleCreateAction;
    private HandleUpdateAction handleUpdateAction;
    private HandleDeleteAction handleDeleteAction;
    private HandlePostAction handlePostAction;
    private HandleSaveAction handleSaveAction;
    private HandleSendAction handleSendAction;

    private ParseMessage1()
    {
        // Приватный конструктор для предотвращения создания экземпляров извне
    }

    // Ленивая инициализация с двойной проверкой
    public static ParseMessage1 getInstance(HandleGetAction handleGetAction,
                                           HandleCreateAction handleCreateAction,
                                           HandleUpdateAction handleUpdateAction,
                                           HandleDeleteAction handleDeleteAction,
                                           HandlePostAction handlePostAction,
                                           HandleSaveAction handleSaveAction,
                                           HandleSendAction handleSendAction)
    {
        if (instance == null) // Проверяет, создан ли уже экземпляр?
        {
            synchronized (ParseMessage1.class) // Блокировка класса,
                                               // чтобы только один поток мог войти в этот блок
                                               // кода одновременно.
            {
                if (instance == null) // Вторая проверка внутри синхронизированного блока
                                      // для предотвращения создания нескольких экземпляров,
                                      // если несколько потоков проходят первую проверку одновременно.
                {
                    instance = new ParseMessage1();
                    instance.handleGetAction = handleGetAction;
                    instance.handleCreateAction = handleCreateAction;
                    instance.handleUpdateAction = handleUpdateAction;
                    instance.handleDeleteAction = handleDeleteAction;
                    instance.handlePostAction = handlePostAction;
                    instance.handleSaveAction = handleSaveAction;
                    instance.handleSendAction = handleSendAction;
                }
            }
        }
        return instance;
    }

    public void parseMessage1(String message, Session session) throws IOException {
        String[] parts = message.split(" ");

        if (parts.length < 3) {
            session.getRemote().sendString("Error: Invalid message format.");
            return;
        }

        String method = parts[0].toLowerCase();
        String action = parts[1].toLowerCase();
        String parameter = parts[2];

        switch (method) {
            case "get":
                handleGetAction.handleGetAction(session, action, parameter);
                break;
            case "save":
                handleSaveAction.save(session, action, parameter);
                break;
            case "send":
                handleSendAction.send(session, action, parameter);
                break;
            case "delete":
                handleDeleteAction.delete(session, action, parameter);
                break;
            case "update":
                handleUpdateAction.update(session, action, parameter);
                break;
            case "create":
                handleCreateAction.create(session, action, parameter);
                break;
            case "post":
                handlePostAction.post(session, action, parameter);
                break;
            default:
                session.getRemote().sendString("Error: Unknown method " + method + ".");
        }
    }
}

