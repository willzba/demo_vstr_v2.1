package vstr.controllers.transmisionControllers;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import vstr.model.ChatMessage;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
public class ChatController {

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/chat")
    public ChatMessage sendMessage(ChatMessage message, Principal principal) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setFrom(principal.getName()); // Asume que el nombre de usuario es único
        chatMessage.setContent(message.getContent());
        chatMessage.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        return chatMessage;
    }
}
