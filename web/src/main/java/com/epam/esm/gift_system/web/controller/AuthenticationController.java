package com.epam.esm.gift_system.web.controller;

import com.epam.esm.gift_system.service.UserService;
import com.epam.esm.gift_system.service.dto.AuthenticationRequestDto;
import com.epam.esm.gift_system.service.dto.AuthenticationResponseDto;
import com.epam.esm.gift_system.service.dto.UserDto;
import com.epam.esm.gift_system.service.exception.GiftSystemException;
import com.epam.esm.gift_system.service.security.JwtTokenProvider;
import com.epam.esm.gift_system.web.hateaos.HateaosBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

import static com.epam.esm.gift_system.service.exception.ErrorCode.AUTHENTICATION_REQUIREMENT;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {
    private final UserService userService;
    private final HateaosBuilder hateaosBuilder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthenticationController(UserService userService, HateaosBuilder hateaosBuilder, JwtTokenProvider jwtTokenProvider
            , AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.hateaosBuilder = hateaosBuilder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/signup")
    @PreAuthorize("hasAuthority('auth:sign_up') || !hasAuthority('auth:logout')")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto signUp(@RequestBody UserDto userDto) {
        UserDto created = userService.create(userDto);
        hateaosBuilder.setLinks(created);
        return created;
    }

    @PostMapping("/login")
    @PreAuthorize("hasRole('ANONYMOUS')")
    public AuthenticationResponseDto authenticate(@RequestBody AuthenticationRequestDto requestDto) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(requestDto.getUsername(), requestDto.getPassword()));
        UserDto userDto = userService.findByName(requestDto.getUsername());
        String token = jwtTokenProvider.createToken(userDto.getName(), userDto.getRole().name());
        return AuthenticationResponseDto.builder().username(requestDto.getUsername()).token(token).build();
    }

    @PostMapping("/logout")
    @PreAuthorize("hasAuthority('auth:logout')")
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (Objects.isNull(auth)) {
            throw new GiftSystemException(AUTHENTICATION_REQUIREMENT);
        }
        auth.setAuthenticated(false);
    }
}