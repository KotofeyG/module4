package com.epam.esm.gift_system.repository.dao;

import com.epam.esm.gift_system.repository.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByName(String name);
}