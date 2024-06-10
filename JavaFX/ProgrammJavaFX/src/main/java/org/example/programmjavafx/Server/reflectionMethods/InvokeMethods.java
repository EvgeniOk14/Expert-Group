package org.example.programmjavafx.Server.reflectionMethods;

import com.google.gson.JsonObject;
import org.example.programmjavafx.Server.interfaces.InterfaceMethods;
import java.lang.reflect.Method;

public class InvokeMethods
{
    /** Метод для вызова метода с использованием рефлексии **/
    public void invokeMethod(InterfaceMethods handler, String methodName, Object args,
                             JsonObject response) throws Exception
    {
        try
        {
            Method[] methods = handler.getClass().getMethods(); // Получаем все методы класса

            boolean methodFound = false; // устанавливаем флаг false, на нахождение нужного метода

            for (Method method : methods) // итерируемся по массиву методов
            {
                if (method.getName().equalsIgnoreCase(methodName)) // Ищем метод, который соответствует имени
                                                                   // если имя метода совпадает с methodName
                {
                    Class<?>[] parameterTypes = method.getParameterTypes();
                                   // получаем массив объектов представляющих типы параметров
                                  //  на которые указывает объект method
                                 //   если у метода method есть параметры,
                                //    этот массив будет содержать типы этих параметров в порядке,
                               //     в котором они объявлены в сигнатуре метода
                              // т.е. массив будет содержать один элемент:  Class<InterfaceMethods.Args>


                    if (parameterTypes.length == 1 && parameterTypes[0] == args.getClass())
                                         // проверяем, что длина массива параметров равна 1
                                        // (то есть метод принимает только один параметр)
                                       // и что тип этого параметра соответствует типу объекта args

                    {
                        if (args instanceof InterfaceMethods.Args)
                        {
                            method.invoke(handler, args); // вызов метода handler с аргументом args
                            methodFound = true; // файл найден, меняем флаг на true
                            break; // останавливаемся и выходим из метода
                        }
                        else
                        {
                            throw new IllegalArgumentException("Invalid argument type");
                        }
                    }
                }
            }
            if (!methodFound) // Если метод не найден, отправляем сообщение об ошибке
            {
                response.addProperty("статус", "ошибка");
                response.addProperty("сообщение", "неподдерживаемый метод");
                if (args instanceof InterfaceMethods.Args && ((InterfaceMethods.Args) args).session != null)
                {
                    ((InterfaceMethods.Args) args).session.getRemote().sendString(response.toString());
                }
            }
        }
        catch (Exception e)
        {
            response.addProperty("статус", "ошибка");
            response.addProperty("сообщение", "ошибка вызова метода");
            if (args instanceof InterfaceMethods.Args && ((InterfaceMethods.Args) args).session != null)
            {
                ((InterfaceMethods.Args) args).session.getRemote().sendString(response.toString());
            }
            else
            {
                throw new IllegalArgumentException("Invalid argument type");
            }
        }
    }
}
