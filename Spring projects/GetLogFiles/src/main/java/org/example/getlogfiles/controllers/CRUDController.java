package org.example.getlogfiles.controllers;

import org.example.getlogfiles.service.LogFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Controller
public class CRUDController
{
    @Autowired
    private LogFileService logFileService;

    private static final String LOG_DIRECTORY = "C:\\Expert Group\\LoggerLathe\\logfiles";

    @GetMapping("/getlog")
    public String index(Model model)
    {
        List<String> logFiles = logFileService.getAllLogFiles();
        model.addAttribute("logFiles", logFiles);
        return "index";
    }


    @GetMapping("/log/{filename}")
    @ResponseBody
    public String viewLogFile(@PathVariable String filename)
    {
        try
        {
            return new String(Files.readAllBytes(Paths.get(LOG_DIRECTORY, filename)));
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return "Error reading file: " + filename;
        }
    }
}












//    @GetMapping("/getlog")
//    public String index(Model model)
//    {
//        List<String> logFiles = new ArrayList<>();
//
//        File directory = new File(LOG_DIRECTORY);
//        if (directory.exists() && directory.isDirectory())
//        {
//            File[] files = directory.listFiles();
//            if (files != null)
//            {
//                for (File file : files)
//                {
//                    if (file.isFile() && file.getName().matches("\\d{4}_\\d{2}_\\d{2}_\\d{2}_\\d{2}_\\d{2}_\\w{8}.txt")) {
//                        logFiles.add(file.getName());
//                    }
//                }
//            }
//        }
//        model.addAttribute("logFiles", logFiles);
//        return "index";
//    }






//    @Autowired
//    private LogFileService logFileService;
//
//    private static final String LOG_DIRECTORY = "C:\\Expert Group\\LoggerLathe\\logfiles";
//
//    @GetMapping("/getlog")
//    public String index(Model model)
//    {
//        List<String> logFiles = new ArrayList<>(); // Создается пустой список строк logFiles, который будет содержать имена файлов-логов.
//
//        File directory = new File(LOG_DIRECTORY); // Этот объект представляет директорию, в которой находятся файлы-логи
//
//        if (directory.exists() && directory.isDirectory()) // Проверяется, существует ли указанный путь и является ли он директорией
//        {
//            File[] files = directory.listFiles(); // Получение списка файлов в директории Метод listFiles возвращает массив объектов File
//
//            if (files != null) // если массив файлов равен null, то:
//            {
//                for (File file : files) // итерируемся по списку файлов
//                {
//                    //if (file.isFile() && file.getName().matches("\\d{4}_\\d{2}_\\d{2}_\\d{2}_\\d{2}_\\d{2}_\\d{5}\\w{3}.txt"))
//                    // if (file.isFile() && file.getName().matches("\\d{4}_\\d{2}_\\d{2}_\\d{2}_\\d{2}_\\d{2}_.+.txt"))
//                    if (file.isFile() && file.getName().matches("\\d{4}_\\d{2}_\\d{2}_\\d{2}_\\d{2}_\\d{2}_\\w{8}.txt"))
//                    {
//                        logFiles.add(file.getName()); // Добавление имени файла в список logFiles
//
//                    }
//                }
//            }
//        }
//        model.addAttribute("logFiles", logFiles);
//        return "index";
//    }
//
//
//    @GetMapping("/log/{filename}")
//    @ResponseBody
//    public String viewLogFile(@PathVariable String filename) // filename будет взято из части URL.
//    {
//        try
//        {
//            return new String(Files.readAllBytes(Paths.get(LOG_DIRECTORY, filename)));
//
//            // Метод Paths.get создает объект Path, представляющий путь к файлу.
//            // путь составляется из двух частей: константы LOG_DIRECTORY и переменной filename
//
//            // Files.readAllBytes читает все байты из файла, путь к которому указан в аргументе.
//            // Этот метод возвращает массив байтов (byte[]).
//
//            // Конструктор String преобразует массив байтов в строку. Таким образом,
//            // содержимое файла читается как массив байтов и затем преобразуется в строку,
//            // которую метод возвращает
//
//        }
//        catch (IOException e) // исключение выбрасывается в случае проблем с чтением файла
//        {
//            e.printStackTrace(); // Выводит стек вызовов исключения в стандартный поток ошибок.
//
//            return "Error reading file: " + filename;
//        }
//    }
//}




//    @GetMapping("/getlog")
//    public String index1(Model model)
//    {
//        try {
//            List<String> logFiles = Files.list(Paths.get(LOG_DIRECTORY))
//                    .filter(Files::isRegularFile)
//                    .map(path -> path.getFileName().toString())
//                    .filter(name -> name.matches("\\d{4}_\\d{2}_\\d{2}_\\d{2}_\\d{2}_\\d{2}_\\d{5}\\w{3}.txt"))
//                    .collect(Collectors.toList());
//            model.addAttribute("logFiles", logFiles);
//        }
//        catch (IOException e)
//        {
//            e.printStackTrace();
//        }
//        return "index";
//    }