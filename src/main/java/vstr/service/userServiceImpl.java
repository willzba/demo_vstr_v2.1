package vstr.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import vstr.dto.UserDto;
import vstr.model.Role;
import vstr.model.User;
import vstr.repository.UserRepository;
import vstr.repository.UserRoleRepository;

import java.util.Arrays;

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
		// Buscar el rol predeterminado
		Role defaultRole = roleRepository.findByName("USER");

		String email = userDto.getEmail();

		// Verificar si el correo electrónico ya está en uso
		if (userRepository.existsByEmail(email)) {
			throw new EmailExistsException("El correo electrónico ya está en uso");
		}

		// Verificar si el rol predeterminado existe
		if (defaultRole == null) {
			throw new IllegalStateException("El rol predeterminado 'USER' no está configurado en la base de datos.");
		}

		User user = new User(userDto.getFullname(), userDto.getEmail(), passwordEncoder.encode(userDto.getPassword()), Arrays.asList(defaultRole));
		return userRepository.save(user);
	}

	@Override
	public User findByEmail(String email) {
		return userRepository.findByEmail(email);
	}

}
