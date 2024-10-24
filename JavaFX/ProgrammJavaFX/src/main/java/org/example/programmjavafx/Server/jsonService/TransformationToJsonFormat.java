package org.example.programmjavafx.Server.jsonService;

import com.google.gson.Gson;
import org.example.programmjavafx.Server.RequestMessage;

public class TransformationToJsonFormat
{
    //region Fields
    private static Gson gson = new Gson(); //  экземпляр Gson для работы с JSON
    private static RequestMessage requestMessage; // экземпляр класса RequestMessage для хранения результатов преобразования
    //endregion

    // преобразование входящего сообщения строки json формата в Java объект, конкретно в класс RequestMessage:
    public static RequestMessage transformationToJsonFormat(String message)
    {
        // выполняет десериализацию JSON строки в объект класса RequestMessage
        requestMessage = gson.fromJson(message, RequestMessage.class);
        System.out.println(
                           "Преобразование входящего сообщения: " +
                           "method=" + requestMessage.getMethod() +
                         ", entity=" + requestMessage.getEntity() +
                         ", data=" + requestMessage.getData()
                          );
        return requestMessage;
    }
}
