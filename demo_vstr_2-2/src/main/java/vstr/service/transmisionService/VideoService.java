package vstr.service.transmisionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import vstr.exception.FileTypeNotAllowedException;
import vstr.exception.UsuarioNotFoundException;
import vstr.model.User;
import vstr.model.Video;
import vstr.repository.UserRepository;
import vstr.repository.VideoRepository;

import java.io.IOException;
import java.util.*;

@Service
public class VideoService {
    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private UserRepository usuarioRepository;

    /**
     * Método para subir un video
     *
     * @param titulo    Título del video
     * @param file      Archivo de video
     * @param publico   Indicador de si el video es público
     * @param email  Nombre de usuario que sube el video
     * @return          Objeto Video guardado
     * @throws IOException Si ocurre un error al leer el archivo
     */
    public Video subirVideo(String titulo, MultipartFile file, boolean publico, String email) throws IOException {
        User usuario = usuarioRepository.findByEmail(email);
        if (usuario == null) {
            throw new UsuarioNotFoundException("Usuario no encontrado: " + email);
        }

        // Limpiar el nombre del archivo
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        // Validar el nombre del archivo
        if(fileName.contains("..")) {
            throw new RuntimeException("Nombre de archivo inválido: " + fileName);
        }

        // Extraer la extensión del archivo
        String extension = "";
        int index = fileName.lastIndexOf('.');
        if (index > 0) {
            extension = fileName.substring(index + 1).toLowerCase();
        }

        // Validar el tipo de archivo
        if (extension.isEmpty()) {
            throw new RuntimeException("El archivo no tiene una extensión válida.");
        }

        if (!isAllowedExtension(extension)) {
            throw new FileTypeNotAllowedException("Tipo de archivo no permitido: ." + extension);
        }

        // Convertir el archivo a bytes
        byte[] contenido = file.getBytes();

        // Crear el objeto Video
        Video video = new Video();
        video.setTitulo(titulo);
        video.setContenido(contenido); // Establecer contenido
        video.setPublico(publico);
        video.setUser(usuario);
        video.setExtension(extension); // Establecer extensión

        return videoRepository.save(video);
    }


    /**
     * Obtener una lista de videos públicos
     *
     * @return Lista de videos públicos
     */
    public List<Video> obtenerVideosPublicos() {
        List<Video> videos = videoRepository.findByPublicoTrue();
        System.out.println("Total de videos públicos: " + videos.size());
        return videos;
    }

    /**
     * Obtener una lista de videos privados del usuario
     *
     * @param email Nombre de usuario
     * @return Lista de videos privados del usuario
     */
    public List<Video> obtenerVideosPrivados(String email) {
        try {
            return videoRepository.findVideosByUsuarioEmailAndPublicoFalse(email);
        } catch (Exception e) {
            System.err.println("Error ejecutando la consulta para obtener videos privados:");
            e.printStackTrace();
            return Collections.emptyList(); // Devuelve una lista vacía si hay un error
        }
    }

    /**
     * Obtener un video por su ID
     *
     * @param id ID del video
     * @return Optional con el video si existe
     */
    public Optional<Video> obtenerVideoPorId(Long id) {
        return videoRepository.findById(id);
    }

    /**
     * Verificar si el usuario es el propietario del video
     *
     * @param video     Objeto Video
     * @param username  Nombre de usuario
     * @return          true si es el propietario, false en caso contrario
     */
    public boolean esPropietario(Video video, String username) {
        return video.getUser().getEmail().equals(username);
    }

    /**
     * Cambiar el estado de visibilidad de un video
     *
     * @param video Video a actualizar
     */
    public void cambiarVisibilidad(Video video) {
        video.setPublico(!video.isPublico());
        videoRepository.save(video);
    }

    /**
     * Eliminar un video
     *
     * @param video Video a eliminar
     */
    public void eliminarVideo(Video video) {
        videoRepository.delete(video);
    }

    public void cargarVideo(Video video) {
        videoRepository.save(video);
    }

    /**
     * Método para verificar si la extensión del archivo está permitida
     *
     * @param extension Extensión del archivo
     * @return true si está permitida, false en caso contrario
     */
    private boolean isAllowedExtension(String extension) {
        List<String> allowedExtensions = Arrays.asList("mp4", "avi", "mov", "wmv", "mkv"); // Agrega más según sea necesario
        return allowedExtensions.contains(extension);
    }

}
