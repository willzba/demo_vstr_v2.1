package vstr.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import vstr.dto.UserDto;
import vstr.model.User;
import vstr.repository.UserRepository;

@Service
public class userServiceImpl implements UserService{
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private UserRepository userRepository;

	@Override
	public User save(UserDto userDto) {
		User user= new User(userDto.getEmail(),passwordEncoder.encode( userDto.getPassword()),userDto.getRole(),userDto.getFullname());
		return userRepository.save(user);
	}
	
}
