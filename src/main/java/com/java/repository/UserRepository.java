package com.java.repository;

import com.java.repository.entity.UserEntity;

public interface UserRepository {
    UserEntity findByEmail(String email);
    int createUser(UserEntity user);
    UserEntity findById(Integer id);
    boolean existsByEmail(String email);
}