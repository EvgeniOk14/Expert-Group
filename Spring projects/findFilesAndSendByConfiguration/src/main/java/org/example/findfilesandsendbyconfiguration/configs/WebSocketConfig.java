package org.example.findfilesandsendbyconfiguration.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer
{
    /**
     * метод настраивает обработчики WebSocket.
     * Он вызывается Spring для регистрации обработчиков WebSocket при инициализации приложения.
     * **/
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) // WebSocketHandlerRegistry управляет регистрацией обработчиков WebSocket.
    {
        // метод добавляет новый обработчик WebSocket для указанного URL: (/ws).
        // В данном случае, обработчиком является LogFileWebSocketHandler.
        registry.addHandler(new LogFileWebSocketHandler(), "/ws").setAllowedOrigins("*");
        // .setAllowedOrigins("*"): Устанавливает разрешённые источники (origins) для соединений.
        // Здесь * означает, что разрешены соединения от любых источников.
    }
}
