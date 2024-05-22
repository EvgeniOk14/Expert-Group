package org.example.models;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/** Класс управление конфигурацией **/
public class ConfigManager
{
    //region Fields

    // Определение константы, которая содержит имя конфигурационного файла
    private static final String CONFIG_FILE = "D:/Expert Group/LoggerLathe/src/main/java/org/example/config/config.properties";

    // Properties в Java — это класс, который является частью стандартной библиотеки Java (java.util.Properties)
    private Properties properties; // Он представляет собой подкласс Hashtable и используется для сохранения пар ключ-значение
                                   // где и ключи, и значения являются строками.
                                   // Этот класс широко используется для хранения конфигурационных параметров
                                   // и для загрузки этих параметров из файлов свойств (properties files)
    //endregion

    //region Constructor

    public ConfigManager() //  Конструктор класса
    {
        properties = new Properties(); // Инициализация объекта Properties.
        loadProperties(); //  Вызов метода loadProperties, который загружает конфигурационные параметры из файла.
    }

    //endregion

    /** метод загружает свойства из файла **/
    private void loadProperties()
    {
        try (FileInputStream input = new FileInputStream(CONFIG_FILE)) // Открытие файла свойств для чтения
        {
            properties.load(input); // Загрузка свойств из входного потока.
        }
        catch (IOException e) // Обработка исключений, если файл не найден или произошла ошибка ввода-вывода.
        {
            System.err.println("Не удалось загрузить конфигурационный файл: " + e.getMessage()); // Вывод сообщения об ошибке в поток ошибок.
        }
    }

    /** метод возвращает значение свойства default.path **/
    public String getDefaultPath()
    {
        return properties.getProperty("default.path", "./"); // Возвращение значения свойства default.path
                                                                           // Если свойство не найдено,
                                                                           // возвращается значение по умолчанию ("./").
    }

    /** метод  задает значение свойства default.path  **/
    public void setDefaultPath(String path)
    {
        properties.setProperty("default.path", path); // Установка нового значения для свойства default.path.
        saveProperties();         // Вызов метода saveProperties, который сохраняет изменения в файл свойств.
    }

    /** метод сохраняет свойства в файл **/
    private void saveProperties()
    {
        try (FileOutputStream output = new FileOutputStream(CONFIG_FILE)) // Открытие файла свойств для записи в блоке try-with-resources
        {
            properties.store(output, null); // Сохранение свойств в выходной поток.
                                                    // Второй параметр может содержать комментарий,
                                                   // который будет добавлен в начале файла, но здесь используется null.
        }
        catch (IOException e) // Обработка исключений, если произошла ошибка ввода-вывода
        {
            System.err.println("Не удалось сохранить конфигурационный файл: " + e.getMessage());
        }
    }
}

