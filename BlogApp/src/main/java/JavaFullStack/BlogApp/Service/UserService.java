package JavaFullStack.BlogApp.Service;

import JavaFullStack.BlogApp.DTO.Request.LoginRequest;
import JavaFullStack.BlogApp.DTO.Request.RegisterRequest;
import JavaFullStack.BlogApp.DTO.Response.AuthResponse;
import JavaFullStack.BlogApp.Entity.User;
import JavaFullStack.BlogApp.Enums.Role;
import JavaFullStack.BlogApp.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public AuthResponse register(RegisterRequest request){
        if(userRepository.existsByEmail(request.getEmail()))
            throw new RuntimeException("Email already in use");

        if(userRepository.existsByUsername(request.getUsername()))
            throw new RuntimeException("username already in use");

        // Build the User entity from the request DTO
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(request.getPassword()) // plain text for now, security comes later
                .role(Role.USER)
                .build();

        // Save to DB
        userRepository.save(user);

        // Return response DTO — token is empty string for now, will add security tomorrow
        return new AuthResponse("no-token-yet", user.getUsername(), user.getEmail());

    }
    public AuthResponse login(LoginRequest request){
        // Find user by email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Plain text password check for now — security replaces this later
        if (!user.getPassword().equals(request.getPassword()))
            throw new RuntimeException("Wrong password");

        return new AuthResponse("no-token-yet", user.getUsername(), user.getEmail());
    }
}
