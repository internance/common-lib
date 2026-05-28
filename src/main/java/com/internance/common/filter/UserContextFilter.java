package com.internance.common.filter;

import com.internance.common.context.UserContext;
import com.internance.common.context.UserContextHolder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class UserContextFilter extends OncePerRequestFilter {

    public static final String USER_ID_HEADER = "X-User-Id";

    private static final Logger log = LoggerFactory.getLogger(UserContextFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        UUID userId = parseUserId(request.getHeader(USER_ID_HEADER));
        if (userId != null) {
            UserContextHolder.set(new UserContext(userId));
        }
        try {
            filterChain.doFilter(request, response);
        } finally {
            UserContextHolder.clear();
        }
    }

    private UUID parseUserId(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        try {
            return UUID.fromString(raw);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid {} header format", USER_ID_HEADER);
            return null;
        }
    }
}
