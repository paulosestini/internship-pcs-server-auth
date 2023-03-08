package com.poli.internship.data.datasource;

import com.poli.internship.data.entity.UserEntity;
import com.poli.internship.data.mapper.UserMapper;
import com.poli.internship.data.repository.UserRepository;
import com.poli.internship.domain.models.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserDataSource {
    @Autowired
    public UserRepository repository;

    public UserModel getUser(String id) {
        UserEntity userEntity = repository.findById(Long.parseLong(id));
        return UserMapper.INSTANCE.userEntityToModel(userEntity);
    }

    public UserModel createUser(String name, String password) {
        UserEntity userEntity = repository.save(new UserEntity(name, password));
        return UserMapper.INSTANCE.userEntityToModel(userEntity);
    }

}
