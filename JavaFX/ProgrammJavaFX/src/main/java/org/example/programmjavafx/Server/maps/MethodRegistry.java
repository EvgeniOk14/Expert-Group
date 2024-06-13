package org.example.programmjavafx.Server.maps;

import org.example.programmjavafx.Server.entities.FileEntity;
import org.example.programmjavafx.Server.entities.LogEntity;
import org.example.programmjavafx.Server.interfaces.InterfaceMethods;
import java.util.HashMap;
import java.util.Map;

public class MethodRegistry
{
    private static final Map<String, InterfaceMethods> methodMap = new HashMap<>();
    static // автоматическое заполнение Map ключами и значениями при инициализации класса
    {
        methodMap.put("log",  new LogEntity());
        methodMap.put("file", new FileEntity());
        // а так же можно добавить другие методы и сущности, если это необходимо
    }

    /** метод получение значения из Map по ключу **/
    public static InterfaceMethods getMap(String key)
    {
        return methodMap.get(key); // получение значения по ключу
    }
}
