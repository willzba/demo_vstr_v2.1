package vstr.dto;

/**
 * Data Transfer Object for User entity.
 * This class is used to transfer data between the layers of the application.
 */
public class UserDto {

	private String fullname;

	private String email;

	private String password;
	private String role;

	public UserDto(String fullname, String email, String password, String role) {
		this.fullname = fullname;
		this.email = email;
		this.password = password;
		this.role = role;
	}

	public String getFullname() {
		return fullname;
	}

	public UserDto() {
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}
}
