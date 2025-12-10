package com.github.heroslender.hero_api.controller;

import com.github.heroslender.hero_api.database.entity.UserEntity;
import com.github.heroslender.hero_api.dto.request.AuthenticationRequest;
import com.github.heroslender.hero_api.dto.response.LoginResponse;
import com.github.heroslender.hero_api.dto.request.RegistrationRequest;
import com.github.heroslender.hero_api.exceptions.UnauthorizedException;
import com.github.heroslender.hero_api.security.RequireAdminRole;
import com.github.heroslender.hero_api.security.TokenService;
import com.github.heroslender.hero_api.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("auth")
public class AuthController {
    private final TokenService tokenService;
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    Logger logger = LoggerFactory.getLogger(AuthController.class);

    public AuthController(TokenService tokenService, UserService userService, AuthenticationManager authenticationManager) {
        this.tokenService = tokenService;
        this.userService = userService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid AuthenticationRequest request) {
        try {
            UsernamePasswordAuthenticationToken usernamePassword = new UsernamePasswordAuthenticationToken(request.login(), request.password());
            Authentication auth = authenticationManager.authenticate(usernamePassword);

            var token = tokenService.generateToken((UserEntity) auth.getPrincipal());

            return ResponseEntity.ok(new LoginResponse(token));
        } catch (BadCredentialsException e) {
            throw new com.github.heroslender.hero_api.exceptions.BadCredentialsException();
        } catch (AuthenticationException e) {
            logger.warn("AuthEx", e);
            throw new UnauthorizedException();
        }
    }

    @PostMapping("/register")
    @RequireAdminRole
    public ResponseEntity<Void> register(@RequestBody @Valid RegistrationRequest request) {
        UserEntity user = userService.getUser(request.username());
        if (user != null) {
            return ResponseEntity.badRequest().build();
        }

        userService.createUser(request);

        return ResponseEntity.ok().build();
    }
}
