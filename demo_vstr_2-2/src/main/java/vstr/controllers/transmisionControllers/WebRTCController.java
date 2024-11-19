package vstr.controllers.transmisionControllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import vstr.model.WebRTCMessage;

@Controller
public class WebRTCController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/webrtc.sendMessage")
    public void handleWebRTCMessage(WebRTCMessage message) {
        System.out.println("Mensaje WebRTC recibido: " + message);
        if (message.getTo().equals("all")) {
            // Broadcast a todos los viewers
            messagingTemplate.convertAndSend("/topic/webrtc", message);
            System.out.println("Mensaje WebRTC enviado a todos.");
        } else {
            // Enviar al destinatario específico (si es necesario)
            messagingTemplate.convertAndSend("/topic/webrtc", message);
            System.out.println("Mensaje WebRTC enviado a: " + message.getTo());
        }
    }
}
