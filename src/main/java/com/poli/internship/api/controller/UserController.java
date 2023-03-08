package com.poli.internship.api.controller;

import com.poli.internship.domain.models.UserModel;
import com.poli.internship.domain.usecase.CreateUserUseCase;
import com.poli.internship.domain.usecase.GetUserUseCase;
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
    public CreateUserUseCase createUserUseCase;

    @QueryMapping
    public UserModel getUser(@Argument Map input) {
        Map data = (Map) input.get("input");
        return this.getUserUseCase.exec((String) data.get("id"));
    }

    @MutationMapping
    public UserModel createUser(@Argument Map input) {
        Map data = (Map) input.get("input");
        return this.createUserUseCase.exec((String) data.get("name"), (String) data.get("password"));
    }
}
