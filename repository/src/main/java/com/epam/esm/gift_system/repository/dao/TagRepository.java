package com.epam.esm.gift_system.repository.dao;

import com.epam.esm.gift_system.repository.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findByName(String name);
}