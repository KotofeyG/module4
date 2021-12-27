package com.epam.esm.gift_system.service.impl;

import com.epam.esm.gift_system.repository.dao.GiftCertificateRepository;
import com.epam.esm.gift_system.repository.dao.TagRepository;
import com.epam.esm.gift_system.repository.model.Tag;
import com.epam.esm.gift_system.service.DtoConverterService;
import com.epam.esm.gift_system.service.EntityConverterService;
import com.epam.esm.gift_system.service.TagService;
import com.epam.esm.gift_system.service.dto.CustomPage;
import com.epam.esm.gift_system.service.dto.TagDto;
import com.epam.esm.gift_system.service.exception.GiftSystemException;
import com.epam.esm.gift_system.service.validator.EntityValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import static com.epam.esm.gift_system.service.constant.GeneralConstant.NULLABLE_ID;
import static com.epam.esm.gift_system.service.exception.ErrorCode.NON_EXISTENT_ENTITY;
import static com.epam.esm.gift_system.service.exception.ErrorCode.NON_EXISTENT_PAGE;
import static com.epam.esm.gift_system.service.exception.ErrorCode.TAG_INVALID_NAME;
import static com.epam.esm.gift_system.service.exception.ErrorCode.USED_ENTITY;
import static com.epam.esm.gift_system.service.validator.EntityValidator.ValidationType.STRONG;

@Service
public class TagServiceImpl implements TagService {
    private final TagRepository tagRepository;
    private final GiftCertificateRepository certificateRepository;
    private final EntityValidator validator;
    private final DtoConverterService dtoConverter;
    private final EntityConverterService entityConverter;

    @Autowired
    public TagServiceImpl(TagRepository tagRepository, GiftCertificateRepository certificateRepository
            , EntityValidator validator, DtoConverterService dtoConverter, EntityConverterService entityConverter) {
        this.tagRepository = tagRepository;
        this.certificateRepository = certificateRepository;
        this.validator = validator;
        this.dtoConverter = dtoConverter;
        this.entityConverter = entityConverter;
    }

    @Override
    public TagDto create(TagDto tagDto) {
        Tag created = entityConverter.convertDtoIntoEntity(tagDto);
        return dtoConverter.convertEntityIntoDto(createTag(created));
    }

    @Override
    public Tag createTag(Tag tag) {
        if (validator.isNameValid(tag.getName(), STRONG)) {
            tag.setId(NULLABLE_ID);
            return tagRepository.findByName(tag.getName()).orElseGet(() -> tagRepository.save(tag));
        }
        throw new GiftSystemException(TAG_INVALID_NAME);
    }

    @Override
    public TagDto update(Long id, TagDto tagDto) {
        throw new UnsupportedOperationException("update method isn't implemented in TagServiceImpl class");
    }

    @Override
    public TagDto findById(Long id) {
        return dtoConverter.convertEntityIntoDto(findTagById(id));
    }

    private Tag findTagById(Long id) {
        return tagRepository.findById(id).orElseThrow(() -> new GiftSystemException(NON_EXISTENT_ENTITY));
    }

    @Override
    public Page<TagDto> findAll(Pageable pageable) {
        Page<Tag> tagPage = tagRepository.findAll(pageable);
        if (!validator.isPageExists(pageable, tagPage.getTotalElements())) {
            throw new GiftSystemException(NON_EXISTENT_PAGE);
        }
        return new CustomPage<>(tagPage.getContent(), tagPage.getPageable(), tagPage.getTotalElements())
                .map(dtoConverter::convertEntityIntoDto);
    }

    @Override
    public void delete(Long id) {
        Tag deleted = findTagById(id);
        if (certificateRepository.findFirstByTagList_Id(id).isPresent()) {
            throw new GiftSystemException(USED_ENTITY);
        }
        tagRepository.delete(deleted);
    }
}