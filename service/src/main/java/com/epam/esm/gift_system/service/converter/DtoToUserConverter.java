package com.epam.esm.gift_system.service.converter;

import com.epam.esm.gift_system.repository.model.Role;
import com.epam.esm.gift_system.repository.model.User;
import com.epam.esm.gift_system.service.dto.UserDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class DtoToUserConverter implements Converter<UserDto, User> {
    @Override
    public User convert(UserDto source) {
        return User.builder()
                .id(source.getId())
                .name(source.getName())
                .password(source.getPassword())
                .role(Role.valueOf(source.getRole().name()))
                .build();
    }
}