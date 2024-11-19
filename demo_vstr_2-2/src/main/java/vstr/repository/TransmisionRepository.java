package vstr.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vstr.model.Transmision;
import vstr.model.User;

import java.util.List;
import java.util.Optional;

public interface TransmisionRepository extends JpaRepository<Transmision, Long> {

    List<Transmision> findByEnVivoTrue();
    List<Transmision> findByEnVivoFalse();
    Optional<Transmision> findByUsuarioEmailAndEnVivoTrue(String email);
    Transmision findByStreamerUsername(String username);
}
