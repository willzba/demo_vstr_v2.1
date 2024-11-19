package vstr.controllers.transmisionControllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;
import vstr.dto.TransmisionDto;
import vstr.exception.UsuarioNotFoundException;
import vstr.model.Transmision;
import vstr.model.User;
import vstr.model.WebRTCMessage;
import vstr.repository.TransmisionRepository;
import vstr.repository.UserRepository;
import vstr.service.UserService;
import vstr.service.transmisionService.TransmisionService;
import vstr.service.transmisionService.VideoService;

import java.security.Principal;
import java.util.Map;
import java.util.Optional;


@Controller
public class StreamController {

    @Autowired
    private UserService userService;

    @Autowired
    UserDetailsService userDetailsService;

    @Autowired
    private VideoService videoService;

    @Autowired
    private TransmisionService transmisionService;

    @Autowired
    private TransmisionRepository transmisionRepository;

    @Autowired
    private UserRepository usuarioRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private static final Logger logger = LoggerFactory.getLogger(StreamController.class);
    @Autowired
    private vstr.service.userServiceImpl userServiceImpl;


    /*@GetMapping("/streamer/dashboard")
    public String mostrarDashboardStreamer(Model model) {
        // Puedes agregar contenido adicional al dashboard si lo deseas
        return "dashboard-streamer";  // El nombre del archivo HTML
    }*/


    /*@GetMapping("/cargar-videos")
    @PreAuthorize("isAuthenticated()") // Permite acceso solo a usuarios autenticados
    public String mostrarVista(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = null;

        if (authentication != null && authentication.isAuthenticated()) {
            // Suponiendo que tu UserDetails implementa User
            user = (User) authentication.getPrincipal();
        }

        model.addAttribute("usuario", user); // Asegúrate de que no sea null
        return "cargar-videos";
    }*/

    /*@GetMapping("/user-videos")
    public String userVideos(Model model, Principal principal) {
        try {
            String email = principal.getName();
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            // Recuperar videos privados del usuario desde el servicio
            List<Video> userVideos = videoService.obtenerVideosPrivados(email);
            // Recuperar videos públicos desde el servicio
            List<Video> videosPublicos = videoService.obtenerVideosPublicos();

            model.addAttribute("user", userDetails);
            model.addAttribute("userVideos", userVideos); // Asegúrate de usar "userVideos"
            model.addAttribute("videosPublicos", videosPublicos); // Agrega esta línea

            return "user_page/videos"; // Nombre del archivo HTML para la plantilla de videos
        } catch (Exception e) {
            logger.error("Error al cargar los videos del usuario con email: " + principal.getName(), e);
            model.addAttribute("error", "Ocurrió un error al cargar los videos del usuario");
            return "error"; // Página de error personalizada si es necesario
        }
    }*/


    public User getCurrentUser() {
        // Usa el método getAuthenticatedUser del servicio
        return userService.getAuthenticatedUser();
    }

    @GetMapping("/current-user-details")
    @ResponseBody
    public User getCurrentUserDetails() {
        User currentUser = getCurrentUser();
        if (currentUser != null) {
            return currentUser;
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No hay usuario autenticado");
        }
    }

        /*@GetMapping("/cargar-videos")
    public String userVideos(Model model, Principal principal) {
        if (principal == null) {
            model.addAttribute("error", "Usuario no autenticado");
            return "error"; // Página de error personalizada si es necesario
        }

        String email = principal.getName();

        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            // Recuperar videos del usuario desde un servicio o repositorio
            List<Video> videos = videoService.obtenerVideosPrivados(email); // Asegúrate de implementar este método en tu servicio

            model.addAttribute("user", userDetails);
            model.addAttribute("videos", videos);

            return "user_page/cargar-videos"; // Nombre del archivo HTML para la plantilla de videos
        } catch (Exception e) {
            model.addAttribute("error", "Ocurrió un error al cargar los videos del usuario");
            return "error"; // Página de error personalizada si es necesario
        }
    }*/


    @GetMapping("/iniciar-transmision")
    @PreAuthorize("hasRole('USER')")
    public String iniciarTransmision(Model model, Principal principal) {
        String email = principal.getName();
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        logger.info("Usuario {} está intentando iniciar una transmisión.", email);

        // Obtener el usuario que está intentando iniciar la transmisión por su correo electrónico
        User usuario = usuarioRepository.findByEmail(email);
        if (usuario == null) {
            throw new UsuarioNotFoundException("Usuario no encontrado: " + email);
        }

        // Verificar si el usuario ya tiene una transmisión activa
        Optional<Transmision> transmisionActiva = transmisionRepository.findByUsuarioEmailAndEnVivoTrue(email);
        if (transmisionActiva.isPresent()) {
            logger.warn("Usuario {} ya tiene una transmisión activa. No puede iniciar otra.", email);
            model.addAttribute("error", "Ya tienes una transmisión activa.");
            return "error";
        }

        // Crear una nueva transmisión para el usuario
        Transmision transmision = new Transmision();
        transmision.setTitulo("Transmisión en Vivo de " + usuario.getFullname());
        transmision.setEnVivo(false); // La transmisión comienza inactiva
        transmision.setUsuario(usuario);

        try {
            // Guardar la nueva transmisión en la base de datos
            transmisionRepository.save(transmision);
            logger.info("Transmisión iniciada: ID={}, Streamer={}", transmision.getId(), usuario.getFullname());

            // Añadir la transmisión al modelo para ser usada en la vista
            model.addAttribute("user", userDetails);
            model.addAttribute("transmision", transmision);
            model.addAttribute("transmisionId", transmision.getId()); // Enviar el ID al frontend si es necesario

            // Redirigir a la página de inicio de transmisión o mostrar la vista de la transmisión
            return "user_page/iniciar-transmision"; // Si quieres mostrar la vista sin redirección
            // return "redirect:/user_page/iniciar-transmision"; // Si prefieres redirigir
        } catch (Exception e) {
            logger.error("Error al iniciar la transmisión para el usuario {}: {}", email, e.getMessage());
            model.addAttribute("error", "Error al iniciar la transmisión. Por favor, inténtalo de nuevo.");
            return "error"; // Mostrar página de error si ocurre un problema
        }
    }

    /*@PostMapping("/crear-transmision")
    public ResponseEntity<?> crearTransmision(@RequestBody Transmision transmisionDto) {
        Transmision transmision = transmisionService.save(transmisionDto);
        return ResponseEntity.ok(transmision.getId()); // Devuelve el ID de la transmisión creada
    }*/

    @PostMapping("/actualizar-transmision-a-vivo")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> actualizarTransmisionAVivo(@RequestBody Map<String, Long> payload, Principal principal) {
        Long transmisionId = payload.get("transmisionId");

        // Obtener el usuario autenticado por correo electrónico
        String username = principal.getName();
        User usuario = usuarioRepository.findByEmail(username);

        // Verificar si el usuario existe
        if (usuario == null) {
            throw new RuntimeException("Usuario no encontrado");
        }

        // Buscar la transmisión por ID
        Transmision transmision = transmisionRepository.findById(transmisionId)
                .orElseThrow(() -> new RuntimeException("Transmisión no encontrada con ID: " + transmisionId));

        // Verificar si el usuario autenticado es el streamer de la transmisión
        if (!transmision.getUsuario().getId().equals(usuario.getId())) {
            throw new AccessDeniedException("No tienes permiso para actualizar esta transmisión");
        }

        // Actualizar el estado de la transmisión a "en vivo"
        transmision.setEnVivo(true);
        transmisionRepository.save(transmision);

        logger.info("Transmisión {} ha sido actualizada a 'en vivo'.", transmisionId);

        return ResponseEntity.ok("Transmisión iniciada");
    }

    //endpoin para actualizar la transmision a no en vivo cuando se detenga la transmision
    @PostMapping("/actualizar-transmision-a-no-vivo")
    //@PreAuthorize("hasRole('USER')")
    @ResponseBody
    public ResponseEntity<?> actualizarTransmisionANoVivo(@RequestBody Map<String, Long> payload, Principal principal) {
        Long transmisionId = payload.get("transmisionId");

        // Obtener el usuario autenticado por correo electrónico
        String username = principal.getName();
        User usuario = usuarioRepository.findByEmail(username);

        // Busca la transmisión por ID
        Optional<Transmision> optionalTransmision = transmisionRepository.findById(transmisionId);
        if (optionalTransmision.isPresent()) {
            Transmision transmision = optionalTransmision.get();

            // Asegurarse de que la transmisión pertenece al usuario autenticado
            if (!transmision.getUsuario().getEmail().equals(username)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tienes permiso para actualizar esta transmisión.");
            }

            // Cambia el estado a false (no en vivo)
            transmision.setEnVivo(false);
            transmisionRepository.save(transmision);

            // Notificar al streamer que la transmisión ha sido terminada
            String streamerUsername = transmision.getUsuario().getFullname();
            String notificationMessage = "La transmisión ha terminado debido a un evento imprevisto.";

            // Envía el mensaje a través de WebSocket
            messagingTemplate.convertAndSendToUser(streamerUsername, "/queue/notifications", notificationMessage);

            logger.info("Transmisión {} ha sido actualizada a 'no vivo' por el usuario {}.", transmisionId, usuario.getFullname());

            return ResponseEntity.ok("Transmisión actualizada a no viva.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Transmisión no encontrada.");
        }
    }



    // Endpoint para detener una transmisión
    @GetMapping("/detener-transmision")
    public String detenerTransmision(Model model, Principal principal) {
        String username = principal.getName();
        logger.info("Usuario {} está intentando detener una transmisión.", username);

        Optional<Transmision> transmisionActiva = transmisionRepository.findByEnVivoTrue().stream().findFirst();
        if (transmisionActiva.isPresent()) {
            Transmision transmision = transmisionActiva.get();
            logger.info("Transmisión activa encontrada: ID={}, Streamer={}", transmision.getId(), transmision.getUsuario().getFullname());

            // Verificar si el usuario actual es el streamer de la transmisión
            if (transmision.getUsuario().getEmail().equals(username)) {
                transmision.setEnVivo(false);
                transmisionRepository.save(transmision);
                logger.info("Transmisión ID={} detenida por el streamer {}.", transmision.getId(), username);

                // Notificar a los viewers que la transmisión ha sido detenida
                WebRTCMessage stopMessage = new WebRTCMessage();
                stopMessage.setFrom("streamer");
                stopMessage.setTo("all");
                stopMessage.setType("stop");
                stopMessage.setPayload(null);
                messagingTemplate.convertAndSend("/topic/webrtc", stopMessage);
                logger.info("Mensaje 'stop' enviado a todos los viewers.");

                // Informar al usuario sobre la detención
                model.addAttribute("mensaje", "Transmisión detenida correctamente. Ya puedes iniciar una nueva transmisión.");
                model.addAttribute("nombreUsuario", transmision.getUsuario().getFullname()); // Mostrar el nombre completo del usuario

                // Retornar la vista donde se puede iniciar una nueva transmisión
                return "user_page/iniciar-transmision"; // Cambia a la vista de nueva transmisión
            } else {
                logger.warn("Usuario {} no tiene permiso para detener la transmisión ID={}.", username, transmision.getId());
                model.addAttribute("error", "No tienes permiso para detener esta transmisión.");
                return "error"; // Esto puede ser una vista de error más amigable
            }
        }

        logger.warn("No hay transmisiones activas para detener.");
        model.addAttribute("mensaje", "Transmision detenida, Dale en el boton \uD83D\uDC47. (GRACIAS)."); // Mensaje más amigable
        return "error"; // Cambia a la vista de nueva transmisión
    }
}
