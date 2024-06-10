package org.example.programmjavafx.Server;

import org.example.programmjavafx.LatheController;
import spark.ModelAndView;
import spark.template.thymeleaf.ThymeleafTemplateEngine;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import static spark.Spark.*;

public class CRUDController
{
    private static LatheController latheController;
    private static final String LOG_DIRECTORY = "C:\\Expert Group\\LoggerLathe\\logfiles";

    public static void setLatheController(LatheController controller) {
        System.out.println("Метод setLatheController в классе CRUDController вызван в: " + new Date(System.currentTimeMillis()));
        latheController = controller;
    }

    public static void main(String[] args)
    {
        System.out.println("Метод main в классе CRUDController вызван в: " + new Date(System.currentTimeMillis()));

        if (latheController == null) {
            throw new IllegalStateException("LatheController is not set");
        }

        port(8098);
        webSocket("/ws", MyWebSocketHandler.class);
        staticFiles.location("/public");

        // Маршрут для отображения списка лог-файлов
        get("/getlog", (req, res) ->
        {
            List<String> logFiles = new ArrayList<>();
            File directory = new File(LOG_DIRECTORY);
            if (directory.exists() && directory.isDirectory())
            {
                FilenameFilter logFileFilter = new FilenameFilter()
                {
                    @Override
                    public boolean accept(File dir, String name)
                    {
                        // Проверяем, начинается ли имя файла с "psblog_" и заканчивается ли ".txt"
                        return name.startsWith("psblog_") && name.endsWith(".txt");
                    }
                };
                File[] files = directory.listFiles(logFileFilter); // передаём logFileFilter
                if (files != null)
                {
                    for (File file : files)
                    {
                        if (file.isFile()) // && file.getName().matches("\\d{4}_\\d{2}_\\d{2}_\\d{2}_\\d{2}_\\d{2}_\\w{8}.txt"))
                        {
                            logFiles.add(file.getName());
                        }
                    }
                }
            }
            Map<String, Object> model = new HashMap<>();
            model.put("logFiles", logFiles);
            return new ModelAndView(model, "index"); // "index" - это имя шаблона Thymeleaf
        }, new ThymeleafTemplateEngine());


        /**
         *  Этот метод отвечает за обработку GET запросов к определенному лог-файлу.
         *  Он извлекает имя файла из параметра маршрута, читает содержимое файла и возвращает его клиенту.
         *  Если файл не найден, возвращается ошибка 404.
         *  **/
        get("/log/:filename", (req, res) ->
        {
            String filename = req.params(":filename"); // извлекает значение параметра filename
            String fileContent = getLogFileContent(filename); // чтение содержимого файла
            if (fileContent != null)
            {
                res.type("text/plain; charset=UTF-8"); // ответ будет содержать обычный текст
                return fileContent; // возвращает содержимое файла клиенту
            }
            else
            {
                res.status(404);
                return "Log file not found";
            }
        });

        init();
    }


    public static String getLogFileContent(String partialFileName) throws IOException {
        System.out.println("Метод getLogFileContent в классе CRUDController вызван в: " + new Date(System.currentTimeMillis()));
        File directory = new File(LOG_DIRECTORY);
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().startsWith("psblog_") && file.getName().endsWith(".txt") && file.getName().contains(partialFileName)) {
                    return Files.readString(file.toPath());
                }
            }
        }
        return null;
    }

    public static List<String> getAllLogFiles() {
        List<String> logFiles = new ArrayList<>();
        File directory = new File(LOG_DIRECTORY);
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && file.getName().matches("\\d{4}_\\d{2}_\\d{2}_\\d{2}_\\d{2}_\\d{2}_\\w{8}.txt")) {
                        logFiles.add(file.getName());
                    }
                }
            }
        }
        return logFiles;
    }

    public static LatheController getLatheController() {
        System.out.println("Метод getLatheController() в классе CRUDController вызван в: " + new Date(System.currentTimeMillis()));
        return latheController;
    }
}




//        /**
//         * Маршрут для отображения списка лог-файлов
//         * **/
//        get("/getlog", (req, res) ->
//        {
//            System.out.println("Метод get в классе CRUDController вызван в: " + new Date(System.currentTimeMillis()));
//            List<String> logFiles = getAllLogFiles();
//            Map<String, Object> model = new HashMap<>();
//            model.put("logFiles", logFiles);
//            return new ModelAndView(model, "index");
//        }, new ThymeleafTemplateEngine());


//        /**
//         *  Этот метод отвечает за обработку GET запросов к определенному лог-файлу.
//         *  Он извлекает имя файла из параметра маршрута, читает содержимое файла и возвращает его клиенту.
//         *  Если файл не найден, возвращается ошибка 404.
//         *  **/
//        get("/log/:filename", (req, res) -> {
//            System.out.println("Метод getLog в классе CRUDController вызван в: " + new Date(System.currentTimeMillis()));
//            String filename = req.params(":filename");
//            String fileContent = getLogFileContent(filename);
//            if (fileContent != null) {
//                res.type("text/plain; charset=UTF-8");
//                return fileContent;
//            } else {
//                res.status(404);
//                return "Log file not found";
//            }
//        });
//
//        init();
//    }