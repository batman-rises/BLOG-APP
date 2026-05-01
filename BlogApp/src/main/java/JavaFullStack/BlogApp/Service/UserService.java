package JavaFullStack.BlogApp.Service;

import JavaFullStack.BlogApp.DTO.Request.LoginRequest;
import JavaFullStack.BlogApp.DTO.Request.RegisterRequest;
import JavaFullStack.BlogApp.DTO.Response.AuthResponse;
import JavaFullStack.BlogApp.Entity.User;
import JavaFullStack.BlogApp.Enums.Role;
import JavaFullStack.BlogApp.Repository.UserRepository;
import JavaFullStack.BlogApp.Security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authManager;

    public AuthResponse register(RegisterRequest request){
        if(userRepository.existsByEmail(request.getEmail()))
            throw new RuntimeException("Email already in use");

        if(userRepository.existsByUsername(request.getUsername()))
            throw new RuntimeException("username already in use");

        // Build the User entity from the request DTO
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();

        // Save to DB
        userRepository.save(user);

        // generate real JWT token
        String token = jwtService.generateToken(user);
        return new AuthResponse(token, user.getUsername(), user.getEmail());

    }
    public AuthResponse login(LoginRequest request){
        // authManager checks email + password against DB automatically
        // throws BadCredentialsException if wrong
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        // Find user by email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));



        String token = jwtService.generateToken(user);
        return new AuthResponse(token, user.getUsername(), user.getEmail());
    }
}
