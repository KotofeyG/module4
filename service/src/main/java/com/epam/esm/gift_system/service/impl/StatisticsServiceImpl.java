package com.epam.esm.gift_system.service.impl;

import com.epam.esm.gift_system.repository.dao.StatisticsRepository;
import com.epam.esm.gift_system.service.DtoConverterService;
import com.epam.esm.gift_system.service.StatisticsService;
import com.epam.esm.gift_system.service.dto.TagDto;
import com.epam.esm.gift_system.service.exception.GiftSystemException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.epam.esm.gift_system.service.exception.ErrorCode.NON_EXISTENT_ENTITY;

@Service
public class StatisticsServiceImpl implements StatisticsService {
    private final StatisticsRepository statisticsRepository;
    private final DtoConverterService dtoConverter;

    @Autowired
    public StatisticsServiceImpl(StatisticsRepository statisticsRepository, DtoConverterService dtoConverter) {
        this.statisticsRepository = statisticsRepository;
        this.dtoConverter = dtoConverter;
    }

    @Override
    public TagDto findMostPopularTag() {
        return statisticsRepository.findMostPopularTag()
                .map(dtoConverter::convertEntityIntoDto)
                .orElseThrow(() -> new GiftSystemException(NON_EXISTENT_ENTITY));
    }
}