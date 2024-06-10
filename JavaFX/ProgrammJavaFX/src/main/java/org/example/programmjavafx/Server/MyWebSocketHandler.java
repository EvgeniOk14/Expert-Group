package org.example.programmjavafx.Server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.example.programmjavafx.LatheController;
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

    private static final Map<Session, String> sessions = new ConcurrentHashMap<>();

    private InvokeMethods invokeMethods;

    private LatheController controller;

    private static String plateNumber;


    public MyWebSocketHandler()
    {
        this.invokeMethods = new InvokeMethods();
        this.controller = CRUDController.getLatheController(); // Ensure controller is fetched correctly
    }

    Gson gson = new Gson();

    @OnWebSocketConnect
    public void onConnect(Session session)
    {
        System.out.println("Метод onConnect в классе MyWebSocketHandler вызван в: " + new Date(System.currentTimeMillis()));

        sessions.put(session, "");
        System.out.println("WebSocket Client connected: " + session.getRemoteAddress().getAddress());
    }


    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException
    {

        System.out.println("Метод onMessage в классе MyWebSocketHandler вызван в: " + new Date(System.currentTimeMillis()));
        System.out.println("Received message from client: " + message);

        RequestMessage requestMessage;

        try
        {

                // использование класса RequestMessage для обработки входящих сообщений:
                requestMessage = gson.fromJson(message, RequestMessage.class);
        }
        catch (JsonSyntaxException e)
        {
            System.err.println("Не удалось разобрать сообщение: " + message);
            JsonObject errorResponse = new JsonObject();
            errorResponse.addProperty("status", "error");
            errorResponse.addProperty("message", "Недопустимый формат JSON");
            session.getRemote().sendString(errorResponse.toString());
            return;
        }

        /** зполучаем из Класса RequestMessage данные **/
        String method = requestMessage.getMethod();
        String entity = requestMessage.getEntity();
        String data = requestMessage.getData();

        JsonObject response = new JsonObject();

        if (data != null)
        {
            setPlateNumber(data);
            System.out.println("Plate number set: " + plateNumber);
        } else {
            response.addProperty("status", "error");
            response.addProperty("message", "Неизвестная сущность: " + entity);
            session.getRemote().sendString(response.toString());
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
            response.addProperty("status", "error");
            response.addProperty("message", "Внутренняя ошибка сервера.");
            session.getRemote().sendString(response.toString());
        }
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason)
    {
        System.out.println("Метод onClose в классе MyWebSocketHandler вызван в: " + new Date(System.currentTimeMillis()));
        sessions.remove(session);
        System.out.println("WebSocket Client disconnected: " + session.getRemoteAddress().getAddress());
    }

    @OnWebSocketError
    public void onError(Session session, Throwable throwable)
    {
        System.out.println("Метод onError в классе MyWebSocketHandler вызван в: " + new Date(System.currentTimeMillis()));
        System.err.println("WebSocket Client error: " + throwable.getMessage());
    }


    public void setPlateNumber(String data)
    {
        System.out.println("Метод setPlateNumber в классе MyWebSocketHandler вызван в: " + new Date(System.currentTimeMillis()));
        this.plateNumber = data;

        System.out.println("в методе setPlateNumber(String data) установили plateNumber " + plateNumber);

        //controller.updatePlateNumber(plateNumber);
    }

    public String getPlateNumber()
    {
        System.out.println("Метод getPlateNumber в классе MyWebSocketHandler вызван в: " + new Date(System.currentTimeMillis()));
        return plateNumber;
    }

    public static void findLogByBoardNumber(Session session, String boardNumber) throws IOException {
        long currentTime = System.currentTimeMillis();
        System.out.println("Метод findLogByBoardNumber в классе MyWebSocketHandler() вызван в: " + new Date(currentTime));

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
        long currentTime = System.currentTimeMillis();
        System.out.println("Метод getFile в классе MyWebSocketHandler() вызван в: " + new Date(currentTime));

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

    private String getLogFileContent(String boardNumber) throws IOException
    {
        File directory = new File(LOG_DIRECTORY);
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().startsWith("psblog_") && file.getName().endsWith(".txt") && file.getName().contains(boardNumber)) {
                    return Files.readString(file.toPath());
                }
            }
        }
        return null;
    }

}


//private static List<String> receivedMessages = Collections.synchronizedList(new ArrayList<>());


//    @OnWebSocketMessage
//    public void onMessage(Session session, String message) throws IOException
//    {
//        // Сохранение всех сообщений в один список
//        receivedMessages.add(message);
//
//        System.out.println("Метод onMessage в классе MyWebSocketHandler вызван в: " + new Date(System.currentTimeMillis()));
//        System.out.println("Received message from client: " + message);
//
//        // Обработка каждого сообщения по очереди
//        synchronized (receivedMessages)
//        {
//            for (String msg : receivedMessages) {
//                processMessage(session, msg);
//            }
//        }
//    }
//
//    private void processMessage(Session session, String message) throws IOException
//    {
//        RequestMessage requestMessage;
//
//        try
//        {
//            requestMessage = gson.fromJson(message, RequestMessage.class);
//        }
//        catch (JsonSyntaxException e)
//        {
//            System.err.println("Не удалось разобрать сообщение: " + message);
//            JsonObject errorResponse = new JsonObject();
//            errorResponse.addProperty("status", "error");
//            errorResponse.addProperty("message", "Недопустимый формат JSON");
//            session.getRemote().sendString(errorResponse.toString());
//            return;
//        }
//
//        // Получаем из класса RequestMessage данные
//        String method = requestMessage.getMethod();
//        String entity = requestMessage.getEntity();
//        String data = requestMessage.getData();
//
//        JsonObject response = new JsonObject();
//
//        if (data != null)
//        {
//            setPlateNumber(data);
//            System.out.println("Plate number set: " + plateNumber);
//        } else {
//            response.addProperty("status", "error");
//            response.addProperty("message", "Неизвестная сущность: " + entity);
//            session.getRemote().sendString(response.toString());
//        }
//
//        try {
//            String key = entity.toLowerCase();
//            InterfaceMethods handler = MethodRegistry.getMap(key);
//
//            if (handler != null)
//            {
//                InterfaceMethods.Args args = new InterfaceMethods.Args(session, data);
//                invokeMethods.invokeMethod(handler, method, args, response);
//            }
//        }
//        catch (Exception e)
//        {
//            System.err.println("Запрос на обработку ошибки: " + e.getMessage());
//            response.addProperty("status", "error");
//            response.addProperty("message", "Внутренняя ошибка сервера.");
//            session.getRemote().sendString(response.toString());
//        }
//    }
//






