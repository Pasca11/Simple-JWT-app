package ru.amir.springcourse.config;

import com.auth0.jwt.exceptions.JWTVerificationException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.amir.springcourse.security.JWTUtil;
import ru.amir.springcourse.services.PersonDetailsService;

import java.io.IOException;

@Component
public class JWTFilter extends OncePerRequestFilter {
    private final JWTUtil jwtUtil;
    private final PersonDetailsService personDetailsService;

    public JWTFilter(JWTUtil jwtUtil, PersonDetailsService personDetailsService) {
        this.jwtUtil = jwtUtil;
        this.personDetailsService = personDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String str = request.getHeader("Authorization");

        if (str != null && !str.isBlank() && str.startsWith("Bearer ")) {
            String jwtToken = str.substring(7);

            if (jwtToken.isBlank())
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid jwt token");
            else {
                try {
                    String username = jwtUtil.validateAndGetClaim(jwtToken);
                    UserDetails details = personDetailsService.loadUserByUsername(username.substring(1,
                            username.length() - 1));

                    UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                            details,
                            details.getPassword(),
                            details.getAuthorities()
                    );
                    if (SecurityContextHolder.getContext().getAuthentication() == null) {
                        SecurityContextHolder.getContext().setAuthentication(token);
                    }
                } catch (JWTVerificationException e) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid jwt token");
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}
