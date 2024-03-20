package vstr.model;

import jakarta.persistence.*;

import java.util.Collection;

@Entity
@Table(name="users", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
public class User {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String fullname;
	
	private String email;
	
	private String password;

	@ManyToMany(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
	@JoinTable(
			name = "usuarios_roles",
			joinColumns = @JoinColumn(name = "usuario_id",referencedColumnName = "id"),
			inverseJoinColumns = @JoinColumn(name = "rol_id",referencedColumnName = "id")
	)
	private Collection<Role> roles;

	public User() {
	}

	public User(Long id, String fullname, String email, String password, Collection<Role> roles) {
		this.id = id;
		this.fullname = fullname;
		this.email = email;
		this.password = password;
		this.roles = roles;
	}

	public User(String fullname, String email, String password, Collection<Role> roles) {
		this.fullname = fullname;
		this.email = email;
		this.password = password;
		this.roles = roles;
	}

	public void addRole(Role role) {
		roles.add(role);
		role.getUsers().add(this);
	}

	// Método para eliminar un rol del usuario
	public void removeRole(Role role) {
		roles.remove(role);
		role.getUsers().add(this);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public Collection<Role> getRoles() {
		return roles;
	}

	public void setRoles(Collection<Role> roles) {
		this.roles = roles;
	}

	public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}
}
