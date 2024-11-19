package vstr.controllers.transmisionControllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import vstr.model.Video;
import vstr.service.transmisionService.VideoService;

import java.security.Principal;
import java.util.List;

@Controller
public class ViewerController {
    private final VideoService videoService;
    @Autowired
    UserDetailsService userDetailsService;

    private static final Logger logger = LoggerFactory.getLogger(StreamController.class);


    // Inyección de dependencias por constructor
    @Autowired
    public ViewerController(VideoService videoService) {
        this.videoService = videoService;
    }

    @GetMapping("/videos-publicos")
    @PreAuthorize("isAuthenticated()")
    public String mostrarVideosPublicos(Model model, Principal principal) {

        try {
            String email = principal.getName();
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            List<Video> videosPublicos = videoService.obtenerVideosPublicos();

            model.addAttribute("user", userDetails);
            model.addAttribute("videosPublicos", videosPublicos);
        } catch (Exception e) {
            logger.error("Error al cargar los videos publicos" + e.getMessage());
            model.addAttribute("error", e.getMessage());
        }

        return "user";
    }

    //api para ver el mis videos de forma correcta
    @GetMapping("/videos-all-publicos")
    public String userVideos(Model model, Principal principal) {
        try {
            String email = principal.getName();
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            // Obtiene todos los videos públicos
            List<Video> videosPublicos = videoService.obtenerVideosPublicos();

            model.addAttribute("user", userDetails);
            model.addAttribute("videosPublicos", videosPublicos);

            return "user_page/videos-publicos"; // Nombre del archivo HTML para la plantilla de videos
        } catch (Exception e) {
            model.addAttribute("error", "Ocurrió un error al cargar los videos del usuario");
            return "error"; // Página de error personalizada si es necesario
        }
    }
}
