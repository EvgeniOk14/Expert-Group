package org.example;

import org.eclipse.jetty.websocket.client.WebSocketClient;

import java.net.URI;

public class EchoClient
{
    public static void main(String[] args)
    {

        String uri = "ws://localhost:8098/ws"; // Адрес сервера ParseServer

        WebSocketClient client = new WebSocketClient(); // класс из библиотеки Jetty WebSocket Client,
                                                        // класс позволяет устанавливать соединения
                                                        // WebSocket с серверами
        try
        {
            client.start(); // Запуск клиента

            MyWebSocketClientHandler socket = new MyWebSocketClientHandler(); // Создание обработчика событий клиента

            System.out.println("Новое соединение с адресом: " + uri); // вывод в консоль о соединение по адресу, на котором находится сервер

            client.connect(socket, new URI(uri)); // client.connect: этот метод инициирует
                                                  // подключения к серверу WebSocket
                                                  // и назначает socket как обработчик событий.
                                                  // URI — это класс из пакета java.net,
                                                  // универсальный идентификатор ресурса
            socket.awaitClose(); // ожидания завершения соединения в главном потоке
        }
        catch (Throwable t)
        {
            t.printStackTrace();
        }
        /**
         * Гарантирует выполнение кода, который останавливает WebSocket-клиент и освобождает ресурсы,
         * независимо от того, было ли выброшено исключение или нет
         * **/
        finally // гарантирует остановку WebSocket-клиент и освобождает ресурсы,
                // независимо от того, было ли выброшено исключение или нет
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







