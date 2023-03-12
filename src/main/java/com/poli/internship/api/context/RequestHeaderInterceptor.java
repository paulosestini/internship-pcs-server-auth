package com.poli.internship.api.context;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.poli.internship.api.error.CustomError;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.graphql.server.WebGraphQlInterceptor;
import org.springframework.graphql.server.WebGraphQlRequest;
import org.springframework.graphql.server.WebGraphQlResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Component
public class RequestHeaderInterceptor implements WebGraphQlInterceptor {
    @Value("${auth.crypto-secret}")
    private String authCryptoSecret;
    @Override
    public Mono<WebGraphQlResponse> intercept (WebGraphQlRequest request, Chain chain) {
        Map<String, Object> context = new HashMap<>();
        String auth = request.getHeaders().getFirst("Authorization");

        DecodedJWT decodedToken = null;
        if (auth != null) {
            decodedToken = this.decodeAuthorizationToken(auth);
        }

        if (decodedToken != null) {
            context.put("userId", decodedToken.getClaim("userId").asString());
            context.put("email", decodedToken.getClaim("email").asString());
        }

        request.configureExecutionInput(((executionInput, builder) ->
                builder.graphQLContext(context).build()));

        return chain.next(request);
    }

    private DecodedJWT decodeAuthorizationToken(String token) {
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
