package vstr.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vstr.model.User;
import vstr.model.VerifyToken;
import vstr.repository.VerificationTokenRepository;

import java.time.LocalDateTime;

@Service
public class VerificationServiceImpl implements VerificationService{

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Override
    public void createVerificationToken(User user, String token, LocalDateTime expiryDate) {
        VerifyToken verificationToken = new VerifyToken();
        verificationToken.setUser(user);
        verificationToken.setToken(token);
        verificationToken.setExpiryDate(expiryDate);
        verificationToken.setVerified(false);
        verificationTokenRepository.save(verificationToken);
    }

    @Override
    public boolean verifyVerificationToken(String email, String token) {
        // Buscar el token de verificación por correo electrónico y token
        VerifyToken verificationToken = verificationTokenRepository.findByUserEmailAndToken(email, token);
        if (verificationToken != null) {
            // Actualizar la columna verified a true
            verificationToken.setVerified(true);
            verificationTokenRepository.save(verificationToken);
            return true;
        }
        return false;
    }

    @Override
    public void verifyEmail(String email) {
        // Buscar el token de verificación por correo electrónico
        VerifyToken token = verificationTokenRepository.findByUserEmail(email);
        if (token != null) {
            // Actualizar el estado del token a verificado en la base de datos
            token.setVerified(true);
            verificationTokenRepository.save(token);
        }
    }

    @Override
    public boolean isEmailVerified(String email) {
        // Verificar si el correo electrónico está verificado en la tabla VerificationToken
        VerifyToken token = verificationTokenRepository.findByUserEmail(email);
        return token != null && token.isVerified();

    }


}
