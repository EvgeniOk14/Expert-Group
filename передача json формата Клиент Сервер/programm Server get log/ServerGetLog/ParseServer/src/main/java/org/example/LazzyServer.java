package org.example;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;
import org.example.service.ParseMessage1;
import org.example.service.create.HandleCreateAction;
import org.example.service.delete.HandleDeleteAction;
import org.example.service.get.HandleGetAction;
import org.example.service.post.HandlePostAction;
import org.example.service.save.HandleSaveAction;
import org.example.service.send.HandleSendAction;
import org.example.service.update.HandleUpdateAction;

import java.io.IOException;

@WebSocket
public class LazzyServer
{
    private final ParseMessage1 parseMessage1;

    public LazzyServer(ParseMessage1 parseMessage1)
    {
        this.parseMessage1 = parseMessage1;
    }

    /** метод предназначен для выполнения кода при подключении клиента к серверу **/
    @OnWebSocketConnect
    public void onConnect(Session session)
    {
        System.out.println("Client connected: " + session.getRemoteAddress().getAddress());
    }

    /** метод обработки сообщения от клиента **/
    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException
    {
        if (message != null && !message.trim().isEmpty()) // если сообщение не пустое и не равно null
        {
            parseMessage1.parseMessage1(message, session); // метод парсит строку и производит действия в зависимости от результата
        }
        else
        {
            session.getRemote().sendString("Error: Empty message.");
        }
    }

    /** метод закрытия соединения **/
    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason)
    {
        System.out.println("Client disconnected: " + reason);
        System.out.println("Status code: " + statusCode);
    }

    public static void main(String[] args)
    {
        Server server = new Server(8095);

        WebSocketHandler wsHandler = new WebSocketHandler() {
            @Override
            public void configure(WebSocketServletFactory factory)
            {
                factory.setCreator((req, resp) ->
                {
                    HandleGetAction handleGetAction = new HandleGetAction();
                    HandleCreateAction handleCreateAction = new HandleCreateAction();
                    HandleUpdateAction handleUpdateAction = new HandleUpdateAction();
                    HandleDeleteAction handleDeleteAction = new HandleDeleteAction();
                    HandlePostAction handlePostAction = new HandlePostAction();
                    HandleSaveAction handleSaveAction = new HandleSaveAction();
                    HandleSendAction handleSendAction = new HandleSendAction();

                    ParseMessage1 parseMessage1 = ParseMessage1.getInstance(handleGetAction,
                                                                            handleCreateAction,
                                                                            handleUpdateAction,
                                                                            handleDeleteAction,
                                                                            handlePostAction,
                                                                            handleSaveAction,
                                                                            handleSendAction);

                    return new LazzyServer(parseMessage1);
                });
            }
        };

        server.setHandler(wsHandler);

        try {
            server.start();
            server.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}




// factory.setCreator((req, resp) -> { ... }):
//req — это объект типа ServletUpgradeRequest, который представляет запрос на обновление соединения до WebSocket.
//resp — это объект типа ServletUpgradeResponse, который представляет ответ на запрос обновления.
//Внутри лямбда-выражения создаются и настраиваются все необходимые действия (HandleGetAction, HandleCreateAction и т.д.).
//Затем создается экземпляр ParseMessage1 с использованием ленивой инициализации.
//Наконец, возвращается новый экземпляр LazzyServer, инициализированный с созданным экземпляром ParseMessage1.