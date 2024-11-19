package vstr.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import vstr.dto.UserDto;
import vstr.model.Role;
import vstr.model.User;
import vstr.repository.UserRepository;
import vstr.repository.UserRoleRepository;

import java.util.Arrays;
import java.util.HashSet;

@Service
public class userServiceImpl implements UserService{

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	UserRoleRepository roleRepository;


	@Override
	public User save(UserDto userDto) throws EmailExistsException {
		// Obtener y verificar el rol predeterminado
		Role defaultRole = roleRepository.findByName("USER");
		if (defaultRole == null) {
			throw new IllegalStateException("El rol predeterminado 'USER' no está configurado en la base de datos.");
		}

		// Verificar si el correo electrónico ya está en uso
		String email = userDto.getEmail();
		if (userRepository.existsByEmail(email)) {
			throw new EmailExistsException("El correo electrónico ya está en uso");
		}

		// Crear y configurar el nuevo usuario
		User usuario = new User();
		usuario.setFullname(userDto.getFullname());
		usuario.setEmail(email);
		usuario.setPassword(passwordEncoder.encode(userDto.getPassword()));

		// Verificar si el rol especificado en el DTO existe, de lo contrario asignar 'USER'
		Role rol = roleRepository.findByName(userDto.getRole());
		if (rol == null) {
			rol = defaultRole; // asignar el rol 'USER' como predeterminado si no se encuentra el rol especificado
		}

		// Asignar roles al usuario
		HashSet<Role> roles = new HashSet<>();
		roles.add(rol);
		usuario.setRoles(roles);

		// Guardar y retornar el usuario creado
		return userRepository.save(usuario);
	}

	@Override
	public User findByEmail(String email) {
		return userRepository.findByEmail(email);
	}


	@Override
	public User getAuthenticatedUser() {
		// Obtener el contexto de seguridad para recuperar el usuario autenticado
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
			String email = ((UserDetails) authentication.getPrincipal()).getUsername();
			return userRepository.findByEmail(email);
		}
		return null; // Si no hay usuario autenticado
	}

	/*
	public User registrarUsuario(String username, String password, String rolNombre) {
		if (userRepository.findByUsername(username) != null) {
			throw new RuntimeException("Usuario ya existe con el nombre de usuario: " + username);
		}

		User usuario = new User();
		usuario.setFullname(username);
		usuario.setPassword(passwordEncoder.encode(password));

		Role rol = roleRepository.findByName(rolNombre);
		if (rol == null) {
			rol = new Role();
			rol.setName(rolNombre);
			roleRepository.save(rol);
		}

		HashSet<Role> roles = new HashSet<>();
		roles.add(rol);
		usuario.setRoles(roles);

		return userRepository.save(usuario);
	}*/
}
