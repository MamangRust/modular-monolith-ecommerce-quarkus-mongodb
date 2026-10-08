package com.sanedge.gateway.service;

import com.sanedge.gateway.dto.OrderItemDto;
import io.smallrye.mutiny.Uni;

public interface OrderItemService {

    Uni<OrderItemDto.ApiResponsePaginationOrderItem> findAll(int page, int size, String search);

    Uni<OrderItemDto.ApiResponsePaginationOrderItemDeleteAt> findByActive(int page, int size, String search);

    Uni<OrderItemDto.ApiResponsePaginationOrderItemDeleteAt> findByTrashed(int page, int size, String search);

    Uni<OrderItemDto.ApiResponsesOrderItem> findOrderItemByOrder(String orderId);
}
