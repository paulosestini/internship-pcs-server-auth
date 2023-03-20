package com.poli.internship.data.mapper;

import com.poli.internship.data.entity.UserEntity;
import static com.poli.internship.domain.models.UserModel.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    User userEntityToModel(UserEntity entity);
}
