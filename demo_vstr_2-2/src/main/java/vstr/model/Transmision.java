package vstr.model;

import jakarta.persistence.*;

@Entity
public class Transmision {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String streamerUsername;

    private String titulo;

    @Column(name = "en_vivo")
    private boolean enVivo;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private User usuario;

    public Transmision() {
    }

    public Transmision(Long id, String streamerUsername, String titulo, boolean enVivo, User usuario) {
        this.id = id;
        this.streamerUsername = streamerUsername;
        this.titulo = titulo;
        this.enVivo = enVivo;
        this.usuario = usuario;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStreamerUsername() {
        return streamerUsername;
    }

    public void setStreamerUsername(String streamerUsername) {
        this.streamerUsername = streamerUsername;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public boolean isEnVivo() {
        return enVivo;
    }

    public void setEnVivo(boolean enVivo) {
        this.enVivo = enVivo;
    }

    public User getUsuario() {
        return usuario;
    }

    public void setUsuario(User usuario) {
        this.usuario = usuario;
    }
}
