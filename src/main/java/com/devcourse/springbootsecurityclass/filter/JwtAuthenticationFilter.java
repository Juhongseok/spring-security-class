package com.devcourse.springbootsecurityclass.filter;

import com.devcourse.springbootsecurityclass.jwt.Jwt;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

@Component
public class JwtAuthenticationFilter extends GenericFilter {

    private final Jwt jwt;

    public JwtAuthenticationFilter(Jwt jwt) {
        this.jwt = jwt;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        String jwtToken = exposeJwtToken(request);
        if (jwtToken != null) {
            Map<String, Object> claims = getClaims(jwtToken);
            UsernamePasswordAuthenticationToken authenticationToken = createToken(claims);
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }

        chain.doFilter(request, response);
    }

    private String exposeJwtToken(HttpServletRequest request) {
        return request.getHeader("token");
    }

    private Map<String, Object> getClaims(String jwtToken) {
        return jwt.verify(jwtToken).asMap();
    }

    private UsernamePasswordAuthenticationToken createToken(Map<String, Object> claims) {
        String username = (String) claims.get("username");
        String[] roles = (String[]) claims.get("roles");

        return new UsernamePasswordAuthenticationToken(username, null, convertAuthority(roles));
    }

    private List<? extends GrantedAuthority> convertAuthority(String[] roles) {
        return Optional.ofNullable(roles).stream()
                .flatMap(Arrays::stream)
                .map(SimpleGrantedAuthority::new)
                .toList();
    }

}
