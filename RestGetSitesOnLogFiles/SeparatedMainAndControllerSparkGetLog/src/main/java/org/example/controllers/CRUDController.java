package org.example.controllers;

import static spark.Spark.get;
import spark.ModelAndView;
import spark.template.thymeleaf.ThymeleafTemplateEngine;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CRUDController
{
    /** установление путей и передача их в модель и d представление index.html **/
    public static void setupRoutes(String logDirectory)
    {
        get("/getlog", (req, res) ->
        {
            List<String> logFiles = new ArrayList<>(); // Создается пустой список строк logFiles, который будет содержать имена файлов-логов.

            File directory = new File(logDirectory); // Получение списка файлов в директории

            if (directory.exists() && directory.isDirectory())
            {
                File[] files = directory.listFiles();
                if (files != null)
                {
                    for (File file : files)
                    {
                        System.out.println("Found file: " + file.getName()); // вывод ля проверки
                        if (file.isFile() && file.getName().matches("\\d{4}_\\d{2}_\\d{2}_\\d{2}_\\d{2}_\\d{2}_\\w{8}\\.txt"))
                        {
                            logFiles.add(file.getName());
                            System.out.println("File matches regex: " + file.getName());
                        }
                        else
                        {
                            System.out.println("File does not match regex: " + file.getName());
                        }
                    }
                }
                else
                {
                    System.out.println("No files found in directory.");
                }
            }
            else
            {
                System.out.println("Directory does not exist or is not a directory.");
            }


            System.out.println("Log files: " + logFiles); // Отладочное сообщение

            // Подготовка данных для шаблона
            Map<String, Object> model = new HashMap<>(); // создаём модель ключ / значение
            model.put("logFiles", logFiles);  // передаём в модель найденные лог файлы
            model.put("baseURL", req.scheme() + "://" + req.host()); // http + :// + localhost:4667

            // Рендеринг HTML с использованием Thymeleaf
            try
            {
                // используется для рендеринга (отображения) HTML-страницы с использованием шаблонизатора Thymeleaf
                // ThymeleafTemplateEngine() интеграции Thymeleaf с веб-фреймворком Spark, класс отвечает за рендеринг HTML-шаблонов с использованием Thymeleaf
                // ModelAndView представляет собой модель и вид. Модель содержит данные, которые должны быть отображены в виде, а вид указывает на конкретный HTML-шаблон, который будет использоваться для отображения.
                // Метод render из ThymeleafTemplateEngine принимает объект ModelAndView и возвращает сгенерированную HTML-страницу в виде строки

                return new ThymeleafTemplateEngine().render(new ModelAndView(model, "index"));
            }
            catch (Exception e)
            {
                e.printStackTrace();
                res.status(500);
                return "Internal Server Error";
            }
        });

        /** получение логов файлов и вывод их в браузер **/
        get("/log/:filename", (req, res) ->
        {
            String filename = req.params(":filename");
            try {
                return new String(Files.readAllBytes(Paths.get(logDirectory, filename)));
            }
            catch (IOException e)
            {
                e.printStackTrace();
                return "Error reading file: " + filename;
            }
        });
    }
}



























//import java.io.File;
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.util.ArrayList;
//import java.util.List;
//import static spark.Spark.get;
//
//public class CRUDController {
//    public static void setupRoutes(String logDirectory) {
//        get("/getlog", (req, res) -> {
//            List<String> logFiles = new ArrayList<>();
//
//            File directory = new File(logDirectory);
//            if (directory.exists() && directory.isDirectory()) {
//                File[] files = directory.listFiles();
//                if (files != null) {
//                    for (File file : files) {
//                        if (file.isFile() && file.getName().matches("\\d{4}_\\d{2}_\\d{2}_\\d{2}_\\d{2}_\\d{2}_\\w{8}.txt")) {
//                            logFiles.add(file.getName());
//                        }
//                    }
//                }
//            }
//
//            StringBuilder htmlBuilder = new StringBuilder("<html><body>");
//            htmlBuilder.append("<header>Вывод всех ссылок на логи:</header>");
//            htmlBuilder.append("<ul>");
//            for (String logFile : logFiles) {
//                htmlBuilder.append("<li><a href=\"/log/").append(logFile).append("\">").append(logFile).append("</a></li>");
//            }
//            htmlBuilder.append("</ul></body></html>");
//            return htmlBuilder.toString();
//        });
//
//        get("/log/:filename", (req, res) -> {
//            String filename = req.params(":filename");
//            try {
//                return new String(Files.readAllBytes(Paths.get(logDirectory, filename)));
//            } catch (IOException e) {
//                e.printStackTrace();
//                return "Error reading file: " + filename;
//            }
//        });
//    }
//}
//
