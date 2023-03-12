package com.poli.internship.api.context;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.poli.internship.domain.models.AuthTokenPayload;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;

@Service
public class JWTService {
    @Value("${auth.crypto-secret}")
    private String authCryptoSecret;

    public String createAuthorizationToken(AuthTokenPayload tokenPayload) {
        Algorithm algorithm = Algorithm.HMAC256(this.authCryptoSecret);
        return JWT.create()
                .withClaim("userId", tokenPayload.getUserId())
                .withClaim("email", tokenPayload.getEmail())
                .withExpiresAt(Instant.now().plusSeconds(tokenPayload.getExpiresIn()))
                .sign(algorithm);
    }
    public DecodedJWT decodeAuthorizationToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(this.authCryptoSecret);
            JWTVerifier verifier = JWT.require(algorithm)
                    .build();
            DecodedJWT decodedJWT = verifier.verify(token);
            return decodedJWT;
        } catch (Exception e){
            return null;
        }

    }
}
