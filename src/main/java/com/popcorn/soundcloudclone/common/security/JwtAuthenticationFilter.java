package com.popcorn.soundcloudclone.common.security;

import com.popcorn.soundcloudclone.features.auth.dto.JwtPayload;
import com.popcorn.soundcloudclone.features.auth.service.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
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

    private final JwtService jwtService; // service xác minh token
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String accessToken = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("access_token".equals(cookie.getName())) {
                    accessToken = cookie.getValue();
                    break;
                }
            }
        }

        if (accessToken == null) {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                accessToken = authHeader.substring(7);
            }
        }

        if (accessToken != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            logger.info("Token: " + accessToken);
            boolean valid = jwtService.verifyToken(accessToken);

            if (valid) {
                JwtPayload payload = jwtService.extractToken(accessToken);
                UserDetails userDetails = userDetailsService.loadUserByUsername(payload.getUsername());
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        userDetails, accessToken, userDetails.getAuthorities());
                // set detail de co meta data(ip, agent,..) ma logging sau nay neu can
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(auth);
                logger.info(String.format("Successfully authenticated username: %s, role: %s",
                        payload.getUsername(), payload.getAuthorities()));
            }
        }

        filterChain.doFilter(request, response);
    }

}
