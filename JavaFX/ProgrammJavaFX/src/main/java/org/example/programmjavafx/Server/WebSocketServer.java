package org.example.programmjavafx.Server;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;
import org.example.programmjavafx.LatheController;

public class WebSocketServer
{
    private Server server;

    private LatheController latheController; // добавил конструктор

    public WebSocketServer(LatheController latheController)
    {
        this.latheController = latheController;
    }

    public void start() throws Exception
    {
        server = new Server(8095);
        WebSocketHandler wsHandler = new WebSocketHandler()
        {
            @Override
            public void configure(WebSocketServletFactory factory)
            {
                factory.register(MyWebSocketHandler.class);
            }
        };
        server.setHandler(wsHandler);
        server.start();
        System.out.println("Server started on port 8095");
    }

    public void stop() throws Exception
    {
        if (server != null)
        {
            server.stop();
        }
    }
}

