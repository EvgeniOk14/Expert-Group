package org.example.programmjavafx.Server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.example.programmjavafx.Server.interfaces.InterfaceMethods;
import org.example.programmjavafx.Server.maps.MethodRegistry;
import org.example.programmjavafx.Server.reflectionMethods.InvokeMethods;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@WebSocket
public class MyWebSocketHandler
{
    private static final String LOG_DIRECTORY = "C:\\Expert Group\\LoggerLathe\\logfiles";

    //private static final String LOG_DIRECTORY = "C:\\Expert Group\\JavaFX\\ProgrammJavaFX\\";

    private static final Map<Session, String> sessions = new ConcurrentHashMap<>();

    private final InvokeMethods invokeMethods = new InvokeMethods();


    private static String plateNumber;

    Gson gson = new Gson();

    @OnWebSocketConnect
    public void onConnect(Session session)
    {
        System.out.println("Метод onConnect в классе MyWebSocketHandler вызван в: " + new Date(System.currentTimeMillis()));

        String userName = "Клиент, с URL: " + session.getRemoteAddress().getAddress(); // создаём именя клиента используя его адрес

        sessions.put(session, userName);

        System.out.println("Присоединён новый WebsocketClient, URL: " + session.getRemoteAddress().getAddress());
    }


    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException
    {
        System.out.println("От клиента c URL: " + sessions.get(session) + "получено сообщение: " + message);

        RequestMessage requestMessage;

        try
        {
                // использование класса RequestMessage для обработки входящих сообщений:
                requestMessage = gson.fromJson(message, RequestMessage.class);
                System.out.println("Parsed request message: method="
                                    + requestMessage.getMethod()
                                    + ", entity="
                                    + requestMessage.getEntity()
                                    + ", data="
                                    + requestMessage.getData());
        }
        catch (JsonSyntaxException e)
        {
            System.err.println("Не удалось разобрать сообщение: " + message);
            JsonObject errorResponse = new JsonObject();
            errorResponse.addProperty("status", "error");
            errorResponse.addProperty("message", "Недопустимый формат JSON");
            session.getRemote().sendString(sessions.get(session) + errorResponse.toString());
            return;
        }

        /** зполучаем из Класса RequestMessage данные **/
        String method = requestMessage.getMethod();
        String entity = requestMessage.getEntity();
        String data = requestMessage.getData();

        JsonObject response = new JsonObject();

        if(method == null)
        {
            response.addProperty("status", "error");
            response.addProperty("ошибка", "в сообщении клиента отсутствует метод!");
            session.getRemote().sendString(response.toString());
        }
        if(entity == null)
        {
            response.addProperty("status", "error");
            response.addProperty("ошибка", "в сообщении клиента отсутствует сущьность entity!");
            session.getRemote().sendString(response.toString());
        }

        if (data != null)
        {
            setPlateNumber(data);
            System.out.println("Установлен номер платы: " + plateNumber);
        } else {
            response.addProperty("статус ", "ощибка");
            response.addProperty("ответ ", "в сообщении от клиента, либо отстутствуют данные, либо Неизвестные данные (номер платы): " + entity);
            session.getRemote().sendString(sessions.get(session) + response.toString());
        }

        try
        {
            String key = entity.toLowerCase();
            InterfaceMethods handler = MethodRegistry.getMap(key);

            if (handler != null)
            {
                InterfaceMethods.Args args = new InterfaceMethods.Args(session, data);
                invokeMethods.invokeMethod(handler, method, args, response);
            }
        }
        catch (Exception e)
        {
            System.err.println("Запрос на обработку ошибки: " + e.getMessage());
            response.addProperty("статус ", "ошибка");
            response.addProperty("ответ ", "Внутренняя ошибка сервера.");
            session.getRemote().sendString(sessions.get(session) + response.toString());
        }
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason)
    {
        sessions.remove(session);
        System.out.println("разорвано соединение с (WebSocket Client) клиентом: " + session.getRemoteAddress().getAddress());
    }

    @OnWebSocketError
    public void onError(Session session, Throwable throwable)
    {
        System.err.println("Ошибка соединения с (WebSocket Client) Клиентом: " + throwable.getMessage());
    }


    public void setPlateNumber(String data)
    {
        this.plateNumber = data;
        System.out.println("в методе setPlateNumber(String data) установили plateNumber " + plateNumber);
    }

    public String getPlateNumber()
    {
        return plateNumber;
    }

    public static void findLogByBoardNumber(Session session, String boardNumber) throws IOException
    {
        Path logDirPath = Paths.get("C:/Expert Group/LoggerLathe/logfiles"); // Путь к каталогу логов

        //Path logDirPath = Paths.get("C:\\Expert Group\\JavaFX\\ProgrammJavaFX\\");

        String fileNamePattern = "*_" + boardNumber + ".txt"; // Шаблон имени файла

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(logDirPath, fileNamePattern))
        {
            boolean fileFound = false; // устанавливаем флаг false
            for (Path entry : stream) // итерируемся по потоку путей
            {
                String fileContent = Files.readString(entry, StandardCharsets.UTF_8); // Чтение содержимого файла
                System.out.println("контент файла: " + fileContent);
                session.getRemote().sendString(fileContent); // Отправка содержимого файла клиенту
                fileFound = true; // файл с номером нужной платы найден, устанавливаем флаг tru
                break; //  выход из цикла
            }

            if (!fileFound) // если файл с такой платой не найден
            {
                System.out.println("файл не найден с такой платой: " + boardNumber);
                session.getRemote().sendString("Ошибка: Файл с таким номером платы: " + boardNumber + " - не найден!"); // Сообщение об ошибке, если файл не найден
            }
        }
        catch (IOException e) // в случае ошибки обрабатываем исключение
        {
            session.getRemote().sendString("Ошибка чтения содержимого из файлов: " + e.getMessage()); // посылаем клиенту сообщение об ошибке
            e.printStackTrace(); // печатает стек ошибок
        }

    }

    public static void getFile(Session session, String data) throws IOException
    {

        Path logDirPath = Paths.get("C:/Expert Group/LoggerLathe/logfiles"); // Путь к каталогу логов

        //Path logDirPath = Paths.get("C:\\Expert Group\\JavaFX\\ProgrammJavaFX\\");

        String fileNamePattern = "*_" + data + ".txt"; // Шаблон имени файла

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(logDirPath, fileNamePattern)) {
            boolean fileFound = false; // устанавливаем флаг false
            for (Path entry : stream) // итерируемся по потоку путей
            {
                JsonObject response = new JsonObject();
                response.addProperty("status", "success");
                response.addProperty("fileName", entry.getFileName().toString());
                session.getRemote().sendString(response.toString());

            }

            if (!fileFound) // если файл с такой платой не найден
            {
                session.getRemote().sendString("Ошибка: файл с таким номером платы: " + data + " - не найден!"); // Сообщение об ошибке, если файл не найден
            }
        } catch (IOException e) // в случае ошибки обрабатываем исключение
        {
            session.getRemote().sendString("Ошибка чтения содержимого из файлов: " + e.getMessage()); // посылаем клиенту сообщение об ошибке
            e.printStackTrace(); // печатает стек ошибок
        }
    }

    private String getLogFileContent(String boardNumber) throws IOException
    {
        File directory = new File(LOG_DIRECTORY);
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().startsWith("pcblog_") && file.getName().endsWith(".txt") && file.getName().contains(boardNumber)) {
                    return Files.readString(file.toPath());
                }
            }
        }
        return null;
    }

    /** метод обходит все сессии и отправляет сообщения всем подключённым клиентам **/
    public static void broadcastMessage(String message)
    {
        for (Map.Entry<Session, String> currentSession : sessions.entrySet())
        {
            Session session = currentSession.getKey();
            String userName = currentSession.getValue();
            if (session.isOpen())
            {
                try
                {
                    session.getRemote().sendString(userName + ": " + message);
                    System.out.println(userName + ": " + message); // Логирование сообщения с именем пользователя
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }



















//    public static void broadcastMessage(String message)
//    {
//        for (Session session : sessions.keySet())
//        {
//            if (session.isOpen())
//            {
//                try
//                {
//                    session.getRemote().sendString(message);
//                }
//                catch (IOException e)
//                {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
}


