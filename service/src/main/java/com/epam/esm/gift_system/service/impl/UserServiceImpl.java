package com.epam.esm.gift_system.service.impl;

import com.epam.esm.gift_system.repository.dao.OrderRepository;
import com.epam.esm.gift_system.repository.dao.UserRepository;
import com.epam.esm.gift_system.repository.model.Order;
import com.epam.esm.gift_system.repository.model.User;
import com.epam.esm.gift_system.service.DtoConverterService;
import com.epam.esm.gift_system.service.EntityConverterService;
import com.epam.esm.gift_system.service.UserService;
import com.epam.esm.gift_system.service.dto.ResponseOrderDto;
import com.epam.esm.gift_system.service.dto.UserDto;
import com.epam.esm.gift_system.service.exception.GiftSystemException;
import com.epam.esm.gift_system.service.validator.EntityValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.epam.esm.gift_system.service.constant.GeneralConstant.NULLABLE_ID;
import static com.epam.esm.gift_system.service.dto.UserDto.Role.*;
import static com.epam.esm.gift_system.service.exception.ErrorCode.DUPLICATE_NAME;
import static com.epam.esm.gift_system.service.exception.ErrorCode.NON_EXISTENT_ENTITY;
import static com.epam.esm.gift_system.service.exception.ErrorCode.NON_EXISTENT_PAGE;
import static com.epam.esm.gift_system.service.exception.ErrorCode.USED_ENTITY;
import static com.epam.esm.gift_system.service.exception.ErrorCode.USER_INVALID_NAME;
import static com.epam.esm.gift_system.service.exception.ErrorCode.USER_INVALID_PASSWORD;
import static com.epam.esm.gift_system.service.validator.EntityValidator.ValidationType.STRONG;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final EntityValidator validator;
    private final DtoConverterService dtoConverter;
    private final EntityConverterService entityConverter;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, OrderRepository orderRepository, EntityValidator validator
            , DtoConverterService dtoConverter, EntityConverterService entityConverter, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.validator = validator;
        this.dtoConverter = dtoConverter;
        this.entityConverter = entityConverter;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public UserDto create(UserDto userDto) {
        checkUserValidation(userDto);
        setUserRequiredParams(userDto);
        User user = entityConverter.convertDtoIntoEntity(userDto);
        return dtoConverter.convertEntityIntoDto(userRepository.save(user));
    }

    private void checkUserValidation(UserDto userDto) {
        if (!validator.isNameValid(userDto.getName(), STRONG)) {
            throw new GiftSystemException(USER_INVALID_NAME);
        }
        if (!validator.isPasswordValid(userDto.getPassword())) {
            throw new GiftSystemException(USER_INVALID_PASSWORD);
        }
        if (userRepository.findByName(userDto.getName()).isPresent()) {
            throw new GiftSystemException(DUPLICATE_NAME);
        }
    }

    private void setUserRequiredParams(UserDto userDto) {
        userDto.setId(NULLABLE_ID);
        userDto.setRole(USER);
        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
    }

    @Override
    public UserDto update(Long id, UserDto userDto) {
        throw new UnsupportedOperationException("update method isn't implemented in UserServiceImpl class");
    }

    @Override
    public UserDto findById(Long id) {
        return dtoConverter.convertEntityIntoDto(findUserById(id));
    }

    @Override
    public User findUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new GiftSystemException(NON_EXISTENT_ENTITY));
    }

    @Override
    public UserDto findByName(String name) {
        if (!validator.isNameValid(name, STRONG)) {
            throw new GiftSystemException(USER_INVALID_NAME);
        }
        return userRepository.findByName(name).map(dtoConverter::convertEntityIntoDto)
                .orElseThrow(() -> new GiftSystemException(NON_EXISTENT_ENTITY));
    }

    @Override
    public Page<UserDto> findAll(Pageable pageable) {
        Page<User> userPage = userRepository.findAll(pageable);
        if (!validator.isPageExists(pageable, userPage.getTotalElements())) {
            throw new GiftSystemException(NON_EXISTENT_PAGE);
        }
        List<UserDto> userDtoList = userPage.stream().map(dtoConverter::convertEntityIntoDto).toList();
        return new PageImpl<>(userDtoList, pageable, userPage.getTotalElements());
    }

    @Override
    public Page<ResponseOrderDto> findUserOrderList(Long id, Pageable pageable) {
        if (!userRepository.existsById(id)) {
            throw new GiftSystemException(NON_EXISTENT_ENTITY);
        }
        Page<Order> orderPage = orderRepository.findOrderByUserId(id, pageable);
        if (!validator.isPageExists(pageable, orderPage.getTotalElements())) {
            throw new GiftSystemException(NON_EXISTENT_PAGE);
        }
        List<ResponseOrderDto> orderDtoList = orderPage.stream().map(dtoConverter::convertEntityIntoDto).toList();
        return new PageImpl<>(orderDtoList, pageable, orderPage.getTotalElements());
    }

    @Override
    @Transactional
    public void delete(Long id) {
        User user = findUserById(id);
        if (orderRepository.existsOrderByUserId(id)) {
            throw new GiftSystemException(USED_ENTITY);
        }
        userRepository.delete(user);
    }
}