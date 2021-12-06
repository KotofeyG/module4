package com.epam.esm.gift_system.service;

import com.epam.esm.gift_system.repository.model.GiftCertificate;
import com.epam.esm.gift_system.service.dto.GiftCertificateAttributeDto;
import com.epam.esm.gift_system.service.dto.GiftCertificateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GiftCertificateService extends BaseService<GiftCertificateDto> {
    GiftCertificate findCertificateById(Long id);

    Page<GiftCertificateDto> findByAttributes(GiftCertificateAttributeDto attributeDto, Pageable pageable);
}