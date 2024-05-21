package org.example;

import org.example.models.ConfigManager;
import org.example.service.FileCreator;
import org.example.service.LoggerService;

public class Main {
    public static void main(String[] args) {
//          String receivedString = "exampleString";
//          LoggerService loggerService = new LoggerService();
//          loggerService.createFile(receivedString);


        // использования метода c заданием пути сохранинея файла

//        String directory = "C:\\example\\directory"; // Укажите путь к директории
//        LoggerService.createFileWithGivenDirectory(receivedString, directory);


        // Создание объекта ConfigManager
        ConfigManager configManager = new ConfigManager();

        // Создание объекта FileCreator
        FileCreator fileCreator = new FileCreator(configManager);


        // Создание первого файла с использованием дефолтного пути
        fileCreator.createFile("Пример содержимого файла 1");


        // Установка нового пути для сохранения файлов
        configManager.setDefaultPath("new/path/to/save/files");

        // Создание второго файла с использованием нового пути
        fileCreator.createFile("Пример содержимого файла 2");

    }
}