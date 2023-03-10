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

    public UserModel getUserById(String id) {
        UserEntity userEntity = repository.findById(Long.parseLong(id));
        if (userEntity == null) {
            return null;
        }
        return UserMapper.INSTANCE.userEntityToModel(userEntity);
    }

    public UserModel getUserByEmail(String email) {
        UserEntity userEntity = repository.findByEmail(email);
        if (userEntity == null) {
            return null;
        }
        return UserMapper.INSTANCE.userEntityToModel(userEntity);
    }
    public UserModel createUser(String name, String email) {
        UserEntity userEntity = repository.save(new UserEntity(name, email));
        return UserMapper.INSTANCE.userEntityToModel(userEntity);
    }

}
