package com.poli.internship.api.context;

import org.springframework.graphql.server.WebGraphQlInterceptor;
import org.springframework.graphql.server.WebGraphQlRequest;
import org.springframework.graphql.server.WebGraphQlResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Component
class RequestHeaderInterceptor implements WebGraphQlInterceptor {

    @Override
    public Mono<WebGraphQlResponse> intercept (WebGraphQlRequest request, Chain chain) {
        Map<String, Object> map = new HashMap<>();
        String auth = request.getHeaders().getFirst("Authorization");
        // Here you can extract info from the token
        //
        // 1) If the token is decryptable:
        // String userId = SomeCryptoService.verify(auth).get("id");
        //
        // 2) If the token can be used to retrieve data from elsewhere (like google):
        // String userId = SomeGoogleService.getData(auth).get("id");
        //
        // Then it can be added to the context
        // map.put("userId", userId);
        //
        // Now you will be able to retrieve it in the queries and mutations
        // Just add "GraphQLContext ctx" as an argument to the query resolver,
        // then retrieve the data:
        // String userId = ctx.get("userId");
        //
        // Instead of getting the context object, you can get only
        // the desired parameter by adding "@ContextValue String userId"
        // to the query resolver's arguments
        request.configureExecutionInput(((executionInput, builder) ->
                builder.graphQLContext(map).build()));

        return chain.next(request);
    }
}
