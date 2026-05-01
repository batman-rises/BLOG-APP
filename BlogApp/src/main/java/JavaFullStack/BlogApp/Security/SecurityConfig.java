package JavaFullStack.BlogApp.Security;

import JavaFullStack.BlogApp.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.*;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    //private final UserRepository userRepository;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                // Step 1 — disable CSRF
                // CSRF protection is for browser sessions — we use JWT so we don't need it
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth
                        // Step 2 — public routes — no token needed
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers(
                                org.springframework.http.HttpMethod.GET,
                                "/api/posts/**",
                                "/api/tags/**"
                        ).permitAll()

                        // Step 3 — everything else needs a valid JWT
                        .anyRequest().authenticated()
                )

                // Step 4 — no sessions
                // Every request must carry its own JWT — server remembers nothing
                .sessionManagement(s ->
                        s.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Step 5 — plug in our JWT filter
                // runs BEFORE Spring's default username/password filter
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)

                .build();
    }

    // Step 6 — tell Spring how to load a user from DB
    // JwtAuthFilter calls this using the email extracted from token
//    @Bean
//    public UserDetailsService userDetailsService() {
//        return email -> userRepository.findByEmail(email)
//                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
//    }

    // Step 7 — BCrypt for password hashing
    // UserService will use this bean to encode passwords on register
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Step 8 — AuthenticationManager
    // UserService uses this to verify email + password on login
    @Bean
    public AuthenticationManager authManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }
}