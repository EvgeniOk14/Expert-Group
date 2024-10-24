package org.example.programmjavafx.Server.maps;

import org.example.programmjavafx.Server.entities.FeederEntity;
import org.example.programmjavafx.Server.entities.FileEntity;
import org.example.programmjavafx.Server.entities.LogEntity;
import org.example.programmjavafx.Server.interfaces.InterfaceMethods;
import java.util.HashMap;
import java.util.Map;

/**
 * класс MethodRegistry
 * содержит названия методов в виде ключей
 * и сущности в виде значений
 * **/
public class MethodRegistry
{
    private static final Map<String, InterfaceMethods> methodMap = new HashMap<>();

    static // конструкция для автоматического заполнения Map ключами и значениями при инициализации класса
    {
        methodMap.put("log",  new LogEntity());
        methodMap.put("file", new FileEntity());
        methodMap.put("feeder", new FeederEntity());
        // а так же можно добавить другие методы и сущности, если это необходимо
    }

    /** метод получение значения из Map по ключу **/
    public static InterfaceMethods getMap(String key)
    {
        return methodMap.get(key); // получение значения по ключу
    }
}
