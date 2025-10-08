package com.popcorn.soundcloudclone.config;

import com.popcorn.soundcloudclone.domain.dto.auth.IntrospectResponse;
import com.popcorn.soundcloudclone.service.AuthService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@AllArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final AuthService authService; // service xác minh token
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            String accessToken = authHeader.replace("Bearer ", "");
            logger.info("Token: " + accessToken);
            IntrospectResponse authenticated = authService.introspect(accessToken);
            logger.info("Verify message: " + authenticated.getMessage());

            if (authenticated.isValid()) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(authenticated.getUsername());
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                // set detail de co meta data(ip, agent,..) ma logging sau nay neu can
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(auth);
                logger.info(String.format("Successfully authenticated username: %s, role: %s", authenticated.getUsername(), authenticated.getAuthorities()));
            }
        }

        filterChain.doFilter(request, response);
    }

}
