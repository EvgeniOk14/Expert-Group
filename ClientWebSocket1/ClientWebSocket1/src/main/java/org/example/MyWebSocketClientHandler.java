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

//        RequestMessage requestMessage = new RequestMessage("ClientWebsocket1 Connected");
//        String jsonMessage = gson.toJson(requestMessage);

        RequestMessage requestMessage1 = new RequestMessage("get", "log", "67890DEF");
        String jsonMessage1 = gson.toJson(requestMessage1);

        try
        {
            //session.getRemote().sendString(jsonMessage);

            session.getRemote().sendString(jsonMessage1);

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
        System.out.println("Получено сообщение от сервера: " + message); // вывод сообщение от сервера в терминал

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
        System.out.println("Соединение закрыто: " + statusCode + ", " + reason); // выввод в консоль параметров закрытия соединения, для наглядности
        closeLatch.countDown();
    }

    /**
     * метод вызывается при возникновении ошибки.
     * Он также сигнализирует об окончании соединения с помощью closeLatch.countDown().
     * **/
    @OnWebSocketError
    public void onError(Session session, Throwable throwable)
    {
        System.err.println("Ошибка: " + throwable.getMessage());
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

