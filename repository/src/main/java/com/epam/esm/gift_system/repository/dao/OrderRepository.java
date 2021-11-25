package com.epam.esm.gift_system.repository.dao;

import com.epam.esm.gift_system.repository.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findOrderByUserId(Long userId, Pageable pageable);

    boolean existsOrderByUserId(Long userId);

    Optional<Order> findFirstByCertificateList_Id(Long certificateId);
}