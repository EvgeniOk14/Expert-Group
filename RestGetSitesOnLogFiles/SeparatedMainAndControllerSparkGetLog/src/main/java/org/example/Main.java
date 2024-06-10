package org.example;

import org.example.controllers.CRUDController;
import static spark.Spark.staticFiles;

public class Main
{
    private static final String LOG_DIRECTORY = "C:\\Expert Group\\LoggerLathe\\logfiles";

    public static void main(String[] args)
    {
        staticFiles.location("/public");      // Путь к статическим файлам (если есть)
        CRUDController.setupRoutes(LOG_DIRECTORY);  // Инициализация контроллеров
    }
}
