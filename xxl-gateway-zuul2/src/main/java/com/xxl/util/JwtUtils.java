package com.xxl.util;

import java.io.InputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.http.util.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static io.jsonwebtoken.lang.Classes.getResourceAsStream;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);
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

    @Value("${auth.jwt.cert.public}")
    private String publicKeyPath;

    public String createJwtToken(Map<String, Object> claims) {

        JwtBuilder builder = Jwts.builder()
                .setClaims(claims)
                .setExpiration(Date.from(Instant.now().plusSeconds(expiration)))
                .signWith(SignatureAlgorithm.RS256, getPrivateKey());
        return builder.compact();
    }

    public Claims parseJwtToken(String token) {

        return Jwts.parser()
            .setSigningKey(getPublicKey())
            .parseClaimsJws(token)
            .getBody();
    }

    private PublicKey getPublicKey() {
        try (InputStream inputStream = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream(publicKeyPath)) {
            X509EncodedKeySpec spec = new X509EncodedKeySpec(IOUtils.toByteArray(inputStream));
            return KeyFactory.getInstance(algorithmType).generatePublic(spec);

        } catch (Exception e) {
            logger.error("exception generate public key: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private PrivateKey getPrivateKey() {
        try (InputStream inputStream = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream(privateKeyPath)) {
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(IOUtils.toByteArray(inputStream));
            return KeyFactory.getInstance(algorithmType).generatePrivate(spec);
        } catch (Exception e) {
            logger.error("exception generate private key: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }


}
