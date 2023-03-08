package com.poli.internship.domain.usecase;

import com.poli.internship.data.datasource.UserDataSource;
import com.poli.internship.domain.models.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CreateUserUseCase {
    @Autowired
    private UserDataSource dataSource;

    public UserModel exec(String name, String password) {
        return this.dataSource.createUser(name, password);
    }
}
