package com.epam.esm.gift_system.web.controller;

import com.epam.esm.gift_system.service.UserService;
import com.epam.esm.gift_system.service.dto.ResponseOrderDto;
import com.epam.esm.gift_system.service.dto.UserDto;
import com.epam.esm.gift_system.web.hateaos.HateaosBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final HateaosBuilder hateaosBuilder;

    @Autowired
    public UserController(UserService userService, HateaosBuilder hateaosBuilder) {
        this.userService = userService;
        this.hateaosBuilder = hateaosBuilder;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('users:create')")
    public UserDto create(@RequestBody UserDto userDto) {
        UserDto created = userService.create(userDto);
        hateaosBuilder.setLinks(created);
        return created;
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('users:read') || #id.equals(authentication.principal.userId)")
    public UserDto findById(@PathVariable Long id) {
        UserDto userDto = userService.findById(id);
        hateaosBuilder.setLinks(userDto);
        return userDto;
    }

    @GetMapping("/search/{name}")
    @PreAuthorize("hasAuthority('users:read') || #name.equals(authentication.principal.username)")
    public UserDto findByName(@PathVariable String name) {
        UserDto userDto = userService.findByName(name);
        hateaosBuilder.setLinks(userDto);
        return userDto;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('users:read')")
    public Page<UserDto> findAll(Pageable pageable) {
        Page<UserDto> page = userService.findAll(pageable);
        page.getContent().forEach(hateaosBuilder::setLinks);
        return page;
    }

    @GetMapping("/{id}/orders")
    @PreAuthorize("hasAuthority('users:read') || #id.equals(authentication.principal.userId)")
    public Page<ResponseOrderDto> findUserOrderList(@PathVariable Long id, Pageable pageable) {
        Page<ResponseOrderDto> page = userService.findUserOrderList(id, pageable);
        page.getContent().forEach(hateaosBuilder::setLinks);
        return page;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('users:delete')")
    public void delete(@PathVariable Long id) {
        userService.delete(id);
    }
}