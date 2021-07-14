package com.charliechiang.wastesortinghelperserver.config;

import com.charliechiang.wastesortinghelperserver.repository.UserRepository;
import com.charliechiang.wastesortinghelperserver.security.JwtTokenAuthenticationFilter;
import com.charliechiang.wastesortinghelperserver.security.JwtTokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain springWebFilterChain(HttpSecurity http,
                                             JwtTokenProvider tokenProvider) throws Exception {

        return http
                .httpBasic(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(c -> c.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(c -> c.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                .authorizeRequests(c -> c
                                           .antMatchers("/").permitAll()
                                           // ---------- AuthenticationController ----------
                                           // Permit All - sign in
                                           .antMatchers("/api/v1/auth/login").permitAll()

                                           // ---------- DustbinController ----------
                                           // Admin only - add dustbins
                                           .antMatchers(HttpMethod.POST, "/api/v1/dustbins").hasRole("ADMIN")
                                           // Admin only - delete dustbins
                                           .antMatchers(HttpMethod.DELETE, "/api/v1/dustbins/**").hasRole("ADMIN")
                                           // Permit All - allow dustbins to change whether it is full
                                           .antMatchers(HttpMethod.POST, "/api/v1/dustbins/*/full").permitAll()

                                           // ---------- SchoolController ----------
                                           // Permit All - get school list
                                           .antMatchers(HttpMethod.GET, "/api/v1/schools").permitAll()
                                           // Admin only - add schools
                                           .antMatchers(HttpMethod.POST, "/api/v1/schools/**").hasRole("ADMIN")
                                           // Admin only - delete schools
                                           .antMatchers(HttpMethod.DELETE, "/api/v1/schools/**").hasRole("ADMIN")

                                           // ServerSettingsController
                                           // Admin only - change server settings
                                           .antMatchers("/api/v1/settings/**").hasRole("ADMIN")

                                           // ---------- UserController ----------
                                           // Admin only - get user list
                                           .antMatchers(HttpMethod.GET, "/api/v1/users").hasRole("ADMIN")
                                           // Permit All - sign up
                                           .antMatchers(HttpMethod.POST, "/api/v1/users").permitAll()
                                           // Authenticate - about me
                                           .antMatchers(HttpMethod.GET, "/api/v1/users/me/**").authenticated()
                                           // Authenticate - update me
                                           .antMatchers(HttpMethod.PUT, "/api/v1/users/me/**").authenticated()
                                           // Authenticate - update my tree info
                                           .antMatchers(HttpMethod.PUT, "/api/v1/users/me/tree").authenticated()
                                           // Authenticate - get my tree info
                                           .antMatchers(HttpMethod.GET, "/api/v1/users/me/tree").authenticated()
                                           // Authenticate - update another user's credit (temporary workaround)
                                           .antMatchers(HttpMethod.PUT, "/api/v1/users/*/credit").authenticated()
                                           // Authenticate - get another user's credit (temporary workaround)
                                           .antMatchers(HttpMethod.GET, "/api/v1/users/*/credit").authenticated()
                                           // Authenticate - offset another user's credit (temporary workaround)
                                           .antMatchers(HttpMethod.PUT, "/api/v1/users/*/credit/offset").authenticated()
                                           // Admin only - get a user
                                           .antMatchers(HttpMethod.GET, "/api/v1/users/**").hasRole("ADMIN")
                                           // Admin only - update a user
                                           .antMatchers(HttpMethod.PUT, "/api/v1/users/**").hasRole("ADMIN")

                                           // ---------- WasteController ----------
                                           // Permit All - allow dustbins to post wastes and incorrect categorizations
                                           .antMatchers(HttpMethod.POST, "/api/v1/wastes/**").permitAll()

                                           // ---------- WebSocketController ----------
                                           // Permit All - websocket for dustbins
                                           .antMatchers("/api/v1/ws/**").permitAll()

                                           .anyRequest().authenticated()
                                  )
                .addFilterBefore(new JwtTokenAuthenticationFilter(tokenProvider), UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    UserDetailsService customUserDetailsService(UserRepository users) {

        return (username) -> users.findByUsername(username)
                                  .orElseThrow(() -> new UsernameNotFoundException("Username: " + username + " not found"));
    }

    @Bean
    AuthenticationManager customAuthenticationManager(UserDetailsService userDetailsService, PasswordEncoder encoder) {

        return authentication -> {
            String username = authentication.getPrincipal() + "";
            String password = authentication.getCredentials() + "";

            UserDetails user = userDetailsService.loadUserByUsername(username);

            if (!encoder.matches(password, user.getPassword())) {
                throw new BadCredentialsException("Bad credentials");
            }

            if (!user.isEnabled()) {
                throw new DisabledException("User account is not active");
            }

            return new UsernamePasswordAuthenticationToken(username, null, user.getAuthorities());
        };
    }
}
