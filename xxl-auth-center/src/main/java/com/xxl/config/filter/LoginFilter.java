package com.xxl.config.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.stream.Collectors;

public class LoginFilter extends AbstractAuthenticationProcessingFilter {

    private static final Logger logger = LoggerFactory.getLogger(LoginFilter.class);

    private final ObjectMapper mapper;

    /**
     * JWT header name.
     */
    @Value("${auth.jwt.header.name}")
    private String headerName;

    /**
     * JWT prefix.
     */
    @Value("${auth.jwt.header.prefix}")
    private String headerPrefix;

    /**
     * expiration.
     */
    @Value("${auth.jwt.expiration}")
    private int expiration;

    /**
     * private key path.
     */
    @Value("${auth.jwt.cert.private}")
    private String privateKeyPath;

    /**
     * algorithm type.
     */
    @Value("${auth.jwt.algorithm.type}")
    private String algorithmType;

    public LoginFilter(String loginUrl, AuthenticationManager authManager) {
        super(new AntPathRequestMatcher(loginUrl, "POST"));
        logger.info("LoginFilter, loginUrl: {}", loginUrl);
        setAuthenticationManager(authManager);
        this.mapper = new ObjectMapper();
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse rsp)
            throws AuthenticationException, IOException, ServletException {
        logger.info("attemptAuthentication.");
        //覆写尝试进行登录认证的逻辑
        try {
            User u = mapper.readValue(req.getInputStream(), User.class);

            return getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken(
                    u.getUsername(), u.getPassword(), Collections.emptyList()));
        } catch (JsonProcessingException e) {
            logger.warn("jwt username password auth failed", e);
            throw new BadCredentialsException("bad json input", e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse rsp, FilterChain chain,
                                            Authentication auth) throws IOException, ServletException {
        PrivateKey privateKey;
        try (InputStream inputStream = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream(privateKeyPath)) {
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(IOUtils.toByteArray(inputStream));
            privateKey = KeyFactory.getInstance(algorithmType).generatePrivate(spec);
        } catch (Exception e) {
            logger.error("exception generate private key: {}", e.getMessage());
            throw new RuntimeException(e);
        }

        String token = Jwts.builder()
                .setSubject(auth.getName())
                .claim("roles", auth.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .setExpiration(Date.from(Instant.now().plusSeconds(expiration)))
                .signWith(SignatureAlgorithm.RS256, privateKey)
                .compact();
        rsp.addHeader(headerName, headerPrefix + " " + token);
    }

    private static class User {

        private String username;
        private String password;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
