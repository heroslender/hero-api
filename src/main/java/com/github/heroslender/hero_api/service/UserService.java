package com.github.heroslender.hero_api.service;

import com.github.heroslender.hero_api.dto.RegistrationDTO;
import com.github.heroslender.hero_api.database.entity.UserRole;
import com.github.heroslender.hero_api.database.entity.UserEntity;
import com.github.heroslender.hero_api.database.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserEntity createUser(RegistrationDTO registrationDto) {
        UserEntity user = new UserEntity();
        user.setUsername(registrationDto.username());
        user.setEmail(registrationDto.email());
        user.setPassword(new BCryptPasswordEncoder().encode(registrationDto.password()));
        user.setRoles(List.of(UserRole.USER));

        return userRepository.save(user);
    }

    public UserEntity getUser(String username) {
        return userRepository.findByUsername(username);
    }
}
