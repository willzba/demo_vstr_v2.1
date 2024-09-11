package vstr.service;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Service;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Custom success handler for Spring Security that redirects authenticated users
 * to specific pages based on their roles.
 *
 * This class implements AuthenticationSuccessHandler and overrides
 * the onAuthenticationSuccess method to specify custom behavior upon authentication success.
 *
 * The class handles redirection for users with different roles:
 * - Users with the "ADMIN" role are redirected to /admin-page.
 * - Users with the "USER" role are redirected to /user-page.
 * - Users with no specified role are redirected to /error.
 */
@Service
public class CustomSuccessHandler implements AuthenticationSuccessHandler{

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		// TODO Auto-generated method stub
		
		var authourities = authentication.getAuthorities();
		var roles = authourities.stream().map(GrantedAuthority::getAuthority).findFirst();
		
		if (roles.orElse("").equals("ADMIN")) {
			response.sendRedirect("/admin-page");
		} else if (roles.orElse("").equals("USER")) {
			response.sendRedirect("/user-page");
		} else {
			response.sendRedirect("/error");
		}
	}
}
