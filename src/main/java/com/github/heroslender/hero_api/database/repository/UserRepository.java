package com.github.heroslender.hero_api.database.repository;

import com.github.heroslender.hero_api.database.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    UserEntity findByEmail(String email);

    UserEntity findByUsername(String username);
}
