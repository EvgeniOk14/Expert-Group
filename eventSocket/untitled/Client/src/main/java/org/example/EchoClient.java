package org.example;

import org.eclipse.jetty.websocket.client.WebSocketClient;
import java.net.URI;

public class EchoClient
{
    public static void main(String[] args)
    {
        String uri = "ws://localhost:8088/"; // Адрес сервера

        WebSocketClient client = new WebSocketClient(); // класс из библиотеки Jetty WebSocket Client,
                                                        // класс позволяет устанавливать соединения WebSocket с серверами

        try
        {
            client.start(); // Запуск клиента

            MyWebSocketClientHandler socket = new MyWebSocketClientHandler(); // Создание обработчика событий клиента

            System.out.println("Connecting to: " + uri); // вывод в консоль о соединение по адресу, на котором находится сервер

            client.connect(socket, new URI(uri)); // вызов инициирует установку соединения WebSocket между клиентом и сервером
                                                  // по указанному URI.
                                                  // client.connect: этот метод инициирует процесс подключения к серверу WebSocket
                                                  // инициирует асинхронное подключение к серверу WebSocket
                                                  // и назначает socket как обработчик событий.
                                                  // URI — это класс из пакета java.net,
                                                  // который представляет универсальный идентификатор ресурса (Uniform Resource Identifier).
                                                  // Он предоставляет конструкторы и методы для создания и управления URI.
                                                  // new URI(uri): Этот вызов конструктора создает новый экземпляр URI,
                                                  // используя строку uri, которая должна соответствовать формату URI.
                                                  // Это позволяет создать объект URI из строки,
                                                  // обеспечивая проверку корректности URI во время выполнения.
            socket.awaitClose(); // ожидания завершения соединения в главном потоке

        }
        /**
         * Этот блок перехватывает все исключения, которые могут быть выброшены в блоке try,
         *  включая Exception и Error (которые являются подтипами Throwable). t.printStackTrace();
         *  выводит стек трассировки исключения, что помогает в отладке и понимании того, что пошло не так
         *  **/
        catch (Throwable t)
        {
            t.printStackTrace();
        }
        /**
         * Гарантирует выполнение кода, который останавливает WebSocket-клиент и освобождает ресурсы,
         * независимо от того, было ли выброшено исключение или нет
         * **/
        finally
        {
            try
            {
                client.stop(); // останавливает WebSocket-клиент, освобождая все занятые ресурсы
                               // и закрывая все открытые соединения
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}






