package org.example.findfilesandsendbyconfiguration.configs;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.regex.Pattern;

/**
 * Этот класс, LogFileWebSocketHandler, является обработчиком WebSocket,
 * который предназначен для обработки текстовых сообщений от клиентов WebSocket
 * и предоставления им содержимого лог-файлов, хранящихся в определённой директории
 * **/
public class LogFileWebSocketHandler extends TextWebSocketHandler
{
    private static final String LOG_DIRECTORY = "C:\\Expert Group\\LoggerLathe\\logfiles";
    private static final Pattern LOG_FILE_PATTERN = Pattern.compile("\\d{4}_\\d{2}_\\d{2}_\\d{2}_\\d{2}_\\d{2}_\\w{8}.txt");

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        String payload = message.getPayload(); // получаем сообщение от клиента: get log 12345ABC
        System.out.println("!!!!!!!!!!! сообщение от клиента:  " + payload + "!!!!!!!!!!!");

        if (payload.startsWith("get log ")) // если сообщение начинается на: "get log "
        {
            String partialFileName = payload.substring(8); // обрезаем 8 символов, это: "get log "
            System.out.println("!!!!!!!! часть имени файла, т.е. номер платы: " + partialFileName+ "!!!!!!!!!!!!");

            File directory = new File(LOG_DIRECTORY);
            File[] files = directory.listFiles((dir, name) -> name.endsWith(partialFileName + ".txt") && LOG_FILE_PATTERN.matcher(name).matches());

            if (files != null && files.length > 0)
            {

                File logFile = files[0];  // существует только один файл с таким именем
                System.out.println("печатаем файл: " + logFile);
                if (logFile.exists() && logFile.isFile())
                {
                    System.out.println("проверка захода в блок if !!!!");
                    String logContent = new String(Files.readAllBytes(logFile.toPath()));
                    System.out.println("!!!!содержимое файла: " + logContent + "!!!!!!!! ");
                    session.sendMessage(new TextMessage(logContent));
                }
            }
            else
            {
                session.sendMessage(new TextMessage("Error: Log file not found or invalid format."));
            }
        }
        else
        {
            session.sendMessage(new TextMessage("Error: Invalid command."));
        }
    }
}
