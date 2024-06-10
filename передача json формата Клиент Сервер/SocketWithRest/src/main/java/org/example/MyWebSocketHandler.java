package org.example;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import java.io.File;
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

    private static final String LOG_DIRECTORY = "C:\\Expert Group\\LoggerLathe\\logfiles";

    Gson gson = new Gson();

    @OnWebSocketConnect
    public void onConnect(Session session)
    {
        sessions.put(session, "");
        System.out.println("WebSocket Client connected: " + session.getRemoteAddress().getAddress());
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException
    {
        System.out.println("Received message from client: " + message);

        RequestMessage requestMessage = gson.fromJson(message, RequestMessage.class);

        String method = requestMessage.getMethod();
        String entity = requestMessage.getEntity();
        String data = requestMessage.getData();

        JsonObject response = new JsonObject();

        boolean hasError = false;

        if (method == null || method.isEmpty())
        {
            response.addProperty("error", "Отсутствует метод method : " + method);
            session.getRemote().sendString(gson.toJson(response));
            hasError = true;
        }
        if (entity == null || entity.isEmpty())
        {
            response.addProperty("error", "Отсутствует сущьность entity : " + entity);
            session.getRemote().sendString(gson.toJson(response));
            hasError = true;
        }
        if (data == null || data.isEmpty())
        {
            response.addProperty("error", "Отсутствуют данные (номер платы boardNumber): " + data);
            session.getRemote().sendString(gson.toJson(response));
            hasError = true;
        }

        if (!hasError)
        {

            if ("get".equalsIgnoreCase(method))
            {
                if ("log".equalsIgnoreCase(entity))
                     {
                    try
                    {
                        String logContent = getLogFileContent(data);

                        if (logContent != null)
                        {
                            response.addProperty("data", logContent);
                        }
                        else
                        {
                            response.addProperty("error", "Лог-файл не найден!");
                        }
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
                else
                {
                    response.addProperty("error", "Незнакомая сущьность: " + entity);
                }
            }
            else
            {
                response.addProperty("error", "Незнакомый метод: " + method);
            }
            session.getRemote().sendString(gson.toJson(response));
        }

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

    private String getLogFileContent(String boardNumber) throws IOException {
        File directory = new File(LOG_DIRECTORY);
        File[] files = directory.listFiles();
        if (files != null)
        {
            for (File file : files)
            {
                // pcb
                if (file.isFile() && file.getName().startsWith("psblog_") && file.getName().endsWith(".txt") && file.getName().contains(boardNumber)) {
                    return Files.readString(file.toPath());
                }
            }
        }
        return null;
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
