package org.example;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import java.util.concurrent.CountDownLatch;

@WebSocket
public class MyWebSocketClientHandler
{
    // region Fields
    private final CountDownLatch closeLatch = new CountDownLatch(1); // синхронизатор из пакета java.util.concurrent,
                                                                     // который используется для блокировки одной или нескольких
                                                                     // потоков до тех пор,
                                                                     // пока не будут выполнены определенные условия
                                                                     // final указывает на неизменность ссылки на объект,
                                                                     // после инициализации ссылка на объект не изменится
                                                                     // гарантируя, что closeLatch всегда будет указывать на
                                                                     // один и тот же объект.
                                                                     // аргумент равный 1 означает, что метод await будет
                                                                     // заблокирован до тех пор,
                                                                     // пока метод countDown не будет вызван один раз.
    //private Session session;
    // endregion

    /** метод вызывается при установлении соединения. Он отправляет начальное сообщение серверу **/
    @OnWebSocketConnect
    public void onConnect(Session session)
    {
        System.out.println("Connected to server: " + session.getRemoteAddress().getAddress());
        //this.session = session;
        try
        {
            session.getRemote().sendString("Hello Server!"); // Отправка сообщения серверу
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /** метод вызывается при получении сообщения от сервера. В нем происходит логика обработки полученных сообщений и отправка ответов **/
    @OnWebSocketMessage
    public void onMessage(Session session, String message)
    {
        System.out.println("Received message from server: " + message);

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
        else if (message != null && message.equals("Echo: Hello Client!")) // if("Echo: Hello Client!".equals(message))
        {
        try
            {
                session.close(); // Закрытие соединения после получения ответа
            }
        catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    /** метод вызывается при закрытии соединения. Он сигнализирует об окончании соединения с помощью closeLatch.countDown(). **/
    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason)
    {
        System.out.println("Connection closed: " + statusCode + ", " + reason); // выввод в консоль параметров закрытия соединения, для наглядности
        closeLatch.countDown(); // Сигнал завершения соединения
    }

    /** метод вызывается при возникновении ошибки. Он также сигнализирует об окончании соединения с помощью closeLatch.countDown(). **/
    @OnWebSocketError
    public void onError(Session session, Throwable throwable)
    {
        System.err.println("Error: " + throwable.getMessage()); // выводит в консоль ошибку
        throwable.printStackTrace();
        closeLatch.countDown(); // Сигнал ошибки
    }

    /** Метод awaitClose блокирует основной поток до тех пор, пока не произойдет закрытие соединения или ошибка **/
    public void awaitClose() throws InterruptedException
    {
        closeLatch.await(); // Ожидание завершения соединения,
                            // будет ожидать одного вызова countDown.
                            // В данном контексте это означает, что поток будет ждать до тех пор,
                            // пока соединение WebSocket не будет закрыто (или произойдет ошибка),
                            // и тогда вызов countDown разблокирует ожидание.
    }
}

