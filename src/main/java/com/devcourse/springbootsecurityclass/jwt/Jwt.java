package com.devcourse.springbootsecurityclass.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

@Component
public class Jwt {

    private final String issuer;
    private final String clientSecret;
    private final int expirySeconds;
    private final Algorithm algorithm;
    private final JWTVerifier jwtVerifier;

    public Jwt(JwtProperties jwtProperties) {
        this.issuer = jwtProperties.issuer();
        this.clientSecret = jwtProperties.clientSecret();
        this.expirySeconds = jwtProperties.expirySeconds();
        this.algorithm = Algorithm.HMAC512(clientSecret);
        this.jwtVerifier = JWT.require(algorithm)
                .withIssuer(issuer)
                .build();
    }

    public String sign(Claims claims) {
        Date now = new Date();
        JWTCreator.Builder builder = JWT.create();
        builder.withIssuer(issuer);
        builder.withIssuedAt(now);
        builder.withExpiresAt(new Date(now.getTime() + expirySeconds * 1000L));
        builder.withClaim("username", claims.username);
        builder.withArrayClaim("roles", claims.roles);

        return builder.sign(algorithm);
    }

    public Claims verify(String token) {
        return new Claims(jwtVerifier.verify(token));
    }

    public static class Claims {
        private String username;
        private String[] roles;
        private Date iat;
        private Date exp;

        private Claims() {
        }

        public Claims(DecodedJWT decodedJWT) {
            this.username = decodedJWT.getClaim("username").asString();
            this.roles = decodedJWT.getClaim("roles").asArray(String.class);
            this.iat = decodedJWT.getIssuedAt();
            this.exp = decodedJWT.getExpiresAt();
        }

        public static Claims from(String username, String[] roles) {
            Claims claims = new Claims();
            claims.username = username;
            claims.roles = roles;

            return claims;
        }

        public Map<String, Object> asMap() {
            return Map.of(
                    "username", username,
                    "roles", roles,
                    "iat", iat.getTime(),
                    "exp", exp.getTime()
            );
        }
    }
}
