package org.example.programmjavafx.Server;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

/**
 * обрабатывает websocket  соединения и сообщения
 * **/
public class WebSocketServer
{
    private Server server; // Jetty - легковесный HTTP-сервер обрабатывать входящие HTTP и WebSocket соединения

    // метод запуска сервера
    public void start() throws Exception
    {
        server = new Server(8095); // запускаем сервер на порту 8095

        // WebSocketHandler обработчик, который управляет WebSocket-соединениями
        // используется анонимный класс, который наследует WebSocketHandler
        // и переопределяет метод configure
        WebSocketHandler wsHandler = new WebSocketHandler()
        {
            // используется для настройки фабрики сервлетов WebSocket (WebSocketServletFactory).
            @Override
            public void configure(WebSocketServletFactory factory)
            {
                // зарегистрировать класс MyWebSocketHandler в качестве обработчика WebSocket-сообщений.
                factory.register(MyWebSocketHandler.class);
            }
        };
        server.setHandler(wsHandler); // устанавливаем wsHandler в качестве обработчика сообщений
        server.start(); // запускаем сервер
        System.out.println("Сервер для общения с клиентами стартовал на порту: 8095");
    }

    // метод остановки сервера
    public void stop() throws Exception
    {
        if (server != null)
        {
            server.stop();
        }
    }
}
