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
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@WebSocket
public class EchoServer
{
    /** метод предназначен для выполнения кода при подключении клиента к серверу **/
    @OnWebSocketConnect
    public void onConnect(Session session)
    {
        System.out.println("Client connected: " + session.getRemoteAddress().getAddress());
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException
    {

        if (message != null && message.equals("get log"))
        {
            Path logDirPath = Paths.get("C:/Expert Group/LoggerLathe"); // предоставление пути к каталогу

            StringBuilder logs = new StringBuilder(); // создание объекта StringBuilder для накопления содержимого лог-файлов.

            try (DirectoryStream<Path> stream = Files.newDirectoryStream(logDirPath, "*.txt")) // создания нового объекта потока
                                                                                                    // который фильтрует файлы
                                                                                                    // по шаблону *.txt
            {
                for (Path item : stream) // итерационно проходит по каждому элементу (файлу item) в  потоке DirectoryStream.
                {
                    logs.append(Files.readString(item)).append("\n"); // Чтение и добавление содержимого каждого лог-файла
                }
                session.getRemote().sendString(logs.toString()); // отправляем содержимое всех файлов клиенту
            }
            catch (IOException e)
            {
                session.getRemote().sendString("Error reading logs from files: " + e.getMessage());
                e.printStackTrace();
            }
        }
        else
        {
            System.out.println("Received message from client: " + message); // вывод в консоль для наглядности
            session.getRemote().sendString("Echo: " + message); // отправка сообщения на сервер
        }
    }


    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason)
    {
        System.out.println("Client disconnected: " + reason);
        System.out.println("Status code: " + statusCode);
    }


    public static void main(String[] args)
    {
        Server server = new Server(8099);

        /**
         * WebSocketHandler - это абстрактный класс или интерфейс, предоставляемый библиотекой Jetty,
         * который упрощает создание и настройку WebSocket-сервера.
         * **/
        WebSocketHandler wsHandler = new WebSocketHandler() // Анонимный класс: new WebSocketHandler() { ... }
                // создает анонимный подкласс WebSocketHandler,
                // которы управляет жизненным циклом WebSocket-соединений и
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
                factory.register(EchoServer.class); // регистрация класса EchoServer
            }
        };
        server.setHandler(wsHandler); // Метод setHandler устанавливает обработчик запросов для сервера Jetty.
        // Этот обработчик отвечает за обработку всех входящих HTTP-запросов
        // и WebSocket-соединений.

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
