package vstr.configurations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import vstr.service.CustomSuccessHandler;
import vstr.service.CustomerUserDetailService;

/**
 * Configuration class that sets up security settings for the application.
 * Uses Spring Security annotations to configure authentication and authorization.
 */
@Configuration
@EnableWebSecurity
public class ConfigurationSecurity {
	
	@Autowired
	CustomSuccessHandler customSuccessHandler;
	
	@Autowired
	 CustomerUserDetailService customerUserDetailService;

	@Bean
	public static PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
		
		http.csrf(AbstractHttpConfigurer::disable)
				
				.authorizeHttpRequests(request -> request.requestMatchers("/admin-page","/nav_admin")
						.hasAuthority("ADMIN").requestMatchers("/user-page","/nav_user","/user_page/**").hasAuthority("USER")
						.requestMatchers("/registration", "/css/**","/verify","/verify_success").permitAll()
						.anyRequest().authenticated())
				
				.formLogin(form -> form.loginPage("/login").loginProcessingUrl("/login")
						.successHandler(customSuccessHandler).permitAll())
				
				.logout(form -> form.invalidateHttpSession(true).clearAuthentication(true)
						.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
						.logoutSuccessUrl("/login?logout").permitAll());
				
				return http.build();
	}
	
	@Autowired
	public void configure (AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(customerUserDetailService).passwordEncoder(passwordEncoder());
	}
}
