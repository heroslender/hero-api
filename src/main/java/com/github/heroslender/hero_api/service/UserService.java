package com.github.heroslender.hero_api.service;

import com.github.heroslender.hero_api.database.entity.UserEntity;
import com.github.heroslender.hero_api.database.repository.UserRepository;
import com.github.heroslender.hero_api.dto.request.RegistrationRequest;
import com.github.heroslender.hero_api.exceptions.ResourceNotFoundException;
import com.github.heroslender.hero_api.model.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public UserEntity createUser(RegistrationRequest request) {
        UserEntity user = new UserEntity();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPassword(new BCryptPasswordEncoder().encode(request.password()));
        user.setRoles(List.of(UserRole.USER));

        return userRepository.save(user);
    }

    public UserEntity getUser(String username) {
        return userRepository.findByUsername(username);
    }

    public UserEntity getUser(long id) {
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User with id '" + id + "' does not exist!"));
    }
}
