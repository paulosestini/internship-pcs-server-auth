package com.poli.internship.data.repository;

import com.poli.internship.data.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;


public interface UserRepository extends CrudRepository<UserEntity, Long> {
    UserEntity findById(long id);

}
