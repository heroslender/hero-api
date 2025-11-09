package com.github.heroslender.hero_api.controller;

import com.github.heroslender.hero_api.dto.AuthenticationDTO;
import com.github.heroslender.hero_api.dto.LoginResponseDTO;
import com.github.heroslender.hero_api.dto.RegistrationDTO;
import com.github.heroslender.hero_api.entity.User;
import com.github.heroslender.hero_api.security.RequireAdmin;
import com.github.heroslender.hero_api.security.TokenService;
import com.github.heroslender.hero_api.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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

    public AuthController(TokenService tokenService, UserService userService, AuthenticationManager authenticationManager) {
        this.tokenService = tokenService;
        this.userService = userService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid AuthenticationDTO data) {
        UsernamePasswordAuthenticationToken usernamePassword = new UsernamePasswordAuthenticationToken(data.login(), data.password());
        Authentication auth = authenticationManager.authenticate(usernamePassword);

        var token = tokenService.generateToken((User) auth.getPrincipal());

        return ResponseEntity.ok(new LoginResponseDTO(token));
    }

    @PostMapping("/register")
    @RequireAdmin
    public ResponseEntity<Void> register(@RequestBody @Valid RegistrationDTO data) {
        User user = userService.getUser(data.username());
        if (user != null) {
            return ResponseEntity.badRequest().build();
        }

        userService.createUser(data);

        return ResponseEntity.ok().build();
    }
}
