package org.example;

import org.example.models.ConfigManager;
import org.example.models.MachineLogger;
import org.example.service.FileCreator;

public class Main
{
    public static void main(String[] args)
    {
        ConfigManager configManager = new ConfigManager();
        FileCreator fileCreator = new FileCreator(configManager);
        MachineLogger logger = new MachineLogger(fileCreator);

        // Пример использования
        logger.startNewPlate("12345ABC");
        logger.logNormalOperation("Компонент установлен");
        logger.logWarning("Температура превышена");
        logger.logError("Ошибка в работе датчика");
        logger.endPlateOperation();

        // Изменение пути сохранения файлов
        //configManager.setDefaultPath("new/path/to/save/files");
        configManager.setDefaultPath("new/path/to/save/files");

        // Вставка новой платы
        logger.startNewPlate("67890DEF");
        logger.logNormalOperation("Компонент установлен");
        logger.logWarning("Низкое давление");
        logger.endPlateOperation();
    }
}