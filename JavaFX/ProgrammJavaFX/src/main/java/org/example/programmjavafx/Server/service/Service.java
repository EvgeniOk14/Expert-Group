package org.example.programmjavafx.Server.service;

import com.google.gson.JsonObject;
import org.eclipse.jetty.websocket.api.Session;
import org.example.programmjavafx.Server.CRUDController;
import org.example.programmjavafx.Server.definitionOfPath.GetPlatformSpecificPath;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

/**
 * Класс Service
 * содержит методы для работы с логами
 * **/
public class Service
{
    /**
     * метод ищет лог-файл по номеру платы
     **/
    public static void findLogByBoardNumber(Session session, String boardNumber) throws IOException {

        Path logDirPath = GetPlatformSpecificPath.getPlatformSpecificPath("C:", "Expert Group", "JavaFX", "ProgrammJavaFX"); // Путь к каталогу логов

        String fileNamePattern = "*_" + boardNumber + ".txt"; // поиск по шаблону по имени файла

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
        } catch (IOException e) // в случае ошибки обрабатываем исключение
        {
            session.getRemote().sendString("Ошибка чтения содержимого из файлов: " + e.getMessage()); // посылаем клиенту сообщение об ошибке
            e.printStackTrace(); // печатает стек ошибок
        }

    }

    /**
     * метод получения имени лог-файла
     **/
    public static void getLogFile(Session session, String data, String path) throws IOException
    {
        Path logDirPath = GetPlatformSpecificPath.getPlatformSpecificPath(path.split(FileSystems.getDefault().getSeparator()));

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
        }
        catch (IOException e) // в случае ошибки обрабатываем исключение
        {
            session.getRemote().sendString("Ошибка чтения содержимого из файлов: " + e.getMessage()); // посылаем клиенту сообщение об ошибке
            e.printStackTrace(); // печатает стек ошибок
        }
    }

    /**
     *  метод проверяет число на превышение максимального индекса массива
     *  **/
    public static void checkNumber(String data, int MaxNnumber, Session session) throws IOException {
        try
        {
            int parsedNumber = Integer.parseInt(data);
            if (parsedNumber < MaxNnumber)
            {
                String number = CRUDController.getFeeder(data);
                System.out.println("вывод значения из json массива:" + number);
            }
            else
            {
                session.getRemote().sendString("Число превышает максимальный  индекс массимва! ");
                System.out.println("номер превышает максимальный индекс массива! ");
            }
        }
        catch (NumberFormatException e)
        {
            session.getRemote().sendString(e.getMessage());
        }
    }

    /**
     * метод находитлог-файл по заданному пути,
     * считывает его содержимое
     * и возвращает содержимое найденного файла
     *  **/
    public static String getFileContentByWebSocketClientMessage(String filePath) throws IOException
    {
        System.out.println("Чтение файла по пути: " + filePath); // выводим путь в терминал

        File file = new File(filePath); // проверяем, что файл существует
        if (!file.exists())
        {
            System.err.println("Ошибка: файл не существует по пути: " + filePath);
            return null;
        }
        else if (!file.isFile()) // проверяем что файл - это файл
        {
            System.err.println("Ошибка: путь не является файлом: " + filePath);
            return null;
        }

        String content = Files.readString(Path.of(filePath), StandardCharsets.UTF_8); // читаем содержимое файла
        System.out.println("Файл прочитан успешно."); // выод в консоль об успешном чтении содержимого файла
        return content; // возвращаем содержимое найденного файла
    }

    /** метод ищет json-файл по его имени **/
    public static void getJsonFile(Session session, String data) throws IOException {
        try
            {
                JsonObject response = new JsonObject();
                response.addProperty("имя файла: ", data);
                session.getRemote().sendString(response.toString());
            }
        catch (IOException e)
            {
                session.getRemote().sendString(e.getMessage());
            }
    }
}