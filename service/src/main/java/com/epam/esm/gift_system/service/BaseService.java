package com.epam.esm.gift_system.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface BaseService<T> {
    T create(T t);

    T update(Long id, T t);

    T findById(Long id);

    Page<T> findAll(Pageable pageable);

    void delete(Long id);
}