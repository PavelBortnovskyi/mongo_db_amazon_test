package com.neo.mongocachetest.exceptions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neo.mongocachetest.exceptions.authError.JwtAuthenticationException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Additional filter to handle exception from JwtAuthFilter
 */
@Log4j2
@Component
@RequiredArgsConstructor
public class FilterExceptionHandler extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (JwtAuthenticationException ex) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            response.getWriter()
                    .write(this.objectMapper
                            .writeValueAsString(new ErrorInfo(UrlUtils.buildFullRequestUrl(request), "JWT token empty or invalid!")));
            log.error("JWT token empty or invalid!");
        } catch (RuntimeException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String requestMethod = request.getMethod();

        AntPathRequestMatcher[] matchers = {
                new AntPathRequestMatcher("/swagger-ui/**", requestMethod),
                new AntPathRequestMatcher("/swagger-resources/**", requestMethod),
                new AntPathRequestMatcher("/webjars/**", requestMethod),
                new AntPathRequestMatcher("/v2/api-docs", requestMethod)
        };

        for (AntPathRequestMatcher matcher : matchers) {
            if (matcher.matches(request)) {
                return true;
            }
        }
        return false;
    }
}
