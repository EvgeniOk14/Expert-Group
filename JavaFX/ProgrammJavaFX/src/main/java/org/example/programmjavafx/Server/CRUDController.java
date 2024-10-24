package org.example.programmjavafx.Server;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.example.programmjavafx.HelloApplication;
import org.example.programmjavafx.Server.interfaces.InterfaceMethods;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import spark.ModelAndView;
import spark.template.thymeleaf.ThymeleafTemplateEngine;
import java.io.File;
import java.io.FileReader;
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
    private static final String LOG_DIRECTORY = "C:" + File.separator + "Expert Group" + File.separator + "JavaFX" + File.separator + "ProgrammJavaFX";
    //private static final String LOG_DIRECTORY = "C:\\Expert Group\\JavaFX\\ProgrammJavaFX";

    private static final String JSON_FILE_PATH = "C:" + File.separator + "Expert Group" + File.separator + "Json_projtcts" + File.separator + "untitled" + File.separator + "array.json";
    //private static final String JSON_FILE_PATH = "C:\\Expert Group\\Json_projtcts\\untitled\\array.json";

    private static JsonObject jsonObject;
    //endregion

    public static void main(String[] strings) throws IOException
    {
        port(8098); // Устанавливаем порт, на котором будет запущен сервер
        webSocket("/ws", MyWebSocketHandler.class); // Настраивает веб-сокет по URL /ws и связывает его с классом-обработчиком MyWebSocketHandler

        // Настройка Thymeleaf шаблонизатора
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver(HelloApplication.class.getClassLoader()); // Создаем ClassLoaderTemplateResolver, который будет использовать загрузчик классов приложения HelloApplication для поиска шаблонов.
        resolver.setTemplateMode(TemplateMode.HTML); // Устанавливаем режим шаблонов как HTML.
        resolver.setCharacterEncoding("UTF-8"); //  Устанавливаем кодировку шаблонов как UTF-8.
        resolver.setPrefix("/"); // Устанавливаем префикс для поиска шаблонов. Здесь префикс указывает на корень ресурсов.
        resolver.setSuffix(".html"); // Устанавливаем суффикс для шаблонов. Все шаблоны должны иметь расширение .html.

        // Создание и настройка Thymeleaf шаблонизатора
        TemplateEngine templateEngine = new TemplateEngine(); // Создаем объект TemplateEngine, который управляет процессом рендеринга шаблонов.
        templateEngine.setTemplateResolver(resolver); // Устанавливаем ранее созданный resolver как шаблонный резолвер для templateEngine.
        ThymeleafTemplateEngine engine = new ThymeleafTemplateEngine(resolver); //  Создаем объект ThymeleafTemplateEngine, который используется для интеграции с Spark Java и обработки шаблонов.

        jsonObject = initializeJsonObject(); // Инициализация переменной jsonObject

        InterfaceMethods.Args args = new InterfaceMethods.Args(); // Создание объекта для дальнейшего использования

        /**
         * метод get получает все лог-файлы из заданной директории
         * и передаёт их в модель, в представление доступное по адресу
         * http://localhost:8098/getlog
         * **/
        get("/getlog", (req, res) ->
        {
            List<String> logFiles = new ArrayList<>(); // создаём список имён лог-файлов
            File directory = new File(LOG_DIRECTORY); // directory — это объект File, который указывает на путь
            if (directory.exists() && directory.isDirectory()) // Проверяется, существует ли директория и является ли она директорией. FilenameFilter — это интерфейс, содержащий один метод accept(File dir, String name).
            {
                // Определяется фильтр, который проверяет, начинается ли имя файла с pcblog_ и заканчивается ли .txt.
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
                    for (File file : files) // итерируемся по списку файлов
                    {
                        System.out.println("!!!! выводим имена файлов перед записью в logFiles" + file.getName());
                        if (file.isFile())
                        {
                            logFiles.add(file.getName()); // Имена файлов, прошедших проверку фильтра и являющихся файлами (не директориями), добавляются в список logFiles
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

            System.out.println("!!!!!получаем из url имя файла filename: "+ filename + "!!!!!!!!!!!!!!!!");

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


        // Новый метод для получения текущего номера платы через REST API
        get("/currentPlateNumber", (req, res) ->
        {
            String currentPlateNumber = args.getPlateNumber(); // получаем номер платы
            if (currentPlateNumber != null)
            {
                return currentPlateNumber;
            }
            else
            {
                res.status(404);
                return "Номер платы не задан";
            }
        });

        /** метод получает элемент Json -файла по его индексу, переданному из URL пути **/
        get ("/getFeeder/:index", (req, res) ->
        {
            String index = req.params(":index"); // получаем из URL пути index

            return getFeeder(index); // возвращаем результат метода getFeeder(index)
        });

        // метод выполняет инициализацию сервера и приводит его в активное состояние, готовое принимать запросы.
        init(); // Это часть инфраструктуры Spark, которая заботится о том, чтобы все настройки маршрутов и обработчиков запросов были применены перед запуском сервера.
    }

    // метод выводит элемент Json-файла по его индексу
    public static String getFeeder(String index)
    {
        // проверяем, инициализирован ли JsonObject и содержит ли он ключ "array"
        if (jsonObject != null && jsonObject.has("array"))
        {
            // получаем значение связанное с ключом "array" из массива Json
            JsonArray jsonArray = jsonObject.getAsJsonArray("array");
            // возвращает элемент массива по указанному индексу
            return jsonArray.get(Integer.parseInt(index)).getAsString();
        }
        return "индекс не найден!";
    }

    // метод инициализирует Json объект, т.е. считывает данные по заданному пути и переводит их в Json-объект
    private static JsonObject initializeJsonObject()
    {
        // Пытаемся открыть файл для чтения с использованием try-with-resources,
        // чтобы FileReader автоматически закрывался после завершения работы.
        try(FileReader fileReader = new FileReader(JSON_FILE_PATH))
        {
            // Парсим содержимое файла в JsonObject
           jsonObject = JsonParser.parseReader(fileReader).getAsJsonObject();
           return jsonObject;  // Возвращаем полученный JsonObject
        }
        catch (IOException e)
        {
            e.printStackTrace();  // выводим стек-трейс ошибки
        }
        return null;
    }

    // метод читает находит файл по имени и возвращает его содержимое
    public static String getLogFileContent(String filename) throws IOException
    {
        System.out.println("!!! вывод filename: " + filename + "!!!!");
        File directory = new File(LOG_DIRECTORY); // создаём объект типа файл по указанному пути
        File[] files = directory.listFiles(); // получаем все файлы из директории
        if (files != null)
        {
            for (File file : files)
            {
                    if (file.isFile() && file.getName().contains(filename))
                {
                    return Files.readString(file.toPath()); // возвращает строку в виде содержимое файла
                }
            }
        }
        return null;
    }
}







//public static List<String> getAllLogFiles()
//{
//    List<String> logFiles = new ArrayList<>();
//    File directory = new File(LOG_DIRECTORY);
//    if (directory.exists() && directory.isDirectory())
//    {
//        File[] files = directory.listFiles();
//        if (files != null)
//        {
//            for (File file : files)
//            {
//                if (file.isFile() && file.getName().matches("\\d{4}_\\d{2}_\\d{2}_\\d{2}_\\d{2}_\\d{2}_\\w{8}.txt"))
//                {
//                    logFiles.add(file.getName());
//                }
//            }
//        }
//    }
//    return logFiles;
//}