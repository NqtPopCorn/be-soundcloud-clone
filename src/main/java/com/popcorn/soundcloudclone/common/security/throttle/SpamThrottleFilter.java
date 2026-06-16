package com.popcorn.soundcloudclone.common.security.throttle;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class SpamThrottleFilter extends OncePerRequestFilter {
    private static final String TOO_MANY_REQUESTS_BODY = """
            {"statusCode":429,"message":"Too many requests"}
            """;

    private final List<ThrottleRule> rules;
    private final ConcurrentMap<String, Window> windows = new ConcurrentHashMap<>();
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public SpamThrottleFilter() {
        this(defaultRules());
    }

    private SpamThrottleFilter(List<ThrottleRule> rules) {
        this.rules = rules;
    }

    public static SpamThrottleFilter createDefault() {
        return new SpamThrottleFilter(defaultRules());
    }

    private static List<ThrottleRule> defaultRules() {
        return List.of(
                new ThrottleRule("auth-login", HttpMethod.POST, "/auth/login", 5, Duration.ofMinutes(1)),
                new ThrottleRule("auth-register", HttpMethod.POST, "/auth/register", 5, Duration.ofMinutes(1)),
                new ThrottleRule("auth-refresh", HttpMethod.POST, "/auth/refresh", 20, Duration.ofMinutes(1)),
                new ThrottleRule("track-upload", HttpMethod.POST, "/tracks", 10, Duration.ofMinutes(1)),
                new ThrottleRule("track-update", HttpMethod.PATCH, "/tracks/*", 20, Duration.ofMinutes(1)),
                new ThrottleRule("track-delete", HttpMethod.DELETE, "/tracks/*", 20, Duration.ofMinutes(1)),
                new ThrottleRule("track-play", HttpMethod.POST, "/tracks/*/play", 30, Duration.ofMinutes(1)),
                new ThrottleRule("comment-create", HttpMethod.POST, "/comments/tracks/*", 10, Duration.ofMinutes(1)),
                new ThrottleRule("comment-delete", HttpMethod.DELETE, "/comments/*", 20, Duration.ofMinutes(1)),
                new ThrottleRule("track-like", HttpMethod.POST, "/likes/tracks", 30, Duration.ofMinutes(1)),
                new ThrottleRule("user-follow", HttpMethod.POST, "/user/follows/*", 20, Duration.ofMinutes(1)),
                new ThrottleRule("user-unfollow", HttpMethod.DELETE, "/user/follows/*", 20, Duration.ofMinutes(1)),
                new ThrottleRule("avatar-upload", HttpMethod.POST, "/user/me/avatar", 10, Duration.ofMinutes(1)),
                new ThrottleRule("background-upload", HttpMethod.POST, "/user/me/background", 10, Duration.ofMinutes(1)),
                new ThrottleRule("profile-update", HttpMethod.PATCH, "/user/me", 10, Duration.ofMinutes(1)),
                new ThrottleRule("playlist-create", HttpMethod.POST, "/playlists", 10, Duration.ofMinutes(1)),
                new ThrottleRule("playlist-update", HttpMethod.PATCH, "/playlists/*", 20, Duration.ofMinutes(1)),
                new ThrottleRule("playlist-add-tracks", HttpMethod.POST, "/playlists/*/tracks", 20, Duration.ofMinutes(1)),
                new ThrottleRule("playlist-update-tracks", HttpMethod.PUT, "/playlists/*/tracks", 20, Duration.ofMinutes(1)),
                new ThrottleRule("playlist-delete", HttpMethod.DELETE, "/playlists/*", 20, Duration.ofMinutes(1)),
                new ThrottleRule("album-create", HttpMethod.POST, "/albums", 10, Duration.ofMinutes(1)),
                new ThrottleRule("album-update", HttpMethod.PATCH, "/albums/*", 20, Duration.ofMinutes(1)),
                new ThrottleRule("album-add-tracks", HttpMethod.POST, "/albums/*/tracks", 20, Duration.ofMinutes(1)),
                new ThrottleRule("album-update-tracks", HttpMethod.PUT, "/albums/*/tracks", 20, Duration.ofMinutes(1)),
                new ThrottleRule("album-delete", HttpMethod.DELETE, "/albums/*", 20, Duration.ofMinutes(1)));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        ThrottleRule rule = findRule(request);
        if (rule == null || tryConsume(rule, clientKey(request, rule), Instant.now())) {
            filterChain.doFilter(request, response);
            return;
        }

        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setHeader(HttpHeaders.RETRY_AFTER, String.valueOf(rule.window().toSeconds()));
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(TOO_MANY_REQUESTS_BODY);
    }

    private ThrottleRule findRule(HttpServletRequest request) {
        String method = request.getMethod();
        String path = request.getRequestURI();
        String contextPath = request.getContextPath();
        if (contextPath != null && !contextPath.isBlank() && path.startsWith(contextPath)) {
            path = path.substring(contextPath.length());
        }

        for (ThrottleRule rule : rules) {
            if (rule.method().matches(method) && pathMatcher.match(rule.pathPattern(), path)) {
                return rule;
            }
        }
        return null;
    }

    private boolean tryConsume(ThrottleRule rule, String key, Instant now) {
        AtomicBoolean allowed = new AtomicBoolean(true);
        windows.compute(key, (ignored, window) -> {
            if (window == null || !now.isBefore(window.resetAt())) {
                return new Window(1, now.plus(rule.window()));
            }
            int nextCount = window.count() + 1;
            allowed.set(nextCount <= rule.maxRequests());
            return new Window(nextCount, window.resetAt());
        });
        return allowed.get();
    }

    private String clientKey(HttpServletRequest request, ThrottleRule rule) {
        return rule.name() + ":" + clientIdentity(request);
    }

    private String clientIdentity(HttpServletRequest request) {
        String token = bearerToken(request);
        if (token == null) {
            token = cookieValue(request, "access_token");
        }
        if (token != null && !token.isBlank()) {
            return "token:" + Integer.toHexString(token.hashCode());
        }

        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return "ip:" + forwardedFor.split(",")[0].trim();
        }
        return "ip:" + request.getRemoteAddr();
    }

    private String bearerToken(HttpServletRequest request) {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorization == null) {
            return null;
        }
        String prefix = "bearer ";
        if (!authorization.toLowerCase(Locale.ROOT).startsWith(prefix)) {
            return null;
        }
        return authorization.substring(prefix.length()).trim();
    }

    private String cookieValue(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (name.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    private record ThrottleRule(String name, HttpMethod method, String pathPattern, int maxRequests, Duration window) {
    }

    private record Window(int count, Instant resetAt) {
    }
}
