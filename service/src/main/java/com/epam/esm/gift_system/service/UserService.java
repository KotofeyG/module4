package com.epam.esm.gift_system.service;

import com.epam.esm.gift_system.repository.model.User;
import com.epam.esm.gift_system.service.dto.ResponseOrderDto;
import com.epam.esm.gift_system.service.dto.UserDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService extends BaseService<UserDto> {
    User findUserById(Long id);

    UserDto findByName(String name);

    Page<ResponseOrderDto> findUserOrderList(Long id, Pageable pageable);
}