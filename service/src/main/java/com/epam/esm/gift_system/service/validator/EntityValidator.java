package com.epam.esm.gift_system.service.validator;

import com.epam.esm.gift_system.repository.model.GiftCertificateField;
import com.epam.esm.gift_system.service.dto.GiftCertificateAttributeDto;
import com.epam.esm.gift_system.service.dto.RequestOrderDto;
import com.epam.esm.gift_system.service.dto.TagDto;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.epam.esm.gift_system.service.validator.EntityValidator.ValidationType.SOFT;
import static com.epam.esm.gift_system.service.validator.EntityValidator.ValidationType.STRONG;

@Component
public class EntityValidator {
    private static final int ZERO = 0;
    private static final int MIN_EXPIRATION_PERIOD = 1;
    private static final int MAX_EXPIRATION_PERIOD = 255;
    private static final Set<String> AVAILABLE_SORT_ORDERS = Set.of("asc", "desc");
    private static final String NAME_REGEX = "[A-Za-zА-Яа-я-, ]{2,75}";
    private static final String DESCRIPTION_REGEX = "[A-Za-zА-Яа-я\\d-.,:;!?()\" ]{2,255}";
    private static final String PRICE_REGEX = "\\d{1,5}|\\d{1,5}\\.\\d{1,2}";
    private static final String PASSWORD_REGEX = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\w+$).{8,24}$";
    private static final String PAGE_REGEX = "\\d+";

    public enum ValidationType {
        STRONG, SOFT
    }

    public boolean isNameValid(String name, ValidationType type) {
        return isStringFieldValid(name, NAME_REGEX, type);

    }

    public boolean isDescriptionValid(String description, ValidationType type) {
        return isStringFieldValid(description, DESCRIPTION_REGEX, type);
    }

    private boolean isStringFieldValid(String field, String regex, ValidationType type) {
        return type == STRONG
                ? isNotNullAndBlank(field) && field.matches(regex)
                : Objects.isNull(field) || (!field.isBlank() && field.matches(regex));
    }

    private boolean isNotNullAndBlank(String field) {
        return Objects.nonNull(field) && !field.isBlank();
    }

    public boolean isPriceValid(BigDecimal price, ValidationType type) {
        return type == STRONG
                ? Objects.nonNull(price) && matchPriceToRegex(price)
                : Objects.isNull(price) || matchPriceToRegex(price);
    }

    private boolean matchPriceToRegex(BigDecimal price) {
        return String.valueOf(price.doubleValue()).matches(PRICE_REGEX);
    }

    public boolean isDurationValid(int duration, ValidationType type) {
        return type == STRONG ? isDurationRangeValid(duration) : duration == ZERO || isDurationRangeValid(duration);
    }

    private boolean isDurationRangeValid(int duration) {
        return duration >= MIN_EXPIRATION_PERIOD & duration <= MAX_EXPIRATION_PERIOD;
    }

    public boolean isTagListValid(List<TagDto> tagDtoList, ValidationType type) {
        return type == STRONG
                ? !CollectionUtils.isEmpty(tagDtoList) && isTagNameListValid(tagDtoList, STRONG)
                : CollectionUtils.isEmpty(tagDtoList) || isTagNameListValid(tagDtoList, SOFT);
    }

    private boolean isTagNameListValid(List<TagDto> tagDtoList, ValidationType type) {
        return tagDtoList.stream().allMatch(tag -> Objects.nonNull(tag) && isNameValid(tag.getName(), type));
    }

    public boolean isAttributeDtoValid(GiftCertificateAttributeDto attributeDto) {
        List<String> tagNameList = attributeDto.getTagNameList();
        String searchPart = attributeDto.getSearchPart();
        String orderSort = attributeDto.getOrderSort();
        List<String> sortingFieldList = attributeDto.getSortingFieldList();

        return (CollectionUtils.isEmpty(tagNameList) || tagNameList.stream()
                .allMatch(tagName -> Objects.nonNull(tagName) && isNameValid(tagName, STRONG)))
                && isDescriptionValid(searchPart, SOFT)
                && (Objects.isNull(sortingFieldList) || GiftCertificateField.getNameList().containsAll(sortingFieldList))
                && (Objects.isNull(orderSort) || AVAILABLE_SORT_ORDERS.contains(orderSort.toLowerCase()));
    }

    public boolean isRequestOrderDataValid(RequestOrderDto orderDto) {
        return Objects.nonNull(orderDto.getUserId()) && Objects.nonNull(orderDto.getCertificateIdList())
                && orderDto.getCertificateIdList().stream().allMatch(Objects::nonNull);
    }

    public boolean isPageExists(Pageable pageable, Long totalNumber) {
        if (pageable.getPageNumber() == ZERO) {
            return true;
        }
        long lastPage = (long) Math.ceil((double) totalNumber / pageable.getPageSize());
        return pageable.getPageNumber() < lastPage;
    }

    public boolean isPasswordValid(String password) {
        return password != null && password.matches(PASSWORD_REGEX);
    }
}