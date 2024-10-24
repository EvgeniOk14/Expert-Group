package org.example;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import java.util.concurrent.CountDownLatch;

@WebSocket
public class MyWebSocketClientHandler
{

    // region Fields
    private final CountDownLatch closeLatch = new CountDownLatch(1);
    private Gson gson = new Gson();

    /**
     * метод вызывается при установлении соединения. Он отправляет начальное сообщение серверу
     * **/
    @OnWebSocketConnect
    public void onConnect(Session session)
    {
        System.out.println("Connected to server: " + session.getRemoteAddress().getAddress());

//        RequestMessage requestMessage = new RequestMessage("get", "log", "46SJ78GF");
//        String jsonMessage = gson.toJson(requestMessage);

//        RequestMessage requestMessage1 = new RequestMessage("get", "log", "NVV45FG");
//        String jsonMessage1 = gson.toJson(requestMessage1);
//
//
//        RequestMessage requestMessage2 = new RequestMessage("get", "log", "584FDG8");
//        String jsonMessage2 = gson.toJson(requestMessage2);
//
//
//
//        RequestMessage requestMessage3 = new RequestMessage("get", "log", "RTT464F");
//        String jsonMessage3 = gson.toJson(requestMessage3);
//
//
//        RequestMessage requestMessage4 = new RequestMessage("get");
//        String jsonMessage4 = gson.toJson(requestMessage4);
//
//
//        RequestMessage requestMessage5 = new RequestMessage();
//        String jsonMessage5 = gson.toJson(requestMessage5);
//
//
//        RequestMessage requestMessage6 = new RequestMessage("ghjdfh", "fkgj", "456grfd");
//        String jsonMessage6 = gson.toJson(requestMessage6);
//
        RequestMessage requestMessage7 = new RequestMessage("get", "file", "32590RTW");
        String jsonMessage7 = gson.toJson(requestMessage7);

        try
        {
           // session.getRemote().sendString(jsonMessage);
//            session.getRemote().sendString(jsonMessage1);
 //           session.getRemote().sendString(jsonMessage2);
//            session.getRemote().sendString(jsonMessage3);
//
//            session.getRemote().sendString(jsonMessage4);
//            session.getRemote().sendString(jsonMessage5);
//            session.getRemote().sendString(jsonMessage6);
//
            session.getRemote().sendString(jsonMessage7);

        }
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
        System.out.println("Connection closed: " + statusCode + ", " + reason); // выввод в консоль параметров закрытия соединения, для наглядности
        closeLatch.countDown();
    }

    /**
     * метод вызывается при возникновении ошибки.
     * Он также сигнализирует об окончании соединения с помощью closeLatch.countDown().
     * **/
    @OnWebSocketError
    public void onError(Session session, Throwable throwable)
    {
        System.err.println("Error: " + throwable.getMessage());
        throwable.printStackTrace();
        closeLatch.countDown();
    }


    /**
     *  Метод awaitClose блокирует основной поток до тех пор,
     *  пока не произойдет закрытие соединения или ошибка
     *  **/
    public void awaitClose() throws InterruptedException
    {
        closeLatch.await();
    }
}

