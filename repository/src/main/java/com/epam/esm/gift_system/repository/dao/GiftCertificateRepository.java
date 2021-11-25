package com.epam.esm.gift_system.repository.dao;

import com.epam.esm.gift_system.repository.model.GiftCertificate;
import com.epam.esm.gift_system.repository.model.GiftCertificateAttribute;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface GiftCertificateRepository extends JpaRepository<GiftCertificate, Long> {
//    @Query("SELECT DISTINCT c FROM GiftCertificate c JOIN c.tagList l WHERE l.name=:#{#attribute.tagNameList}")
    @Query(value = "SELECT gift_certificates.id, gift_certificates.name" +
            ", description, price, duration, create_date, last_update_date, tags.id, tags.name FROM gift_certificates" +
            " JOIN tags_certificates ON gift_certificate_id=gift_certificates.id JOIN tags ON tag_id=tags.id" +
            " WHERE "+ "#{#attribute.buildQueryPartForTagNameList()}" + " AND " + "#{#attribute.buildQueryPartForSearchPart()}"
            + " ORDER BY " + "#{#attribute.buildOrderByFields()}", nativeQuery = true)
    Page<GiftCertificate> findByAttributes(@Param("attribute") GiftCertificateAttribute attribute, @Param("pageable") Pageable pageable);

    Optional<GiftCertificate> findFirstByTagList_Id(Long tagId);
}