package vstr.controllers;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Random;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import vstr.dto.UserDto;
import vstr.model.User;
import vstr.model.VerifyToken;
import vstr.service.EmailExistsException;
import vstr.service.UserService;
import vstr.service.VerificationService;
import vstr.service.VerificationServiceImpl;

/**
 * Controller responsible for user registration, authentication, and verification.
 */
@Controller
public class UserController {

	@Autowired
	UserDetailsService userDetailsService;

	@Autowired
	private UserService userService;

	@Autowired
	private VerificationService verificationService;

	@Autowired
	private TemplateEngine templateEngine;

	@Autowired
	private JavaMailSender javaMailSender;

	@GetMapping("/registration")
	public String getRegistrationPage(@ModelAttribute("user") UserDto userDto) {
		return "register";
	}

	@PostMapping("/registration")
	public String saveUser(@ModelAttribute("user") UserDto userDto, Model model) {
		try {
			// Guardar el usuario en la base de datos
			User newUser = userService.save(userDto);

			// Generar OTP
			String otp = generateOTP();

			// Crear token de verificación y enviar correo de verificación
			verificationService.createVerificationToken(newUser, otp, LocalDateTime.now().plusMinutes(60));
			sendEmail(newUser.getEmail(), otp);

			model.addAttribute("message", "¡Registrado correctamente! Por favor, verifica tu correo electrónico.");
			return "register";
		} catch (EmailExistsException e) {
			model.addAttribute("error", "El correo electrónico ya está en uso");
			return "register"; // Mostrar la página de registro con un mensaje de error
		}
	}

	@GetMapping("/verify")
	public String showVerificationPage() {
		return "verification_code";
	}
	@GetMapping("/verify_success")
	public String showVerificationSuccessPage() {
		return "verification_success";
	}
	@PostMapping("/verify")
	public String verifyCode(@RequestParam("email_id") String email, @RequestParam("verificationCode") String verificationCode, Model model) {
		if (verificationService.verifyVerificationToken(email, verificationCode)) {
			// Código de verificación válido
			// Aquí podrías marcar el correo electrónico como verificado en tu base de datos si es necesario
			return "redirect:/verify_success"; // Redirige a la página de inicio de sesión u otra página de éxito
		} else {
			// Código de verificación inválido
			model.addAttribute("error", "Código de verificación inválido");
			model.addAttribute("email", email);
			return "verification_code"; // Vuelve a cargar la página de verificación con un mensaje de error
		}
	}

	// Método para generar un OTP
	private String generateOTP() {
		// Generar un OTP aleatorio de 5 dígitos
		Random random = new Random();
		int otpNumber = 10000 + random.nextInt(90000);
		return String.valueOf(otpNumber);
	}

	@PostMapping("/sendOTP")
	public ResponseEntity<String> sendOTP(@RequestParam("email") String email) {
		// Verificar si el usuario existe
		User user = userService.findByEmail(email);
		if (user == null) {
			return ResponseEntity.badRequest().body("Usuario no encontrado");
		}

		// Generar y enviar OTP
		String otp = generateOTP(); // Implementa esta función para generar un OTP
		verificationService.createVerificationToken(user, otp, LocalDateTime.now().plusMinutes(60)); // Token válido por 60 minutos
		// Aquí deberías enviar el OTP por correo electrónico

		return ResponseEntity.ok("OTP enviado correctamente");
	}

	private void sendEmail(String to, String otp) {
		MimeMessage message = javaMailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");

		try {
			Context context = new Context();
			context.setVariable("otp", otp);
			String verificationLink = "window.location.href='/verify'" + to; // Enlace de verificación con el correo electrónico como parámetro
			String htmlContent = templateEngine.process("verification_email", context);
			htmlContent += "<p>Para verificar tu correo electrónico, por favor visita el siguiente enlace: <a href=\"" + verificationLink + "\">Verificar</a></p>";

			helper.setText(htmlContent, true);
			helper.setTo(to);
			helper.setSubject("Verificación de correo electrónico");
			helper.setFrom("vsrtapp@gmail.com");
			javaMailSender.send(message);

		} catch (MessagingException e) {
			e.printStackTrace();
		}

	}

	@PostMapping("/verifyOTP")
	public ResponseEntity<String> verifyOTP (@RequestParam("email") String email, @RequestParam("otp") String otp){
		// Verificar el token
		if (verificationService.verifyVerificationToken(email, otp)) {
			// Actualizar el estado del token a verificado en la base de datos
			verificationService.verifyEmail(email);
			return ResponseEntity.ok("OTP verificado correctamente");
		} else {
			return ResponseEntity.badRequest().body("OTP no válido");
		}
	}

	@PostMapping("/login")
	public String login(@RequestParam("email") String email, @RequestParam("password") String password, Model model, Principal principal) {
		// Verificar si el correo electrónico del usuario está verificado
		boolean emailVerified = verificationService.isEmailVerified(email);
		if (emailVerified) {
			// Cargar al usuario por su correo electrónico
			User user = userService.findByEmail(email);
			if (user != null && user.getPassword().equals(password)) {
				// Usuario y contraseña válidos
				// Aquí puedes agregar más lógica de inicio de sesión si es necesario
				UserDetails userDetails = userDetailsService.loadUserByUsername(email);
				model.addAttribute("user", userDetails);
				return "redirect:/user-page"; // Redirigir a la página de usuario
			} else {
				// Usuario o contraseña incorrectos
				model.addAttribute("error", "Usuario o contraseña incorrectos");
				return "login"; // Volver a cargar la página de inicio de sesión con un mensaje de error
			}
		} else {
			// Correo electrónico no verificado
			model.addAttribute("error", "El correo electrónico no ha sido verificado");
			return "login"; // Volver a cargar la página de inicio de sesión con un mensaje de error
		}
	}

	@GetMapping("/login")
	public String login() {
		return "login";
	}

	@GetMapping("/nav_user")
	public String nav_user() {
		return "plantilla/nav_user";
	}

	@GetMapping("/dashboard")
	public String dashboard() {
		return "user_page/dashboard";
	}

	@GetMapping("/user-page")
	public String userPage(Model model, Principal principal) {
		String email = principal.getName(); // Obtener el correo electrónico del usuario autenticado
		boolean emailVerified = verificationService.isEmailVerified(email);
		if (emailVerified) {
			UserDetails userDetails = userDetailsService.loadUserByUsername(email);
			model.addAttribute("user", userDetails);
			return "user"; // Redirigir a la página de usuario
		} else {
			model.addAttribute("error", "El correo electrónico no ha sido verificado");
			return "login"; // Volver a cargar la página de inicio de sesión con un mensaje de error
		}
	}


	@GetMapping("admin-page")
	public String adminPage (Model model, Principal principal){
		String email = principal.getName(); // Obtener el correo electrónico del usuario autenticado
		boolean emailVerified = verificationService.isEmailVerified(email);
		if (emailVerified) {
			UserDetails userDetails = userDetailsService.loadUserByUsername(email);
			model.addAttribute("user", userDetails);
			return "admin"; // Redirigir a la página de usuario
		} else {
			model.addAttribute("error", "El correo electrónico no ha sido verificado");
			return "login"; // Volver a cargar la página de inicio de sesión con un mensaje de error
		}
	}

	@GetMapping("/nav_admin")
	public String nav_admin() {
		return "plantilla/nav_admin";
	}

	//cartelera de bienvenida


}



