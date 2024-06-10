package org.example.getlogfiles.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer
{

    private final LogFileWebSocketHandler logFileWebSocketHandler;

    public WebSocketConfig(LogFileWebSocketHandler logFileWebSocketHandler)
    {
        this.logFileWebSocketHandler = logFileWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry)
    {
        registry.addHandler(logFileWebSocketHandler, "/ws").setAllowedOrigins("*");
    }
}
