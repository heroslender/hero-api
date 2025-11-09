package com.github.heroslender.hero_api.service;

import com.github.heroslender.hero_api.dto.RegistrationDTO;
import com.github.heroslender.hero_api.entity.Role;
import com.github.heroslender.hero_api.entity.User;
import com.github.heroslender.hero_api.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(RegistrationDTO registrationDto) {
        User user = new User();
        user.setUsername(registrationDto.username());
        user.setEmail(registrationDto.email());
        user.setPassword(new BCryptPasswordEncoder().encode(registrationDto.password()));
        user.setRoles(List.of(Role.USER));

        return userRepository.save(user);
    }

    public User getUser(String username) {
        return userRepository.findByUsername(username);
    }
}
