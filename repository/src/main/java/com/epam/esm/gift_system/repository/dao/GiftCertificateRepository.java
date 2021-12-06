package com.epam.esm.gift_system.repository.dao;

import com.epam.esm.gift_system.repository.model.GiftCertificate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

import static com.epam.esm.gift_system.repository.dao.constant.SqlQuery.FIND_CERTIFICATES_BY_SEARCH_PART;
import static com.epam.esm.gift_system.repository.dao.constant.SqlQuery.FIND_CERTIFICATES_BY_TAG_NAMES_AND_SEARCH_PART;

public interface GiftCertificateRepository extends JpaRepository<GiftCertificate, Long> {
    @Query(FIND_CERTIFICATES_BY_SEARCH_PART)
    Page<GiftCertificate> findByAttributes(@Param("searchPart") String searchPart, Pageable pageable);

    @Query(FIND_CERTIFICATES_BY_TAG_NAMES_AND_SEARCH_PART)
    Page<GiftCertificate> findByAttributes(@Param("tagNameList") List<String> tagNameList, @Param("tagNumber") long tagNumber
            , @Param("searchPart") String searchPart, Pageable pageable);

    Optional<GiftCertificate> findFirstByTagList_Id(Long tagId);
}