package vstr.configurations;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Configuración del broker de mensajes
        config.enableSimpleBroker("/topic"); // Broker en memoria para mensajería simple
        config.setApplicationDestinationPrefixes("/app"); // Prefijo para mensajes enviados desde el cliente
        // No es necesario usar Relay Host aquí para un broker en memoria
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Registra el endpoint de websocket que el cliente usará para conectarse
        registry.addEndpoint("/ws")
                .setAllowedOrigins("localhost:8080")// Especifica el origen permitido "tu dominio"
                .withSockJS() // Soporte para navegadores que no soportan WebSocket
                .setSessionCookieNeeded(false);
    }
}
