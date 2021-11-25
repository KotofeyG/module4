package com.epam.esm.gift_system.service;

import com.epam.esm.gift_system.service.dto.RequestOrderDto;
import com.epam.esm.gift_system.service.dto.ResponseOrderDto;

public interface OrderService extends BaseService<ResponseOrderDto> {
    ResponseOrderDto create(RequestOrderDto orderDto);
}