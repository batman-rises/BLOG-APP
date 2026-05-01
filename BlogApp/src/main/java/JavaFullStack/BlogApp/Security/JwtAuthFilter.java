package JavaFullStack.BlogApp.Security;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        // Step 1 — read the Authorization header
        // It looks like: "Bearer eyJhbGc..."
        String authHeader = request.getHeader("Authorization");

        // Step 2 — if no header or doesn't start with Bearer, skip
        // this request has no token — move on, don't authenticate
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        // Step 3 — strip "Bearer " (7 characters) → get raw token
        String token = authHeader.substring(7);

        // Step 4 — extract email from inside the token
        String email = jwtService.extractEmail(token);

        // Step 5 — if email found and user not already authenticated
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Step 6 — load the full User object from DB using email
            UserDetails user = userDetailsService.loadUserByUsername(email);

            // Step 7 — check token is valid and not expired
            if (jwtService.isValid(token, user)) {

                // Step 8 — create an auth object and put it in SecurityContext
                // This is what marks the user as "logged in" for this request
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                user, null, user.getAuthorities()
                        );
                auth.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        // Step 9 — pass request to next filter / controller
        chain.doFilter(request, response);
    }
}