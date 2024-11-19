package vstr.service;

import vstr.dto.UserDto;
import vstr.model.User;

public interface UserService  {

	User save(UserDto userDto) throws EmailExistsException;

	User findByEmail(String email_id);

	User getAuthenticatedUser();
}
