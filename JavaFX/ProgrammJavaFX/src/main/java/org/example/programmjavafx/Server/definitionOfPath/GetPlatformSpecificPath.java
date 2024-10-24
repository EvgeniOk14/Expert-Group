package org.example.programmjavafx.Server.definitionOfPath;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * класс GetPlatformSpecificPath
 * для обработки путей,
 * которые зависят от платформы (операционной системы)
 * **/
public class GetPlatformSpecificPath
{
    /**
     * Метод для получения пути в зависимости от операционной системы
     * передаваемый в метод параметр позволяет передать любое количество строк(элементов пути),
     * представляющих элементы пути.
     * **/
    public static Path getPlatformSpecificPath(String... pathElements)
    {
        // Параметр "" — это корневой путь или текущая директория, от которой начинается создание пути.
        // pathElements — массив строк, представляющих элементы пути (например, директории и файлы). Эти элементы будут объединены для формирования полного пути.
        // Возвращает объект Path, который представляет путь в файловой системе.
        return Paths.get(FileSystems.getDefault().getPath("", pathElements).toString());
    }
}
