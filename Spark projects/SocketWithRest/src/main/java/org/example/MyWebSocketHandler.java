package org.example;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;// Класс обработчика WebSocket-соединения

@WebSocket
public class MyWebSocketHandler
{
    private static final Map<Session, String> sessions = new ConcurrentHashMap<>();

    @OnWebSocketConnect
    public void onConnect(Session session)
    {
        sessions.put(session, ""); // Добавляем новую сессию
        System.out.println("WebSocket Client connected: " + session.getRemoteAddress().getAddress());
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException
    {
        String[] array = message.split(" ");
        if (array.length > 3)
        {
            session.getRemote().sendString("Error:  Некорректный запрос!");
        }
        else
        {
            String boardNumber = array[2];
            findLogByBoardNumber(session, boardNumber);
        }
        System.out.println("Received message from client: " + message);
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason)
    {
        sessions.remove(session); // Удаляем закрытую сессию
        System.out.println("WebSocket Client disconnected: " + session.getRemoteAddress().getAddress());
    }

    @OnWebSocketError
    public void onError(Session session, Throwable throwable)
    {
        System.err.println("WebSocket Client error: " + throwable.getMessage());
    }


    public static void findLogByBoardNumber(Session session, String boardNumber) throws IOException {
        Path logDirPath = Paths.get("C:/Expert Group/LoggerLathe/logfiles"); // Путь к каталогу логов
        String fileNamePattern = "*_" + boardNumber + ".txt"; // Шаблон имени файла

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(logDirPath, fileNamePattern)) {
            boolean fileFound = false; // устанавливаем флаг false
            for (Path entry : stream) // итерируемся по потоку путей
            {
                String fileContent = Files.readString(entry, StandardCharsets.UTF_8); // Чтение содержимого файла
                session.getRemote().sendString(fileContent); // Отправка содержимого файла клиенту
                fileFound = true; // файл с номером нужной платы найден, устанавливаем флаг tru
                break; //  выход из цикла
            }

            if (!fileFound) // если файл с такой платой не найден
            {
                session.getRemote().sendString("Error: File for board number " + boardNumber + " not found."); // Сообщение об ошибке, если файл не найден
            }
        } catch (IOException e) // в случае ошибки обрабатываем исключение
        {
            session.getRemote().sendString("Error reading logs from files: " + e.getMessage()); // посылаем клиенту сообщение об ошибке
            e.printStackTrace(); // печатает стек ошибок
        }
    }
}
