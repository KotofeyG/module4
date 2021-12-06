package service.converter;

import com.epam.esm.gift_system.repository.model.Role;
import com.epam.esm.gift_system.repository.model.User;
import com.epam.esm.gift_system.service.converter.UserToDtoConverter;
import com.epam.esm.gift_system.service.dto.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserToDtoConverterTest {
    private UserToDtoConverter userToDtoConverter;

    private User user;
    private UserDto userDto;

    @BeforeEach
    public void setUp() {
        userToDtoConverter = new UserToDtoConverter();
        user = User.builder().id(1L).name("User").role(Role.USER).build();
        userDto = UserDto.builder().id(1L).name("User").role(UserDto.Role.USER).build();
    }

    @Test
    void convert() {
        UserDto actual = userToDtoConverter.convert(user);
        assertEquals(userDto, actual);
    }
}