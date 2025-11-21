package com.github.heroslender.hero_api.security;

import com.github.heroslender.hero_api.database.entity.UserEntity;
import com.github.heroslender.hero_api.database.entity.UserRole;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

public class MockUser {
    public static final UserEntity MOCK_USER = new UserEntity(1L, "test", "test@email.com", "", List.of(UserRole.USER));
    public static final RequestPostProcessor MOCK_USER_REQ = user(MOCK_USER);
}
