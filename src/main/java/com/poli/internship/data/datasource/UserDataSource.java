package com.poli.internship.data.datasource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poli.internship.api.error.CustomError;
import com.poli.internship.data.entity.UserEntity;
import com.poli.internship.data.mapper.UserMapper;
import com.poli.internship.data.messaging.PubsubOutboundGateway;
import com.poli.internship.data.repository.UserRepository;
import com.poli.internship.domain.models.UserModel;
import com.poli.internship.domain.models.UserType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.stereotype.Service;

@Service
public class UserDataSource {
    @Autowired
    public UserRepository repository;
    @Autowired
    private PubsubOutboundGateway messagingGateway;

    public UserModel.User getUserById(String id) {
        UserEntity userEntity = repository.findById(Long.parseLong(id));
        if (userEntity == null) {
            return null;
        }
        return UserMapper.INSTANCE.userEntityToModel(userEntity);
    }

    public UserModel.User getUserByEmail(String email) {
        UserEntity userEntity = repository.findByEmail(email);
        if (userEntity == null) {
            return null;
        }
        return UserMapper.INSTANCE.userEntityToModel(userEntity);
    }
    public UserModel.User createUser(String name, String email, UserType userType) {
        UserEntity userEntity = repository.save(new UserEntity(name, email, userType));

        try {
            String message = (new ObjectMapper()).writeValueAsString(userEntity);
            messagingGateway.sendToPubsub(message);
        } catch (Exception e) {
            repository.delete(userEntity);
            throw new CustomError("User creation failed.", ErrorType.INTERNAL_ERROR);
        }

        return UserMapper.INSTANCE.userEntityToModel(userEntity);
    }

}
