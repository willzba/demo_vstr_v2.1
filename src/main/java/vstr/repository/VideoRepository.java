package vstr.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vstr.model.Video;

import java.util.List;

public interface VideoRepository extends JpaRepository<Video, Long> {

    List<Video> findByUserEmail(String email);
}
