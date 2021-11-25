package com.epam.esm.gift_system.service.impl;

import com.epam.esm.gift_system.repository.dao.GiftCertificateRepository;
import com.epam.esm.gift_system.repository.dao.OrderRepository;
import com.epam.esm.gift_system.repository.model.GiftCertificate;
import com.epam.esm.gift_system.repository.model.GiftCertificateAttribute;
import com.epam.esm.gift_system.repository.model.Tag;
import com.epam.esm.gift_system.service.DtoConverterService;
import com.epam.esm.gift_system.service.EntityConverterService;
import com.epam.esm.gift_system.service.GiftCertificateService;
import com.epam.esm.gift_system.service.TagService;
import com.epam.esm.gift_system.service.dto.GiftCertificateAttributeDto;
import com.epam.esm.gift_system.service.dto.GiftCertificateDto;
import com.epam.esm.gift_system.service.dto.TagDto;
import com.epam.esm.gift_system.service.exception.GiftSystemException;
import com.epam.esm.gift_system.service.validator.EntityValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.epam.esm.gift_system.service.constant.GeneralConstant.NULLABLE_ID;
import static com.epam.esm.gift_system.service.constant.GeneralConstant.ZERO;
import static com.epam.esm.gift_system.service.exception.ErrorCode.CERTIFICATE_INVALID_DESCRIPTION;
import static com.epam.esm.gift_system.service.exception.ErrorCode.CERTIFICATE_INVALID_DURATION;
import static com.epam.esm.gift_system.service.exception.ErrorCode.CERTIFICATE_INVALID_NAME;
import static com.epam.esm.gift_system.service.exception.ErrorCode.CERTIFICATE_INVALID_PRICE;
import static com.epam.esm.gift_system.service.exception.ErrorCode.INVALID_ATTRIBUTE_LIST;
import static com.epam.esm.gift_system.service.exception.ErrorCode.NON_EXISTENT_ENTITY;
import static com.epam.esm.gift_system.service.exception.ErrorCode.NON_EXISTENT_PAGE;
import static com.epam.esm.gift_system.service.exception.ErrorCode.NULLABLE_OBJECT;
import static com.epam.esm.gift_system.service.exception.ErrorCode.TAG_INVALID_NAME;
import static com.epam.esm.gift_system.service.exception.ErrorCode.USED_ENTITY;
import static com.epam.esm.gift_system.service.validator.EntityValidator.ValidationType.SOFT;
import static com.epam.esm.gift_system.service.validator.EntityValidator.ValidationType.STRONG;

@Service
public class GiftCertificateServiceImpl implements GiftCertificateService {
    private final GiftCertificateRepository certificateRepository;
    private final OrderRepository orderRepository;
    private final TagService tagService;
    private final EntityValidator validator;
    private final DtoConverterService dtoConverter;
    private final EntityConverterService entityConverter;

    @Autowired
    public GiftCertificateServiceImpl(GiftCertificateRepository certificateRepository, OrderRepository orderRepository
            , TagService tagService, EntityValidator validator, DtoConverterService dtoConverter, EntityConverterService entityConverter) {
        this.certificateRepository = certificateRepository;
        this.orderRepository = orderRepository;
        this.tagService = tagService;
        this.validator = validator;
        this.dtoConverter = dtoConverter;
        this.entityConverter = entityConverter;
    }

    @Override
    @Transactional
    public GiftCertificateDto create(GiftCertificateDto certificateDto) {
        checkCertificateValidation(certificateDto, STRONG);
        GiftCertificate certificate = entityConverter.convertDtoIntoEntity(certificateDto);
        setTagListInCertificate(certificate);
        certificate.setId(NULLABLE_ID);
        certificateRepository.save(certificate);
        return dtoConverter.convertEntityIntoDto(certificate);
    }

    private void setTagListInCertificate(GiftCertificate certificate) {
        Set<Tag> tagList = certificate.getTagList();
        tagList = tagList.stream().map(tagService::createTag).collect(Collectors.toCollection(LinkedHashSet::new));
        certificate.setTagList(tagList);
    }

    @Override
    @Transactional
    public GiftCertificateDto update(Long id, GiftCertificateDto certificateDto) {
        checkCertificateValidation(certificateDto, SOFT);
        GiftCertificate persistedCertificate = findCertificateById(id);
        setUpdatedFields(persistedCertificate, certificateDto);
        setUpdatedTagList(persistedCertificate, certificateDto.getTagDtoList());
        certificateRepository.save(persistedCertificate);
        return dtoConverter.convertEntityIntoDto(persistedCertificate);
    }

    private void checkCertificateValidation(GiftCertificateDto certificateDto, EntityValidator.ValidationType type) {
        if (Objects.isNull(certificateDto)) {
            throw new GiftSystemException(NULLABLE_OBJECT);
        }
        if (!validator.isNameValid(certificateDto.getName(), type)) {
            throw new GiftSystemException(CERTIFICATE_INVALID_NAME);
        }
        if (!validator.isDescriptionValid(certificateDto.getDescription(), type)) {
            throw new GiftSystemException(CERTIFICATE_INVALID_DESCRIPTION);
        }
        if (!validator.isPriceValid(certificateDto.getPrice(), type)) {
            throw new GiftSystemException(CERTIFICATE_INVALID_PRICE);
        }
        if (!validator.isDurationValid(certificateDto.getDuration(), type)) {
            throw new GiftSystemException(CERTIFICATE_INVALID_DURATION);
        }
        if (!validator.isTagListValid(certificateDto.getTagDtoList(), type)) {
            throw new GiftSystemException(TAG_INVALID_NAME);
        }
    }

    private void setUpdatedFields(GiftCertificate persistedCertificate, GiftCertificateDto updatedCertificateDto) {
        String name = updatedCertificateDto.getName();
        String description = updatedCertificateDto.getDescription();
        BigDecimal price = updatedCertificateDto.getPrice();
        int duration = updatedCertificateDto.getDuration();

        if (Objects.nonNull(name) && !persistedCertificate.getName().equals(name)) {
            persistedCertificate.setName(name);
        }
        if (Objects.nonNull(description) && !persistedCertificate.getDescription().equals(description)) {
            persistedCertificate.setDescription(description);
        }
        if (Objects.nonNull(price) && !persistedCertificate.getPrice().equals(price)) {
            persistedCertificate.setPrice(price);
        }
        if (duration != ZERO && persistedCertificate.getDuration() != duration) {
            persistedCertificate.setDuration(duration);
        }
    }

    private void setUpdatedTagList(GiftCertificate persistedCertificate, List<TagDto> tagDtoList) {
        if (CollectionUtils.isEmpty(tagDtoList)) {
            return;
        }
        Set<Tag> updatedTagSet = tagDtoList.stream()
                .map(entityConverter::convertDtoIntoEntity)
                .map(tagService::createTag)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        persistedCertificate.setTagList(updatedTagSet);
    }

    @Override
    public GiftCertificateDto findById(Long id) {
        return dtoConverter.convertEntityIntoDto(findCertificateById(id));
    }

    @Override
    public GiftCertificate findCertificateById(Long id) {
        return certificateRepository.findById(id).orElseThrow(() -> new GiftSystemException(NON_EXISTENT_ENTITY));
    }

    @Override
    public Page<GiftCertificateDto> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public Page<GiftCertificateDto> findByAttributes(GiftCertificateAttributeDto attributeDto, Pageable pageable) {
        checkSearchParams(attributeDto, pageable);
        GiftCertificateAttribute attribute = entityConverter.convertDtoIntoEntity(attributeDto);
        Page<GiftCertificate> certificatePage = certificateRepository.findByAttributes(attribute, pageable);
        if (!validator.isPageExists(pageable, certificatePage.getTotalElements())) {
            throw new GiftSystemException(NON_EXISTENT_PAGE);
        }
        List<GiftCertificateDto> certificateDtoList = certificatePage.stream().map(dtoConverter::convertEntityIntoDto).toList();
        return new PageImpl<>(certificateDtoList, pageable, certificatePage.getTotalElements());
    }

    private void checkSearchParams(GiftCertificateAttributeDto attributeDto, Pageable pageable) {
        if (!validator.isAttributeDtoValid(attributeDto)) {
            throw new GiftSystemException(INVALID_ATTRIBUTE_LIST);
        }
    }

    @Override
    @Transactional
    public void delete(Long id) {
        GiftCertificate deletedCertificate = findCertificateById(id);
        if (orderRepository.findFirstByCertificateList_Id(id).isPresent()) {
            throw new GiftSystemException(USED_ENTITY);
        }
        certificateRepository.delete(deletedCertificate);
    }
}