package vstr.controllers.transmisionControllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vstr.model.Video;
import vstr.service.transmisionService.VideoService;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

@Controller
public class VideoController {

    @Autowired
    private VideoService videoService;

    @Autowired
    UserDetailsService userDetailsService;

    @Autowired
    public VideoController(VideoService videoService) {
        this.videoService = videoService;
    }

    // Mostrar el formulario para cargar videos y listar videos existentes
    @GetMapping("/cargar-video/list")
    public String mostrarFormularioCargarVideo(Model model, Principal principal) {
        try {
            String email = principal.getName();
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            // Obtiene todos los videos públicos
            List<Video> videosPublicos = videoService.obtenerVideosPublicos();

            // También puedes obtener videos privados si es necesario
            List<Video> videosPrivados = videoService.obtenerVideosPrivados(email);

            model.addAttribute("user", userDetails);
            model.addAttribute("videosPublicos", videosPublicos);
            model.addAttribute("videosPrivados", videosPrivados);
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar los videos del usuario: " + e.getMessage());
            e.printStackTrace(); // Imprimir el error en la consola para depuración
        }
        return "user_page/videos";
    }



    // Manejar la subida de videos
    @PostMapping("/cargar-video")
    public String cargarVideo(@RequestParam("titulo") String titulo,
                              @RequestParam("video") MultipartFile videoFile,
                              @RequestParam(value = "publico", required = false, defaultValue = "false") boolean publico,
                              RedirectAttributes redirectAttributes,
                              Principal principal) {
        String username = principal.getName();
        try {
            videoService.subirVideo(titulo, videoFile, publico, username);

            // Agregar mensaje de éxito a Flash Attributes
            redirectAttributes.addFlashAttribute("mensaje", "Video cargado exitosamente.");

            // Redirigir a la lista de videos
            return "redirect:/cargar-video/list"; // Cambiar la URL a la de GET
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Error al cargar el video: " + e.getMessage());
            return "error"; // Redirigir a la misma URL en caso de error
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", "Error al cargar el video: " + e.getMessage());
            return "redirect:/cargar-video/list"; // Redirigir a la misma URL en caso de error
        }
    }



    // Endpoint para servir los videos por ID
    @GetMapping("/video/{id}")
    @ResponseBody
    public ResponseEntity<Resource> servirVideo(@PathVariable Long id, Principal principal) {
        Video video = videoService.obtenerVideoPorId(id)
                .orElseThrow(() -> new RuntimeException("Video no encontrado con ID: " + id));

        // Verificar si el video es público o si el usuario es el propietario
        if (!video.isPublico() && !videoService.esPropietario(video, principal.getName())) {
            throw new RuntimeException("No tienes permiso para ver este video.");
        }

        byte[] contenido = video.getContenido();

        Resource resource = new ByteArrayResource(contenido);

        // Determinar el tipo de contenido basado en la extensión
        String contentType = determinarTipoContenido(video.getExtension());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }

    /**
     * Determinar el tipo de contenido basado en la extensión del archivo
     *
     * @param extension Extensión del archivo
     * @return Tipo de contenido MIME
     */
    private String determinarTipoContenido(String extension) {
        if (extension == null) {
            return "application/octet-stream"; // Tipo de contenido por defecto
        }
        switch (extension) {
            case "mp4":
                return "video/mp4";
            case "avi":
                return "video/x-msvideo";
            case "mov":
                return "video/quicktime";
            case "wmv":
                return "video/x-ms-wmv";
            case "mkv":
                return "video/x-matroska";
            default:
                return "application/octet-stream";
        }
    }

    // Eliminar un video
    @PostMapping("/video/eliminar/{id}")
    public String eliminarVideo(@PathVariable Long id, Principal principal) {
        Video video = videoService.obtenerVideoPorId(id)
                .orElseThrow(() -> new RuntimeException("Video no encontrado con ID: " + id));

        // Verificar si el usuario es el propietario
        if (!videoService.esPropietario(video, principal.getName())) {
            throw new RuntimeException("No tienes permiso para eliminar este video.");
        }

        videoService.eliminarVideo(video);

        return "redirect:/cargar-video/list";
    }

    // Cambiar la visibilidad de un video
    @PostMapping("/video/cambiar-visibilidad/{id}")
    public String cambiarVisibilidadVideo(@PathVariable Long id, Principal principal) {
        Video video = videoService.obtenerVideoPorId(id)
                .orElseThrow(() -> new RuntimeException("Video no encontrado con ID: " + id));

        // Verificar si el usuario es el propietario
        if (!videoService.esPropietario(video, principal.getName())) {
            throw new RuntimeException("No tienes permiso para cambiar la visibilidad de este video.");
        }

        videoService.cambiarVisibilidad(video);

        return "redirect:/cargar-video/list";
    }

    @GetMapping("/user")
    public String obtenerVideosPublicos(Model model) {
        List<Video> videosPublicos = videoService.obtenerVideosPublicos(); // Método que devuelve solo videos públicos
        model.addAttribute("videosPublicos", videosPublicos);
        return "user"; // Nombre de la plantilla Thymeleaf (videosPublicos.html)
    }

    // Método para cambiar la visibilidad del video
    @PostMapping("/video/{id}/toggle-public")
    public String togglePublic(@PathVariable Long id, Principal principal, RedirectAttributes redirectAttributes) {
        if (principal == null) {
            return "redirect:/login"; // Manejo si el usuario no está autenticado
        }

        String username = principal.getName();
        Video video = videoService.obtenerVideoPorId(id)
                .orElseThrow(() -> new RuntimeException("Video no encontrado con ID: " + id));

        if (!videoService.esPropietario(video, username)) {
            redirectAttributes.addFlashAttribute("error", "No tienes permiso para cambiar la visibilidad de este video.");
            return "redirect:/cargar-video/list";
        }

        videoService.cambiarVisibilidad(video);
        redirectAttributes.addFlashAttribute("mensaje", "Visibilidad cambiada exitosamente.");
        return "redirect:/cargar-video/list";
    }


    @PostMapping("/video/{id}/delete")
    public String deleteVideo(@PathVariable Long id, Principal principal, Model model) {
        String username = principal.getName();
        Video video = videoService.obtenerVideoPorId(id)
                .orElseThrow(() -> new RuntimeException("Video no encontrado con ID: " + id));

        if (!videoService.esPropietario(video, username)) {
            model.addAttribute("error", "No tienes permiso para eliminar este video.");
            return "redirect:user_page/cargar-video";
        }

        videoService.eliminarVideo(video);
        model.addAttribute("mensaje", "Video eliminado exitosamente.");
        return "redirect:user_page/cargar-video";
    }
}
