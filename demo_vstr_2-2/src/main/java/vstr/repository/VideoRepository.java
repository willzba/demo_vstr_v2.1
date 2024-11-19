package vstr.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vstr.model.User;
import vstr.model.Video;

import java.util.List;
import java.util.Optional;

public interface VideoRepository extends JpaRepository<Video, Long> {

    List<Video> findByUser_Email(String email);

    List<Video> findByPublicoTrue();
    Optional<Video> findByUrl(String url);
    List<Video> findByPublicoFalseAndUser(User user);

    // Método para encontrar videos privados del usuario por su email
    @Query("SELECT v FROM Video v WHERE v.user.email = :email AND v.publico = false")
    List<Video> findVideosByUsuarioEmailAndPublicoFalse(@Param("email") String email);
}
