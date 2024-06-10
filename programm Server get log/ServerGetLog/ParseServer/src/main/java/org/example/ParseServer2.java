package org.example;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.servlet.WebSocketCreator;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeRequest;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeResponse;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;
import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.example.service.ParseMessage;
import org.example.service.create.HandleCreateAction;
import org.example.service.delete.HandleDeleteAction;
import org.example.service.get.HandleGetAction;
import org.example.service.post.HandlePostAction;
import org.example.service.save.HandleSaveAction;
import org.example.service.send.HandleSendAction;
import org.example.service.update.HandleUpdateAction;
import java.io.IOException;

@WebSocket
public class ParseServer2
{
    private final ParseMessage parseMessage;

    public ParseServer2(ParseMessage parseMessage)
    {
        this.parseMessage = parseMessage;
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
        if (message != null && !message.trim().isEmpty())
        {
            parseMessage.parseMessage(message, session);
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

        // Инициализация ParseMessage
        HandleGetAction handleGetAction = new HandleGetAction();
        HandleCreateAction handleCreateAction = new HandleCreateAction();
        HandleUpdateAction handleUpdateAction = new HandleUpdateAction();
        HandleDeleteAction handleDeleteAction = new HandleDeleteAction();
        HandlePostAction handlePostAction = new HandlePostAction();
        HandleSaveAction handleSaveAction = new HandleSaveAction();
        HandleSendAction handleSendAction = new HandleSendAction();

        ParseMessage parseMessage = new ParseMessage(
                                                    handleGetAction,
                                                    handleCreateAction,
                                                    handleUpdateAction,
                                                    handleDeleteAction,
                                                    handlePostAction,
                                                    handleSaveAction,
                                                    handleSendAction
                                                    );

        WebSocketHandler wsHandler = new WebSocketHandler()
        {
            @Override
            public void configure(WebSocketServletFactory factory)
            {
                factory.setCreator(new ParseServerCreator(parseMessage));
            }
        };

        server.setHandler(wsHandler);

        try {
                server.start();
                server.join();
            }
        catch (Exception e)
            {
                e.printStackTrace();
            }
    }
}

class ParseServerCreator implements WebSocketCreator
{
    private final ParseMessage parseMessage;

    public ParseServerCreator(ParseMessage parseMessage)
    {
        this.parseMessage = parseMessage;
    }

    @Override
    public Object createWebSocket(ServletUpgradeRequest req, ServletUpgradeResponse resp)
    {
        return new ParseServer2(parseMessage);
    }
}




