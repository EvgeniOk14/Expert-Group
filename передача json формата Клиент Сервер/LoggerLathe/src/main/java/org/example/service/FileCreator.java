package org.example.service;

import org.example.models.ConfigManager;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/** Класс создание файла **/
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
        public void createFile(String fileName, String content)
        {
            String defaultPath = configManager.getDefaultPath();
            File directory = new File(defaultPath);

            if (!directory.exists())
            {
                directory.mkdirs();
            }

            String filePath = defaultPath + File.separator + fileName;

            // Открытие файла в режиме добавления (append)
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true)))
            {
                writer.write(content); // запись содержимого
                writer.newLine(); // переход на новую строку
                System.out.println("Файл успешно обновлен: " + filePath); // вывод в терминал об успешной работе
            }
            catch (IOException e)
            {
                System.err.println("Произошла ошибка при обновлении файла: " + e.getMessage()); // вывод в терминал об ошибке
            }
        }

    }



