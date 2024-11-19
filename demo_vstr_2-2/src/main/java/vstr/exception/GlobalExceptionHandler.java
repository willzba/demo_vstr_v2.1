package vstr.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;


public class GlobalExceptionHandler {

    // Logger para registrar excepciones
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Maneja VideoNotFoundException
     */
    @ExceptionHandler(VideoNotFoundException.class)
    public String handleVideoNotFoundException(VideoNotFoundException ex, Model model) {
        logger.error("VideoNotFoundException capturada: ", ex);
        model.addAttribute("error", ex.getMessage());
        return "error"; // Nombre de tu plantilla de error
    }

    /**
     * Maneja UsuarioNotFoundException
     */
    @ExceptionHandler(UsuarioNotFoundException.class)
    public String handleUsuarioNotFoundException(UsuarioNotFoundException ex, Model model) {
        logger.error("UsuarioNotFoundException capturada: ", ex);
        model.addAttribute("error", ex.getMessage());
        return "error"; // Nombre de tu plantilla de error
    }

    /**
     * Maneja FileTypeNotAllowedException
     */
    @ExceptionHandler(FileTypeNotAllowedException.class)
    public String handleFileTypeNotAllowedException(FileTypeNotAllowedException ex, Model model) {
        logger.error("FileTypeNotAllowedException capturada: ", ex);
        model.addAttribute("error", ex.getMessage());
        return "error"; // Nombre de tu plantilla de error
    }

    /**
     * Maneja todas las excepciones de tipo RuntimeException
     */
    @ExceptionHandler(RuntimeException.class)
    public String handleRuntimeException(RuntimeException ex, Model model) {
        logger.error("RuntimeException capturada: ", ex);
        model.addAttribute("error", ex.getMessage());
        return "error"; // Nombre de tu plantilla de error
    }

    /**
     * Maneja todas las demás excepciones no especificadas
     */
    @ExceptionHandler(Exception.class)
    public String handleException(Exception ex, Model model) {
        logger.error("Exception capturada: ", ex);
        model.addAttribute("error", "Ha ocurrido un error inesperado.");
        return "error"; // Nombre de tu plantilla de error
    }
}
