package org.example.service.get;

import org.eclipse.jetty.websocket.api.Session;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class HandleGetAction
{
    public void handleGetAction(Session session, String action, String parameter) throws IOException
    {
        switch (action)
        {
            case "log":
                findLogByBoardNumber(session, parameter);
                break;
            case "data":
                System.out.println(action);
                break;
            case "file":
                System.out.println(action);
                break;
            case "directory":
                System.out.println(action);
                break;
            case "path":
                System.out.println(action);
                break;
            default:
                session.getRemote().sendString("Error: Unknown action " + action + ".");
        }
    }

    private void findLogByBoardNumber(Session session, String boardNumber) throws IOException
    {
        Path logDirPath = Paths.get("C:/Expert Group/LoggerLathe"); // Путь к каталогу логов
        String fileNamePattern = "*_" + boardNumber + ".txt"; // Шаблон имени файла

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(logDirPath, fileNamePattern))
        {
            boolean fileFound = false; // устанавливаем флаг false
            for (Path entry : stream) // итерируемся по потоку путей
            {
                String fileContent = Files.readString(entry); // Чтение содержимого файла
                session.getRemote().sendString(fileContent); // Отправка содержимого файла клиенту
                fileFound = true; // файл с номером нужной платы найден, устанавливаем флаг tru
                break; //  выход из цикла
            }

            if (!fileFound) // если файл с такой платой не найден
            {
                session.getRemote().sendString("Error: File for board number " + boardNumber + " not found."); // Сообщение об ошибке, если файл не найден
            }
        }
        catch (IOException e) // в случае ошибки обрабатываем исключение
        {
            session.getRemote().sendString("Error reading logs from files: " + e.getMessage()); // посылаем клиенту сообщение об ошибке
            e.printStackTrace(); // печатает стек ошибок
        }
    }
}
