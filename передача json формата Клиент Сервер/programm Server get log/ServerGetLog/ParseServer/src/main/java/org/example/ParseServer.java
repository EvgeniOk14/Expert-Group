package org.example;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;
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
public class ParseServer
{
    private static ParseMessage parseMessage;

    public static void setParseMessage(ParseMessage parseMessage)
    {
        ParseServer.parseMessage = parseMessage;
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
            parseMessage.parseMessage(message, session); // метод парсит строку и производит действия в зависимости от результата

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
        Server server = new Server(8095); // Класс Server из библиотеки Jetty
                                               // отвечает за создание и управление HTTP/HTTPS
                                               // сервером.

        /**
         * WebSocketHandler - это абстрактный класс или интерфейс, предоставляемый библиотекой Jetty,
         * который упрощает создание и настройку WebSocket-сервера.
         * **/
        WebSocketHandler wsHandler = new WebSocketHandler() // Анонимный класс: new WebSocketHandler() { ... }
                // создает анонимный подкласс WebSocketHandler,
                // который управляет жизненным циклом (т.е. обработкой) WebSocket-соединений и
                // переопределяет точки для настройки, такие как метод configure
        {
            /**
             * configure(WebSocketServletFactory factory):
             * Этот метод вызывается Jetty для настройки WebSocketServletFactory.
             * Внутри этого метода регистрируется класс EchoServer как обработчик WebSocket-соединений.
             * **/
            @Override
            public void configure(WebSocketServletFactory factory) // WebSocketServletFactory: Отвечает за создание
            // и конфигурирование WebSocket-сервлетов.
            // Метод register(Class<?> endpoint) регистрирует класс,
            // содержащий аннотации WebSocket,
            // такие как @OnWebSocketConnect, @OnWebSocketMessage, и т.д.
            {
                factory.register(ParseServer.class); // регистрация класса EchoServer
            }
        };


        server.setHandler(wsHandler); // Метод setHandler устанавливает обработчик запросов для сервера Jetty.
        // Этот обработчик отвечает за обработку всех входящих HTTP-запросов
        // и WebSocket-соединений.

        // Инициализация ParseMessage
        HandleGetAction handleGetAction = new HandleGetAction();
        HandleCreateAction handleCreateAction = new HandleCreateAction();
        HandleUpdateAction handleUpdateAction = new HandleUpdateAction();
        HandleDeleteAction handleDeleteAction = new HandleDeleteAction();
        HandlePostAction handlePostAction = new HandlePostAction();
        HandleSaveAction handleSaveAction = new HandleSaveAction();
        HandleSendAction handleSendAction = new HandleSendAction();

        ParseMessage parseMessage = new ParseMessage(handleGetAction, handleCreateAction,
                                                     handleUpdateAction, handleDeleteAction,
                                                     handlePostAction, handleSaveAction,
                                                     handleSendAction);
        ParseServer.setParseMessage(parseMessage); // передача parseMessage в ParseServer



        try {
            server.start(); // запуск сервера
            server.join();  // Ожидание завершения работы сервера.
            // Метод join() в данном контексте используется для того,
            // чтобы текущий поток (в данном случае, поток,
            // выполняющий метод main) ждал завершения работы сервера.
        }
        catch (Exception e) // обработка исключений
        {
            e.printStackTrace(); // вывод в стандартный поток ошибок.
        }

    }
}




