package vstr.model;

public class WebRTCMessage {

    private String from; // ID del remitente
    private String to;   // ID del destinatario
    private String type; // Tipo de mensaje: "offer", "answer", "candidate"
    private String payload; // Contenido del mensaje

    public WebRTCMessage() {
    }

    public WebRTCMessage(String from, String to, String type, String payload) {
        this.from = from;
        this.to = to;
        this.type = type;
        this.payload = payload;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }
}
