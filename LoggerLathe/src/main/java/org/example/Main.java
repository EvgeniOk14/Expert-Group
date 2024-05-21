package org.example;

import org.example.models.ConfigManager;
import org.example.service.FileCreator;

public class Main {
    public static void main(String[] args) {

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