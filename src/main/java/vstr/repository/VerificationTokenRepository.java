package vstr.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vstr.model.VerifyToken;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerifyToken,Long> {
    VerifyToken findByUserEmailAndToken(String email, String token);
    VerifyToken findByUserEmail(String email);
}
