package com.poli.internship.api.auth;

import com.poli.internship.api.error.CustomError;
import graphql.GraphQLContext;
import org.springframework.graphql.execution.ErrorType;

public class GraphQLAuthorization {
    public static void checkAuthorization(GraphQLContext ctx) {
        String userId = ctx.get("userId");
        if (userId == null) {
            throw new CustomError("Missing or invalid authorization token.", ErrorType.UNAUTHORIZED);
        }
    }
}
