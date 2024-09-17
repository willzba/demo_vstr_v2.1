package vstr.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vstr.model.Video;
import vstr.repository.VideoRepository;

import java.util.List;

@Service
public class VideoService {
    @Autowired
    private VideoRepository videoRepository;

    public List<Video> getVideosByUserEmail(String email) {
        return videoRepository.findByUserEmail(email);
    }

    public void saveVideo(Video video) {
        videoRepository.save(video);
    }

    public void deleteVideo(Long id) {
        videoRepository.deleteById(id);
    }

    public Video getVideoById(Long id) {
        return videoRepository.findById(id).orElse(null);
    }
}
