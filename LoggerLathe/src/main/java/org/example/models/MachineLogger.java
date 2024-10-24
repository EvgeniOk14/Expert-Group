package org.example.models;

import org.example.service.FileCreator; // импорт класса FileCreator
import java.time.LocalDateTime; // библиотека представляет дату и время без часового пояса
import java.time.format.DateTimeFormatter; // библиотека для форматирования объектов даты и времени в строку


/** Класс для логирования работы станка **/
public class MachineLogger
{
    //region Fields
    private FileCreator fileCreator; // класс создаёт и записывает файлы
    private String currentFileName; //  имя текущего файла
    //endregion

    //region Constructor
    public MachineLogger(FileCreator fileCreator)
    {
        this.fileCreator = fileCreator;
    }
    //endregion

    /** метод начала работы с платой **/
    public void startNewPlate(String plateSerialNumber) //  передаём параметр - серийный номер платы
    {
        currentFileName = "pcblog" + "_" + timeFormatterForNameOfFile() + "_" + plateSerialNumber + ".txt"; // Создает имя файла, включающее временную метку и серийный номер платы
        log("Установлена плата серийный номер №: " + plateSerialNumber + " Время установки: " + timeFormatter()); // Логирует сообщение о том, что плата установлена, вызывая метод log
    }

    /** метод логирует сообщение о штатной работе **/
    public void logNormalOperation(String message)
    {
        log("INFO: " + message + " Время: " +  timeFormatter());
    }

    /** метод логирует предупреждающие сообщения **/
    public void logWarning(String message)
    {
        log("WARNING: " + message + " Время сообщения об предупреждении: " + timeFormatter());
    }

    /** метод логирует сообщения об ошибках **/
    public void logError(String message)
    {
        log("ERROR: " + message + " Время сообщения об предупреждении: " + timeFormatter());
    }

    /** метод логирует сообщение о завершении работы с платой **/
    public void endPlateOperation()
    {
        log("Операция по работе с платой завершена!" + " Время завершения операции с платой: " + timeFormatter());
        currentFileName = null; // сбрасываем значение currentFileName на null, чтобы указать, что работа с текущей платой завершена
    }

    /** метод выполняет непосредственную запись логов в файл **/
    private void log(String message)
    {
        if (currentFileName != null) // если значение currentFileName установлено, то:
        {
            fileCreator.createFile(currentFileName, message); // вызывает метод createFile объекта fileCreator, передавая имя файла и сообщение для записи
        }
        else // если значение currentFileName не установлено, то:
        {
            System.err.println("Не установлена текущая плата для логирования"); // выводит сообщение об ошибке в консоль
        }
    }

    /** метод получения текущей даты и времени и перевод их в строку **/
    public String timeFormatter()
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
        //System.out.println(formatter.format(LocalDateTime.now()));
        return formatter.format(LocalDateTime.now());
    }
    /**  метод формирует имя файла из даты и времени по заданному шаблону в троку **/
    public String timeFormatterForNameOfFile()
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH;mm;ss.SSS");
        //System.out.println(formatter.format(LocalDateTime.now()));
        return formatter.format(LocalDateTime.now());
    }
}
