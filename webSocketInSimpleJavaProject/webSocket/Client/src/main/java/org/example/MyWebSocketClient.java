package org.example;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import java.net.URI;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class MyWebSocketClient
{
    public static void main(String[] args)
    {
            String uri = "ws://localhost:8086/"; // создаём переменную (адрес сервера)

            WebSocketClient client = new WebSocketClient(); // создаём новое соединение

            try
            {
                client.start(); // открываем соединение

                MyWebSocketClientHandler socket = new MyWebSocketClientHandler(); // создаём новый обработчик WebSocket

                Future<Session> futureSession = client.connect(socket, new URI(uri)); // подключаемся к серверу по URI и получаем Future для сессии


                System.out.println("Connecting to: " + uri);  // вывод в консоль

                Session session = futureSession.get(500, TimeUnit.MILLISECONDS);
                 //Session session = futureSession.get(1, TimeUnit.SECONDS);  // Ждем 5 секунд для подключения

                if (session != null && session.isOpen()) // проверяем, что сессия установлена и открыта
                {
                    session.getRemote().sendString("Hello Server!"); // отправляем сообщение серверу


                    TimeUnit.MILLISECONDS.sleep(500); // Ждем 5 секунд для получения ответа
                    session.getRemote().sendString("Hello Client!");
                    session.close(); // закрываем соединение
                }

                TimeUnit.SECONDS.sleep(1); // дополнительное время


            } catch (Throwable t)
            {
                t.printStackTrace();
            }
            finally
            {
                try
                {
                    client.stop(); // останавливаем WebSocket клиент
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }

}
