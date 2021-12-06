package com.epam.esm.gift_system.repository.dao;

import com.epam.esm.gift_system.repository.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

import static com.epam.esm.gift_system.repository.dao.constant.SqlQuery.FIND_MOST_POPULAR_TAG_OF_RICHEST_USER;

public interface StatisticsRepository extends JpaRepository<Tag, Long> {
    @Query(value = FIND_MOST_POPULAR_TAG_OF_RICHEST_USER, nativeQuery = true)
    Optional<Tag> findMostPopularTag();
}