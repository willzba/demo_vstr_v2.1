package vstr.model;

import jakarta.persistence.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.Collection;
import java.util.Set;

@Entity
@Table(name="users", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
public class User {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String fullname;

	@NotBlank(message = "El correo electrónico es obligatorio")
	@Email(message = "Formato de correo electrónico inválido")
	@Column(unique = true)
	private String email;
	
	private String password;

	@ManyToMany(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
	@JoinTable(
			name = "usuarios_roles",
			joinColumns = @JoinColumn(name = "usuario_id",referencedColumnName = "id"),
			inverseJoinColumns = @JoinColumn(name = "rol_id",referencedColumnName = "id")
	)
	private Collection<Role> roles;

	@OneToMany(mappedBy = "usuario")
	private Set<Transmision> transmisiones;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<Video> videos;

	public User() {
	}

	public User(Long id, String fullname, String email, String password, Collection<Role> roles, Set<Transmision> transmisiones, Set<Video> videos) {
		this.id = id;
		this.fullname = fullname;
		this.email = email;
		this.password = password;
		this.roles = roles;
		this.transmisiones = transmisiones;
		this.videos = videos;
	}

	public User(String fullname, String email, String password, Collection<Role> roles, Set<Transmision> transmisiones, Set<Video> videos) {
		this.fullname = fullname;
		this.email = email;
		this.password = password;
		this.roles = roles;
		this.transmisiones = transmisiones;
		this.videos = videos;
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

	public Set<Transmision> getTransmisiones() {
		return transmisiones;
	}

	public void setTransmisiones(Set<Transmision> transmisiones) {
		this.transmisiones = transmisiones;
	}

	public Set<Video> getVideos() {
		return videos;
	}

	public void setVideos(Set<Video> videos) {
		this.videos = videos;
	}
}
