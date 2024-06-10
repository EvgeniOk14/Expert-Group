package org.example.controllers;

import static spark.Spark.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class CRUDController
{
    private static final String LOG_DIRECTORY = "C:\\Expert Group\\LoggerLathe\\logfiles";

    public static void main(String[] args)
    {
        // Путь к странице index.html
        staticFiles.location("/public");

        // Маршрут для отображения списка лог-файлов
        get("/getlog", (req, res) -> {
            List<String> logFiles = new ArrayList<>(); // Создается пустой список строк logFiles, который будет содержать имена файлов-логов.

            File directory = new File(LOG_DIRECTORY); // Этот объект представляет директорию, в которой находятся файлы-логи

            if (directory.exists() && directory.isDirectory()) { // Проверяется, существует ли указанный путь и является ли он директорией
                File[] files = directory.listFiles(); // Получение списка файлов в директории
                if (files != null) { // Если массив файлов не равен null
                    for (File file : files) { // Итерируемся по списку файлов
                        if (file.isFile() && file.getName().matches("\\d{4}_\\d{2}_\\d{2}_\\d{2}_\\d{2}_\\d{2}_\\w{8}.txt")) {
                            logFiles.add(file.getName()); // Добавление имени файла в список logFiles
                        }
                    }
                }
            }

            // Генерация HTML-кода для списка лог-файлов
            StringBuilder htmlBuilder = new StringBuilder("<html><body>");
            htmlBuilder.append("<header>Вывод всех ссылок на логи:</header>");
            htmlBuilder.append("<ul>");
            for (String logFile : logFiles) {
                htmlBuilder.append("<li><a href=\"/log/").append(logFile).append("\">").append(logFile).append("</a></li>");
            }
            htmlBuilder.append("</ul></body></html>");
            return htmlBuilder.toString();
        });

        // Маршрут для отображения содержимого лог-файла
        get("/log/:filename", (req, res) -> {
            String filename = req.params(":filename"); // Получаем имя файла из URL
            try {
                // Читаем содержимое файла и возвращаем его
                return new String(Files.readAllBytes(Paths.get(LOG_DIRECTORY, filename)));
            } catch (IOException e) {
                e.printStackTrace();
                return "Error reading file: " + filename; // Возвращаем сообщение об ошибке в случае исключения
            }
        });
    }
}

