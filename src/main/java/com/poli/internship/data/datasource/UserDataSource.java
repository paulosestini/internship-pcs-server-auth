package com.poli.internship.data.datasource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poli.internship.api.error.CustomError;
import com.poli.internship.data.entity.UserEntity;
import com.poli.internship.data.mapper.UserMapper;
import com.poli.internship.data.messaging.PubsubOutboundGateway;
import com.poli.internship.data.repository.UserRepository;

import static com.poli.internship.InternshipApplication.LOGGER;
import static com.poli.internship.domain.models.UserModel.User;
import static com.poli.internship.domain.models.CreateUserInputModel.CreateUserInput;
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

    public User getUserById(String id) {
        UserEntity userEntity = repository.findById(Long.parseLong(id));
        if (userEntity == null) {
            return null;
        }
        return UserMapper.INSTANCE.userEntityToModel(userEntity);
    }

    public User getUserByEmail(String email) {
        UserEntity userEntity = repository.findByEmail(email);
        if (userEntity == null) {
            return null;
        }
        return UserMapper.INSTANCE.userEntityToModel(userEntity);
    }
    public User createUser(CreateUserInput input) {
        UserEntity userEntity = repository.save(
                new UserEntity(
                        input.name(),
                        input.email(),
                        input.userType(),
                        input.profilePictureUrl()
                )
        );

        try {
            String message = (new ObjectMapper()).writeValueAsString(userEntity);
            messagingGateway.sendToPubsub(message);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            repository.delete(userEntity);
            throw new CustomError("User creation failed.", ErrorType.INTERNAL_ERROR);
        }

        return UserMapper.INSTANCE.userEntityToModel(userEntity);
    }

}
