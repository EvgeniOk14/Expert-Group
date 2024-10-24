package org.example.programmjavafx.Server.reflectionMethods;

import com.google.gson.JsonObject;
import org.example.programmjavafx.Server.interfaces.InterfaceMethods;
import java.lang.reflect.Method;

/**
 * класс InvokeMethods
 * содержит в себе метод invokeMethod
 * который использует рефлексию для обработки сообщения клиенту
 * в зависимости от входных параметров:
 *      * @param handler - объект класса, реализующего интерфейс InterfaceMethods
 *      * @param methodName - имя метода, который нужно вызвать
 *      * @param args - аргументы, которые передаются в метод
 *      * @param response - объект для формирования ответа клиенту
 *      * @throws Exception - в случае ошибки вызова метода
 * **/
public class InvokeMethods
{
    /** Метод для вызова метода с использованием рефлексии **/
    public void invokeMethod(InterfaceMethods foundEntity, String methodName, Object args,
                             JsonObject response) throws Exception
    {
        try
        {
            Method[] methods = foundEntity.getClass().getMethods(); // извлекаем методы класса MethodRegistry из переменной handler в массив строк methods

            boolean methodFound = false; // устанавливаем флаг false, на нахождение нужного метода

            for (Method method : methods) // итерируемся по массиву методов объекта handler
            {
                // ищем метод, у которого имя совпадает с именем переданного параметра methodName
                if (method.getName().equalsIgnoreCase(methodName))

                {
                    // получаем массив объектов представляющих типы параметров на которые указывает объект method
                    Class<?>[] parameterTypes = method.getParameterTypes();
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
                            // Метод invoke является частью Java Reflection API
                            // и предназначен для вызова метода объекта во время выполнения (runtime).
                            // позволяет вызывать методы динамически, обеспечивая доступ к методам,
                            // которые могут быть неизвестны на этапе компиляции,
                            // но известны во время выполнения программы.
                            // вызов метода с аргументами: handler и args
                            method.invoke(foundEntity, args); // запускает найденный метод

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
