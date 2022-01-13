package com.uc3m.delphi.rest.filter;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.uc3m.delphi.database.model.FilterJwtConfig;
import com.uc3m.delphi.database.model.User;
import com.uc3m.delphi.database.repository.FilterJwtConfigRepository;
import com.uc3m.delphi.rest.request.RequestUser;
import com.uc3m.delphi.util.JwtUtil;
import io.jsonwebtoken.*;
import org.hibernate.annotations.Filter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;


@Filter(name = "loginFilter")
@Component
@Order(1)
public class JwtFilter extends OncePerRequestFilter {

    private final List<FilterJwtConfig> urlPatterns;
    private final AntPathMatcher pathMatcher;

    private final JwtUtil jwtUtil;

    @Autowired
    private RequestUser requestUser;

    public JwtFilter(JwtUtil jwtUtil, FilterJwtConfigRepository filterJwtConfigRepository) {
        this.pathMatcher = new AntPathMatcher();
        this.jwtUtil = jwtUtil;
        this.urlPatterns = filterJwtConfigRepository.findAll();
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if (request.getMethod().equalsIgnoreCase("OPTIONS")) {
            return true;
        }
        return urlPatterns.stream()
                .anyMatch(p -> {
                    if (p.isExcludes()) {
                        return this.pathMatcher.match(p.getUrlPattern(), request.getRequestURI());
                    }
                    return false;
                });
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String jwt = request.getHeader("Authorization");
        if (jwt == null || jwt.equalsIgnoreCase("")) {
            this.unauthorizeCall(response, "INVALID_JWT");
            return;
        }
        jwt = jwt.replace("Bearer ", "");
        try {
            final ObjectMapper mapper = new ObjectMapper();

            Claims claims = this.jwtUtil.validate(jwt);
            User user = mapper.convertValue(claims.get("user"), User.class);
            requestUser.setUser(user);
            if (user == null) {
                this.unauthorizeCall(response, "NULL_USER");
            }

            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user.getEmail(), null,
                    null);
            SecurityContextHolder.getContext().setAuthentication(auth);
            chain.doFilter(request, response);
        } catch (UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {
            e.printStackTrace();
            this.unauthorizeCall(response, "MALFORMED_JWT");
        } catch (ExpiredJwtException e) {
            e.printStackTrace();
            this.unauthorizeCall(response, "EXPIRED_JWT");
        }
    }

    private void unauthorizeCall(HttpServletResponse response, String msg) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, msg);
        SecurityContextHolder.clearContext();
    }
}