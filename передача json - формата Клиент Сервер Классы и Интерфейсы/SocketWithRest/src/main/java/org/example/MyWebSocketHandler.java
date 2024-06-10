package org.example;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.example.interfaces.InterfaceMethods;
import org.example.maps.MethodRegistry;
import org.example.reflectionMethods.InvokeMethods;
import org.example.switchcasamethods.SwitchCaseMethods;
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

    private InvokeMethods invokeMethods;

    public MyWebSocketHandler()
    {
        this.invokeMethods = new InvokeMethods(); // Инициализация значением по умолчанию
    }

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
        System.out.println("Received message from client: " + message); // вывод в консоль сообщения

        RequestMessage requestMessage;

        try
        {
            requestMessage = gson.fromJson(message, RequestMessage.class); // использование класса RequestMessage для обработки входящих сообщений:
        }
        catch (JsonSyntaxException e)
        {
            System.err.println("Не удалось разобрать сообщение: " + message); // вывод в консоль для наглядности
            JsonObject errorResponse = new JsonObject();
            errorResponse.addProperty("status", "error");
            errorResponse.addProperty("message", "Недопустимый формат JSON ");
            session.getRemote().sendString(errorResponse.toString()); // отправка сообщения клиенту
            return;
        }

        /** зполучаем из Класса RequestMessage данные **/
        String method = requestMessage.getMethod();  // метод
        String entity = requestMessage.getEntity(); //  сущьность
        String data = requestMessage.getData();    //   данные

        JsonObject response = new JsonObject(); // создаём Json объект ответ

        try
        {
            String key = entity.toLowerCase(); // ключ это log
            InterfaceMethods handler = MethodRegistry.getMap(key); // получаем значение по ключу
                                                                  // т.е. класс сущности

            if (handler != null) // если значение не null, то:
            {
                InterfaceMethods.Args args = new InterfaceMethods.Args(session, data);
                invokeMethods.invokeMethod(handler, method, args, response);

                //invokeMethods.invokeMethod(handler, method, session, data, response);// вызов нужного метода с помощью рефлексии

                //SwitchCaseMethods.switchCaseMethod(handler, method, session, data, response);

            }
        }
        catch (Exception e)
        {
            System.err.println("Запрос на обработку ошибки: " + e.getMessage());
            response.addProperty("status", "error");
            response.addProperty("message", "Внутренняя ошибка сервера.");
            session.getRemote().sendString(response.toString());
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

    public static void getFile(Session session, String data) throws IOException {
        Path logDirPath = Paths.get("C:/Expert Group/LoggerLathe/logfiles"); // Путь к каталогу логов
        String fileNamePattern = "*_" + data + ".txt"; // Шаблон имени файла

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(logDirPath, fileNamePattern)) {
            boolean fileFound = false; // устанавливаем флаг false
            for (Path entry : stream) // итерируемся по потоку путей
            {
                JsonObject response = new JsonObject();
                response.addProperty("status", "success");
                response.addProperty("fileName", entry.getFileName().toString());

            }

            if (!fileFound) // если файл с такой платой не найден
            {
                session.getRemote().sendString("Error: File for board number " + data + " not found."); // Сообщение об ошибке, если файл не найден
            }
        } catch (IOException e) // в случае ошибки обрабатываем исключение
        {
            session.getRemote().sendString("Error reading logs from files: " + e.getMessage()); // посылаем клиенту сообщение об ошибке
            e.printStackTrace(); // печатает стек ошибок
        }
    }
}






