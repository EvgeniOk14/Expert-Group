package org.example.programmjavafx.LoggerLathe.service;

import org.example.programmjavafx.LoggerLathe.models.ConfigManager;
import java.io.*;

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
            String defaultPath = configManager.getDefaultPath(); // получение пути по умолчанию из класса ConfigManager
            File directory = new File(defaultPath); // создаём новый объект File, представляющий директорию (путь)

            if (!directory.exists()) // проверяет, существует ли директория или файл по пути defaultPath
            {
                directory.mkdirs(); // создаёт все необходимые директории по этому пути
            }

            String filePath = defaultPath + File.separator + fileName; // создаём путь к файлу

            // Открытие файла в режиме добавления (append)
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) // Записывает данные в файл, true - дозапись в конец файла
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

    /** метод делает дозапись в найденный файл **/
    public void writeNewRecordIntoFoundFile(File file, String logMessage)
    {
        try (FileWriter fw = new FileWriter(file, true); // открываем файл в режиме append (добавление)
             BufferedWriter bw = new BufferedWriter(fw); // создает буфер, который собирает данные, пока они не будут записаны в файл за один раз. Это снижает количество операций записи на диск, улучшая производительность
             PrintWriter out = new PrintWriter(bw)) // обеспечивает буферизацию и текстовое форматирование
        {
            out.println(logMessage); // метод записывает строку logMessage в выходной поток, за которым следует символ новой строки
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}



