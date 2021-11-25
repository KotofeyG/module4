package com.epam.esm.gift_system.service.security;

import com.epam.esm.gift_system.repository.dao.UserRepository;
import com.epam.esm.gift_system.repository.model.User;
import com.epam.esm.gift_system.service.exception.GiftSystemException;
import com.epam.esm.gift_system.service.validator.EntityValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import static com.epam.esm.gift_system.service.exception.ErrorCode.NON_EXISTENT_ENTITY;
import static com.epam.esm.gift_system.service.exception.ErrorCode.USER_INVALID_NAME;
import static com.epam.esm.gift_system.service.validator.EntityValidator.ValidationType.STRONG;

@Component
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;
    private final EntityValidator validator;

    @Autowired
    public UserDetailsServiceImpl(UserRepository userRepository, EntityValidator validator) {
        this.userRepository = userRepository;
        this.validator = validator;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (!validator.isNameValid(username, STRONG)) {
            throw new GiftSystemException(USER_INVALID_NAME);
        }
        User user = userRepository.findByName(username).orElseThrow(() -> new GiftSystemException(NON_EXISTENT_ENTITY));
        return SecurityUserDetailsBuilder.create(user);
    }
}