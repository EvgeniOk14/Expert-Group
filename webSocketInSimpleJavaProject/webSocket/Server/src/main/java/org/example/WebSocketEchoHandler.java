package org.example;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;
import java.io.IOException;

    @WebSocket
    public class WebSocketEchoHandler
    {

        @OnWebSocketConnect
        public void onConnect(Session session)
        {
            System.out.println("Client connected: " + session.getRemoteAddress().getAddress());
        }

        @OnWebSocketMessage
        public void onMessage(Session session, String message) throws IOException
        {
            System.out.println("Received message from client: " + message);
            session.getRemote().sendString("Echo: " + message);
        }

        @OnWebSocketClose
        public void onClose(Session session, int statusCode, String reason)
        {
            System.out.println("Client disconnected: " + reason);
            System.out.println("Status code: " + statusCode);
        }


        public static void main(String[] args)
        {
            Server server = new Server(8086);
            WebSocketHandler wsHandler = new WebSocketHandler()
            {
                @Override
                public void configure(WebSocketServletFactory factory)
                {
                    factory.register(WebSocketEchoHandler.class);
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

