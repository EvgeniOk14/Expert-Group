package org.example.programmjavafx.LoggerLathe.models;

import org.example.programmjavafx.LatheController;
import org.example.programmjavafx.LoggerLathe.service.FileCreator;
import org.example.programmjavafx.Server.DataBase.dbService.ServiceDB;
import org.example.programmjavafx.Server.entityDB.Plate;
import java.io.*;
import java.sql.SQLException;
import java.time.LocalDateTime; // библиотека представляет дату и время без часового пояса
import java.time.format.DateTimeFormatter; // библиотека для форматирования объектов даты и времени в строку

/** Класс для логирования работы станка **/
public class MachineLogger
{
    //region Fields
    private final FileCreator fileCreator; // класс создаёт и записывает файлы
    private String currentFileName; //  имя текущего файла
    //endregion

    //region Constructor
    public MachineLogger(FileCreator fileCreator)
    {
        this.fileCreator = fileCreator;
    }
    //endregion

    ServiceDB serviceDB = new ServiceDB(); // инициализируем класс ServiceDB (методы работы с базой данных)

    /**
     * метод начала работы с платой
     * принимает на вход строку: номер платы,
     * с которой работает станок в данный момент
     **/
    public void startNewPlate(String plateNumber) //  передаём параметр - серийный номер платы
    {
        String logMessage = "Установлена плата серийный номер №: " + plateNumber + " Время установки: " + timeFormatter(); // создаём сообщение для данного события
        currentFileName = "pcblog" + "_" + timeFormatterForNameOfFile() + "_" + plateNumber + ".txt"; // формируем имя файла
        File foundFile =  LatheController.getFileByPlateNumberAndWriteNewInformation(plateNumber); // ищем файл с таким номером платы

        if (foundFile != null) // если найденный файл не равен нулю
        {
            //currentFileName = foundFile.getAbsolutePath(); // обновляем currentFileName с найденным файлом
            fileCreator.writeNewRecordIntoFoundFile(foundFile, logMessage); // производим дозапись сообщения в существующий файл
        }
        else // если файла нет, то создаём новый ло-файл
        {
            // добавляем новую запись о начале работы станка с платой и создам новый лог-файл
            log(logMessage); // логируем сообщение о том, что плата установлена, вызывая метод log
        }

        // производим запись в базу данных
        Plate plate = new Plate(plateNumber); // инициализируем сущность Plate, с текущим номером платы
        serviceDB.insertStartWork(plate, logMessage); // вставляем в базу данных новую запись о старте работы с новой платой
    }

    /**
     * метод логирует сообщение о штатной работе
     **/
    public void logNormalOperation(String plateNumber, String message) throws SQLException
    {
        String logMessage = "INFO: " + message + " Время: " + timeFormatter(); // создаём сообщение для данного события
        File foundFile =  LatheController.getFileByPlateNumberAndWriteNewInformation(plateNumber);

        if (foundFile != null)
        {
            currentFileName = foundFile.getAbsolutePath(); // обновляем currentFileName с найденным файлом
            fileCreator.writeNewRecordIntoFoundFile(foundFile, logMessage); // производит дозапись новых данных в существующий файл
        }
        else
        {
            log(logMessage); // добавляем новую запись о нормальной работе станка в у же созданный лог
        }
        //serviceDB.updateNormalWork(plateNumber, logMessage);  // производим запись в базУ данных
        serviceDB.updateWorkEvent(plateNumber, logMessage, "Нормальная работа станка.");
    }


    /**
     * метод логирует предупреждающие сообщения
     **/
    public void logWarning(String plateNumber, String message)
    {
        String logMessage = "WARNING: " + message + " Время сообщения об предупреждении: " + timeFormatter(); // создаём сообщение для данного события
        File foundFile =  LatheController.getFileByPlateNumberAndWriteNewInformation(plateNumber);

        if (foundFile != null)
        {
            currentFileName = foundFile.getAbsolutePath(); // обновляем currentFileName с найденным файлом
            fileCreator.writeNewRecordIntoFoundFile(foundFile, logMessage); // производит дозапись новых данных в существующий файл
        }
        else
        {
            log(logMessage); // добавляем новую запись о предупреждение о возможной ошибке в работе станка в у же созданный лог
        }
        //serviceDB.updateWarningWork(plateNumber, logMessage); // производим запись в базу данных
        serviceDB.updateWorkEvent(plateNumber, logMessage, "Предупреждение о возможной проблеме!");
    }

    /**
     * метод логирует сообщения об ошибках
     **/
    public void logError(String plateNumber, String message)
    {
        String logMessage = "ERROR: " + message + " Время сообщения об предупреждении: " + timeFormatter();
        File foundFile =  LatheController.getFileByPlateNumberAndWriteNewInformation(plateNumber);

        if (foundFile != null)
        {
            currentFileName = foundFile.getAbsolutePath(); // обновляем currentFileName с найденным файлом
            fileCreator.writeNewRecordIntoFoundFile(foundFile, logMessage); // производим дозапись сообщения об ошибке в существующий файл
        }
        else
        {
            log(logMessage); // добавляем новую запись об ошибке, произошедшей при  работе станка в у же созданный лог
        }
        //serviceDB.updateErrorWork(plateNumber, logMessage); // производим запись в базу данных
        serviceDB.updateWorkEvent(plateNumber, logMessage, "Ошибка при работе станка!");
    }

    /**
     * метод логирует сообщение о завершении работы с платой
     **/
    public void endPlateOperation(String logMessage, String plateNumber)
    {
        File foundFile =  LatheController.getFileByPlateNumberAndWriteNewInformation(plateNumber);

        if (foundFile != null)
        {
            currentFileName = foundFile.getAbsolutePath(); // обновляем currentFileName с найденным файлом
            fileCreator.writeNewRecordIntoFoundFile(foundFile, logMessage); // производит дозапись новых данных в существующий файл
        }
        else
        {
            log(logMessage + timeFormatter()); // логируем сообщение об окончании работы с платой
            currentFileName = null; // сбрасываем значение currentFileName на null, чтобы указать, что работа с текущей платой завершена
        }
        //serviceDB.updateEndWork(plateNumber, logMessage); // производим запись в базу данных
        serviceDB.updateWorkEvent(plateNumber, logMessage, "Завершение работы с платой.");
    }

    /**
     * метод выполняет непосредственную запись логов в файл
     **/
    private void log(String message)
    {
        if (currentFileName != null) // если значение currentFileName установлено, то:
        {
            fileCreator.createFile(currentFileName, message); // вызывает метод createFile объекта fileCreator, передавая имя файла и сообщение для записи
        }
        else // если значение currentFileName не установлено, то:
        {
            System.err.println("Не задано имя файла для логирования! "); // выводим сообщение об ошибке в консоль
        }
    }


    /**
     * метод получения текущей даты и времени и перевод их в строку
     **/
    public String timeFormatter()
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"); // использует двуеточие для разделения времени
        return formatter.format(LocalDateTime.now());
    }

    /**
     * метод формирует часть ИМЕНИ ФАЙЛА,
     * которая соответствует текущей дате и времени
     * по заданному шаблону yyyy-MM-dd'T'HH-mm-ss.SSS
     * и переводит в строку
     **/
    public String timeFormatterForNameOfFile()
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH-mm-ss.SSS"); // использует дефис для разделения времени
        return formatter.format(LocalDateTime.now());
    }

    /**
     * метод для проверки состояния текущей платы
     **/
    public boolean isPlateInstalled()
    {
        return currentFileName != null;
    }
}

