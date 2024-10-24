package org.example.programmjavafx.LoggerLathe.models;

/**
 *  вспомогательный настроечный Класс
 *  содержит переменную - дефолтный путь
 *  используется в классе FileCreator (класс создания и сохранения файла)
 *  **/
public class ConfigManager
{
    //region Fields
    private String defaultPath = "./"; // Значение по умолчанию для пути сохранения файлов
    //endregion

    //region Constructor
    public ConfigManager()
    {
        // defalt constructor
    }
    //endregion

    //region Getters && Setters
    /** метод возвращает значение свойства default.path **/
    public String getDefaultPath()
    {
        return defaultPath;
    }

    /** метод  задает значение свойства default.path  **/
    public void setDefaultPath(String path)
    {
        this.defaultPath = path;
    }
    //endregion
}












