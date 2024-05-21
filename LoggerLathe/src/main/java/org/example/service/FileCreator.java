package org.example.service;

import org.example.models.ConfigManager;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FileCreator
{
    //region Fields
    private ConfigManager configManager;
    //endregion

    //region Constructor
    public FileCreator(ConfigManager configManager)
    {
        this.configManager = configManager;
    }
    //endregion

    /** метод создания файла с заданным содержимым **/
    public void createFile(String content)
    {

        LocalDateTime now = LocalDateTime.now(); // Получаем текущую дату и время

        // Форматируем дату и время в строку
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss"); // Создает объект DateTimeFormatter
                                                                                         // для форматирования даты и времени
                                                                                        // в строку определенного формата
                                                                                       // (yyyy_MM_dd_HH_mm_ss).
        String timestamp = now.format(formatter); // Форматирует текущую дату и время в строку timestamp.

        String defaultPath = configManager.getDefaultPath();  // получаем дефолтный путь из конфигурации

        File directory = new File(defaultPath); // Создает объект File, представляющий директорию по этому пути

        if (!directory.exists()) // Проверяет, существует ли директория
        {
            directory.mkdirs();// если нет, создает все необходимые поддиректории
        }

        String fileName = defaultPath + File.separator + timestamp + "_" + content + ".txt";  // Создаем имя файла

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) { // Записываем строку в файл
            writer.write("Дата и время создания файла: " + timestamp);
            writer.newLine(); // Переход на новую строку
            writer.write(content); // запись содержимого в файл
            System.out.println("Файл успешно создан: " + fileName); // вывод в терминал, для проверки
        }
        catch (IOException e)
        {
            System.err.println("Произошла ошибка при создании файла: " + e.getMessage());
        }
    }
}
