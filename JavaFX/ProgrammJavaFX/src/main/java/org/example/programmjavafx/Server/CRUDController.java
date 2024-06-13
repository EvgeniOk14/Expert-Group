package org.example.programmjavafx.Server;

import org.example.programmjavafx.HelloApplication;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import spark.ModelAndView;
import spark.template.thymeleaf.ThymeleafTemplateEngine;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import static spark.Spark.*;

/**
 * клас запускающий сервер на порту 8098 и методы Rest Api
 * **/
public class CRUDController
{
    //region Fields
    private static final String LOG_DIRECTORY = "C:\\Expert Group\\LoggerLathe\\logfiles";
    //private static final String LOG_DIRECTORY = "C:\\Expert Group\\JavaFX\\ProgrammJavaFX\\";

    //endregion

    public static void main(String[] strings) throws IOException
    {
        port(8098);
        webSocket("/ws", MyWebSocketHandler.class);

        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver(HelloApplication.class.getClassLoader());
        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setCharacterEncoding("UTF-8");
        resolver.setPrefix("/");
        resolver.setSuffix(".html");

        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(resolver);
        ThymeleafTemplateEngine engine = new ThymeleafTemplateEngine(resolver);


        /**
         * метод get получает все лог-файлы из заданной директории
         * и передаёт их в модель, в представление доступное по адресу
         * http://localhost:8098/getlog
         * **/
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
                        return name.startsWith("pcblog_") && name.endsWith(".txt");
                    }
                };
                File[] files = directory.listFiles(logFileFilter); // возвращает массив файлов, находящихся в указанном  directory
                if (files != null)
                {
                    for (File file : files)
                    {
                        if (file.isFile())
                        {
                            logFiles.add(file.getName());
                        }
                    }
                }
            }
              Map<String, Object> model = new HashMap<>(); // создаём модель
              model.put("logFiles", logFiles); // передаём в модель список файлов
            return new ModelAndView(model, "index.html"); // возвращаем представление "index.html"
        }, engine);


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
                return "Лог-файл не найден!";
            }
        });
        init();
    }

    public static String getLogFileContent(String partialFileName) throws IOException
    {
        File directory = new File(LOG_DIRECTORY);
        File[] files = directory.listFiles();
        if (files != null)
        {
            for (File file : files)
            {
                if (file.isFile() && file.getName().startsWith("pcblog_") && file.getName().endsWith(".txt") && file.getName().contains(partialFileName))
                {
                    return Files.readString(file.toPath());
                }
            }
        }
        return null;
    }

    public static List<String> getAllLogFiles()
    {
        List<String> logFiles = new ArrayList<>();
        File directory = new File(LOG_DIRECTORY);
        if (directory.exists() && directory.isDirectory())
        {
            File[] files = directory.listFiles();
            if (files != null)
            {
                for (File file : files)
                {
                    if (file.isFile() && file.getName().matches("\\d{4}_\\d{2}_\\d{2}_\\d{2}_\\d{2}_\\d{2}_\\w{8}.txt"))
                    {
                        logFiles.add(file.getName());
                    }
                }
            }
        }
        return logFiles;
    }
}





//private static final Logger logger = LoggerFactory.getLogger(CRUDController.class);

//        import java.net.URL;
//        URL templateUrl = CRUDController.class.getClassLoader().getResource("index.html");
//        if (templateUrl != null) {
//            System.out.println("Template file exists" + templateUrl.getFile());
//        } else {
//            System.out.println("Template file does not exist");
//        }
//        Enumeration<URL> urls = CRUDController.class.getClassLoader().getResources("");
//        while (urls.hasMoreElements()) {
//            System.out.println(urls.nextElement());
//        }
