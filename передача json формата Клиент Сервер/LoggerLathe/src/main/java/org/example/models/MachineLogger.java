package org.example.models;

import org.example.service.FileCreator; // импорт класса FileCreator
import java.time.LocalDateTime; // библиотека представляет дату и время без часового пояса
import java.time.format.DateTimeFormatter; // библиотека для форматирования и парсинга объектов даты и времени,
                                          // позволяет преобразовывать объекты даты и времени в строковые представления
                                         // и обратно.

/** Класс для логирования работы станка **/
public class MachineLogger
{
    //region Fields
    private FileCreator fileCreator; // отвечает за создание и запись файлов.
    private String currentFileName; // Строка, которая хранит имя текущего файла для логирования.
                                   // Имя файла включает временную метку и серийный номер платы.
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
        currentFileName = "psblog" + "_" + timeFormatterForNameOfFile() + "_" + plateSerialNumber + ".txt"; // Создает имя файла, включающее временную метку и серийный номер платы
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
//        LocalDateTime now = LocalDateTime.now(); // получаем текущую дату и время
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"); // создаём объект LocalDateTime с конкретной датой и временем
//        String timestamp = now.format(formatter); // форматируем эти данные в строку
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
        System.out.println(formatter.format(LocalDateTime.now()));
        return formatter.format(LocalDateTime.now());
    }
    public String timeFormatterForNameOfFile()
    {
//        LocalDateTime now = LocalDateTime.now(); // получаем текущую дату и время
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"); // создаём объект LocalDateTime с конкретной датой и временем
//        String timestamp = now.format(formatter); // форматируем эти данные в строку
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH;mm;ss.SSS");
        System.out.println(formatter.format(LocalDateTime.now()));
        return formatter.format(LocalDateTime.now());
    }
}
