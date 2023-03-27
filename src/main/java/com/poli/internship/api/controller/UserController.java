package com.poli.internship.api.controller;

import com.poli.internship.api.auth.GraphQLAuthorization;
import static com.poli.internship.domain.models.LoginInputModel.LoginInput;
import com.poli.internship.domain.models.LoginModel;
import static com.poli.internship.domain.models.UserModel.User;
import com.poli.internship.domain.models.UserType;
import com.poli.internship.domain.usecase.GetUserUseCase;
import com.poli.internship.domain.usecase.LoginUseCase;
import graphql.GraphQLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
public class UserController {
    @Autowired
    public GetUserUseCase getUserUseCase;
    @Autowired
    public LoginUseCase loginUseCase;

    @QueryMapping
    public User getUser(GraphQLContext ctx) {
        GraphQLAuthorization.checkAuthorization(ctx);
        return this.getUserUseCase.exec(ctx.get("userId"));
    }


    @MutationMapping
    public LoginModel login(@Argument Map input) {
        Map data = (Map) input.get("input");
        return this.loginUseCase.exec(
                new LoginInput(
                    (String) data.get("code"),
                    UserType.valueOf((String) data.get("userType")),
                    (String) data.get("redirectUri")
                )
        );
    }
}
