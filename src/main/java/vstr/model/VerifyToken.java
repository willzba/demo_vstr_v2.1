package vstr.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "VerificationToken")
public class VerifyToken {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "email_id", referencedColumnName = "email")
    private User user;

    private String token;
    private LocalDateTime expiryDate;
    private boolean verified;

    public VerifyToken() {
    }

    public VerifyToken(User user) {
        this.user = user;
    }

    public VerifyToken(User user, String token, LocalDateTime expiryDate) {
        this.user = user;
        this.token = token;
        this.expiryDate = expiryDate;
    }

    public VerifyToken(Long id, User user, String token, LocalDateTime expiryDate, boolean verified) {
        this.id = id;
        this.user = user;
        this.token = token;
        this.expiryDate = expiryDate;
        this.verified = verified;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }
}
