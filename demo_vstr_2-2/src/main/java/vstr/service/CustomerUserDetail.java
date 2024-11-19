package vstr.service;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import vstr.model.Role;
import vstr.model.User;

public class CustomerUserDetail implements UserDetails {
	
	private User user;
	private Role role;
	

	public CustomerUserDetail(User user, Role role) {
		this.user = user;
		this.role=role;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// Ingreso segun los roles asignados desde la base de datos
		return List.of(()-> role.getName());
	}
	/*
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// Ingreso segun los roles asignados desde la base de datos
		List<GrantedAuthority> authorities = new ArrayList<>();
		for (Role role : user.getRoles()) {
			authorities.add(new SimpleGrantedAuthority(role.getNombre()));
		}
		return authorities;
	}*/

	/*
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// Retorna el rol del usuario como una autoridad
		return Collections.singletonList(new SimpleGrantedAuthority(role.getNombre()));
	}*/

	public String getFullname() {
		return user.getFullname();
	}


	@Override
	public String getPassword() {
		// TODO Auto-generated method stub
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return user.getEmail();
	}

	@Override
	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return true;
	}



}
