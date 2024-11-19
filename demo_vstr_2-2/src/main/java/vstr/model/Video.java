package vstr.model;

import jakarta.persistence.*;

@Entity
public class Video {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(columnDefinition = "longblob")
    private byte[] contenido;

    private String titulo;

    private String url; // Ruta o URL del video

    private boolean publico;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private User user; // Relación con el usuario

    private String extension;

    public Video() {
    }

    public Video(Long id, byte[] contenido, String titulo, String url, boolean publico, User user, String extension) {
        this.id = id;
        this.contenido = contenido;
        this.titulo = titulo;
        this.url = url;
        this.publico = publico;
        this.user = user;
        this.extension = extension;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public byte[] getContenido() {
        return contenido;
    }

    public void setContenido(byte[] contenido) {
        this.contenido = contenido;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isPublico() {
        return publico;
    }

    public void setPublico(boolean publico) {
        this.publico = publico;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }
}
