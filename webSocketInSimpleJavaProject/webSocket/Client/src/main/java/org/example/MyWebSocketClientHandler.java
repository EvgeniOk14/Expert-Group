package org.example;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;

@WebSocket
public class MyWebSocketClientHandler
{
    private Session session;

    @OnWebSocketConnect
    public void onConnect(Session session)
    {
        System.out.println("Connected to server: " + session.getRemoteAddress().getAddress());
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message)
    {
        System.out.println("Received message from server: " + message);
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason)
    {
        System.out.println("Connection closed: " + statusCode + ", " + reason);
    }

    public Session getSession()
    {
        return this.session;
    }

    @OnWebSocketError
    public void onError(Session session, Throwable throwable)
    {
        System.err.println("Error: " + throwable.getMessage());
        throwable.printStackTrace();
    }

}


