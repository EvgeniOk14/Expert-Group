package org.example.service;

import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LoggerService
{
    /**  Метод создания файла
     * на вход поступает строка
     *  на выходе создаётся файл **/
    public void createFile(String content)
    {

        LocalDateTime now = LocalDateTime.now(); // <---  Получаем текущую дату и время

        // Форматируем дату и время в строку
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss");
        String timestamp = now.format(formatter); // создаёт строку в соответствии с шаблоном


        String fileName = timestamp + "_" + content + ".txt";  // Создаем имя файла


        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName)))   // Записываем строку в файл
        {
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


    public static void createFileWithGivenDirectory(String content, String directory)
    {

        LocalDateTime now = LocalDateTime.now(); // Получаем текущую дату и время

        // Форматируем дату и время в строку
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss");
        String timestamp = now.format(formatter);


        String fileName = timestamp + "_" + content + ".txt"; // Создаем имя файла


        Path filePath = Paths.get(directory, fileName); // // создает объект Path, представляющий полный путь к файлу

        // Проверяем, существует ли директория, и создаем ее при необходимости
        try {
            if (!Files.exists(filePath.getParent()))
            {
                Files.createDirectories(filePath.getParent()); // создает все отсутствующие директории, чтобы убедиться, что указанный путь существует.
            }

            // Записываем строку и текущую дату и время в файл
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath.toFile()))) {
                writer.write("Дата и время создания файла: " + timestamp);
                writer.newLine(); // Переход на новую строку
                writer.write("Содержимое: " + content);
                System.out.println("Файл успешно создан: " + filePath.toString());
            }
        } catch (IOException e) {
            System.err.println("Произошла ошибка при создании файла: " + e.getMessage());
        }
    }


}









//public class SaveTextFormat<String>
//{
//
//    public void write() throws IOException, ClassNotFoundException
//    {
//        String pathProject = System.getProperty("user.dir");
//        String pathFile = pathProject.concat("/file.txt");
//        File file = new File(pathFile);
//        FileWriter writer = new FileWriter(file.getAbsolutePath(), true);
//        writer.write("\nTxt Format\n");
//        writer.write();
//        writer.flush();
//    }
//
//}