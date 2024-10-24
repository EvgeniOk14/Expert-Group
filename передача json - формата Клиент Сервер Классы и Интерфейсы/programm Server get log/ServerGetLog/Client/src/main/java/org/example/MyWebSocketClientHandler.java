package org.example;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import java.util.concurrent.CountDownLatch;

@WebSocket
public class MyWebSocketClientHandler
{
    // region Fields
    // Создает объект CountDownLatch с начальным значением 1.
    // Это синхронизирующий вспомогательный объект,
    // который позволяет одному или нескольким потокам ждать,
    // пока не будет выполнено определенное количество операций.
    // В этом случае, он используется для блокировки основного потока
    // до тех пор, пока WebSocket-соединение не будет закрыто
    // или не произойдет ошибка.
    private final CountDownLatch closeLatch = new CountDownLatch(1);
    // Создаем экземпляр Gson, для преобразования объектов в JSON-формат и обратно
    private Gson gson = new Gson();
    //endregion

    /**
     * метод вызывается при установлении соединения. Он отправляет начальное сообщение серверу
     * **/
    @OnWebSocketConnect
    public void onConnect(Session session)
    {
        // печатаем в консоль адрес удаленного сервера, к которому подключился клиент
        System.out.println("Connected to server: " + session.getRemoteAddress().getAddress());

        //  Создает объект RequestMessage, который представляет собой сообщение, отправляемое серверу
        RequestMessage requestMessage = new RequestMessage("get", "log", "ABC001");
        // Преобразует объект RequestMessage в строку JSON с помощью Gson.
        String jsonMessage = gson.toJson(requestMessage);

//        RequestMessage requestMessage1 = new RequestMessage("get", "log", "NVV45FG");
//        String jsonMessage1 = gson.toJson(requestMessage1);
//
//        RequestMessage requestMessage2 = new RequestMessage("get", "log", "584FDG8");
//        String jsonMessage2 = gson.toJson(requestMessage2);
//
//        RequestMessage requestMessage3 = new RequestMessage("get", "feeder", "5");
//        String jsonMessage3 = gson.toJson(requestMessage3);

        //RequestMessage requestMessageTestGet = new RequestMessage("get", "file", "array.json");
        //String jsonMessageTestGet = gson.toJson(requestMessageTestGet);

        //RequestMessage requestMessageTestUpdate = new RequestMessage("update", "file", "array.txt");
        //RequestMessage requestMessageTestUpdate = new RequestMessage("update", "file", "array.json");
        //String jsonMessageTestUpdate = gson.toJson(requestMessageTestUpdate);
//
//        RequestMessage requestMessage4 = new RequestMessage("get");
//        String jsonMessage4 = gson.toJson(requestMessage4);
//
//        RequestMessage requestMessage5 = new RequestMessage();
//        String jsonMessage5 = gson.toJson(requestMessage5);
//
//        RequestMessage requestMessage6 = new RequestMessage("ghjdfh", "fkgj", "456grfd");
//        String jsonMessage6 = gson.toJson(requestMessage6);
//
//        RequestMessage requestMessage7 = new RequestMessage("get", "file", "67890DEF");
//        String jsonMessage7 = gson.toJson(requestMessage7);

        try
        {
            //session.getRemote().sendString(jsonMessageTestGet);
            //session.getRemote().sendString(jsonMessageTestUpdate);


            session.getRemote().sendString(jsonMessage); // Отправляет строку JSON на сервер
//            session.getRemote().sendString(jsonMessage1);
 //           session.getRemote().sendString(jsonMessage2);
          //  session.getRemote().sendString(jsonMessageTestUpdate);
//
//           session.getRemote().sendString(jsonMessage3);
//            session.getRemote().sendString(jsonMessage5);
//            session.getRemote().sendString(jsonMessage6);
//
//            session.getRemote().sendString(jsonMessage7);

        }
        // Ловим и печатаем исключения, если отправка сообщения не удалась
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * метод вызывается при получении сообщения от сервера.
     * В нем происходит логика обработки полученных сообщений и отправка ответов
     * **/
    @OnWebSocketMessage
    public void onMessage(Session session, String message)
    {
        // печатаем в консоль полученное сообщение от сервера
        System.out.println("Received message from server: " + message); // вывод сообщение от сервера в терминал

        if (message != null && message.equals("Echo: Hello Server!")) // ("Echo: Hello Server!".equals(message))
        {
            try
            {
                session.getRemote().sendString("Hello Client!"); // Отправка второго сообщения
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        else if (message != null && message.equals("Echo: Hello Client!"))
        {
            try
            {
                session.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * метод вызывается при закрытии соединения.
     * Он сигнализирует об окончании соединения с помощью closeLatch.countDown().
     * **/
    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason)
    {
        // Печатает в консоль код состояния и причину закрытия соединения.
        System.out.println("Connection closed: " + statusCode + ", " + reason); // выввод в консоль параметров закрытия соединения, для наглядности
        // уменьшаем счетчик CountDownLatch на 1, сигнализируя о том, что соединение закрыто
        closeLatch.countDown();
    }

    /**
     * метод вызывается при возникновении ошибки.
     * Он также сигнализирует об окончании соединения с помощью closeLatch.countDown().
     * **/
    @OnWebSocketError
    public void onError(Session session, Throwable throwable)
    {
        // печатаем сообщение об ошибке в стандартный поток ошибок
        System.err.println("Error: " + throwable.getMessage());
        // печатаем стек трейс ошибки
        throwable.printStackTrace();
        // уменьшаем счетчик CountDownLatch на 1, сигнализируя о том, что произошло исключение
        closeLatch.countDown();
    }


    /**
     *  Метод awaitClose блокирует основной поток до тех пор,
     *  пока не произойдет закрытие соединения или ошибка
     *  **/
    public void awaitClose() throws InterruptedException
    {
        // Блокирует текущий поток до тех пор,
        // пока счетчик CountDownLatch не достигнет нуля,
        // что происходит, когда соединение закрыто или произошла ошибка.
        closeLatch.await();
    }
}

