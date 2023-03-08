package com.poli.internship.data.mapper;

import com.poli.internship.data.entity.UserEntity;
import com.poli.internship.domain.models.UserModel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserModel userEntityToModel(UserEntity entity);
}
