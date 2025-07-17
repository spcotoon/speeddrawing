package com.spcotoon.speeddrawing.common.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.Key;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends GenericFilter {

    private final JwtTokenProvider jwtTokenProvider;
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        String token = httpServletRequest.getHeader("Authorization");

        try {
            if (token != null) {
                if (!token.startsWith("Bearer")) {
                    throw new AuthenticationServiceException("Bearer 형식이 아닙니다.");
                }
                String jwtToken = token.substring(7);
                Key signingKey = jwtTokenProvider.getSigningKey();

                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(signingKey)
                        .build()
                        .parseClaimsJws(jwtToken)
                        .getBody();

                List<GrantedAuthority> authorities = new ArrayList<>();
                authorities.add(new SimpleGrantedAuthority((String) claims.get("role")));
                UserDetails userDetails = new User(claims.getSubject(), "", authorities);

                Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

            chain.doFilter(request, response);
        } catch (Exception e) {
            String clientIp = ((HttpServletRequest) request).getHeader("X-Forwarded-For");
            if (clientIp == null) {
                clientIp = request.getRemoteAddr();
            }

            String uri = httpServletRequest.getRequestURI();
            String ua = httpServletRequest.getHeader("User-Agent");
            String snippet = token != null && token.length() > 20
                    ? token.substring(0, 10) + "..." + token.substring(token.length() - 10)
                    : token;

            log.warn("JWT 검증 실패 | ip={} | uri={} | userAgent={} | tokenSnippet={} | error={}",
                    clientIp, uri, ua, snippet, e.getMessage());
            httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
            httpServletResponse.setContentType(String.valueOf(MediaType.APPLICATION_JSON));
            httpServletResponse.getWriter().write("invalid token");
            httpServletResponse.getWriter().flush();
        }
    }
}
