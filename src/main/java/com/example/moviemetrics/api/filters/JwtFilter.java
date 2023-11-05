package com.example.moviemetrics.api.filters;

import com.example.moviemetrics.api.util.JWTProvider;
import io.jsonwebtoken.Claims;
import io.micrometer.common.lang.NonNull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {

        if (!request.getRequestURI().startsWith("/auth/")) {
            String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            if(authHeader == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Access denied");
                return;
            }

            String token = authHeader.substring(7);
            try {
                Claims claims = JWTProvider.parseToken(token);
                request.setAttribute("id", claims.get("id"));
                request.setAttribute("email", claims.getSubject());
            } catch (Exception ex) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Access denied");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}
