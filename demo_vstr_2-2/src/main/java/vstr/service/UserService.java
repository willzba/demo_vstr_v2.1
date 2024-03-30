package vstr.service;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UserDetailsService;
import vstr.dto.UserDto;
import vstr.model.User;

import java.util.List;

public interface UserService  {

	 User save(UserDto userDto) throws EmailExistsException;

	User findByEmail(String email_id);
}
