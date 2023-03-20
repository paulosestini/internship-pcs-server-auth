package com.poli.internship.domain.usecase;

import com.poli.internship.data.datasource.UserDataSource;
import static com.poli.internship.domain.models.UserModel.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GetUserUseCase {
    @Autowired
    private UserDataSource dataSource;

    public User exec(String id) {
        return this.dataSource.getUserById(id);
    }
}
