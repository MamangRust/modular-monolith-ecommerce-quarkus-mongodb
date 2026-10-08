package com.sanedge.gateway.service;

import com.sanedge.gateway.dto.OrderDto;
import io.smallrye.mutiny.Uni;

public interface OrderService {
    Uni<OrderDto.ApiResponsePaginationOrder> findAll(int page, int size, String search);
    Uni<OrderDto.ApiResponsePaginationOrderDeleteAt> findByActive(int page, int size, String search);
    Uni<OrderDto.ApiResponsePaginationOrderDeleteAt> findByTrashed(int page, int size, String search);
    Uni<OrderDto.ApiResponseOrder> findById(String id);

    Uni<OrderDto.ApiResponseOrder> create(pb.order.OrderCommand.CreateOrderRequest body);
    Uni<OrderDto.ApiResponseOrder> update(String id, pb.order.OrderCommand.UpdateOrderRequest body);
    Uni<OrderDto.ApiResponseOrderDeleteAt> delete(String id);
    Uni<OrderDto.ApiResponseOrderDeleteAt> restore(String id);
    Uni<OrderDto.SimpleResponse> deletePermanent(String id);
    Uni<OrderDto.ApiResponseOrder> updateTotalPrice(pb.order.OrderCommand.UpdateOrderTotalPriceRequest body);
    Uni<OrderDto.SimpleResponse> restoreAll();
    Uni<OrderDto.SimpleResponse> deleteAllPermanent();

    Uni<OrderDto.ApiResponseOrderMonthly> findMonthlyRevenue(int year, int month);
    Uni<OrderDto.ApiResponseOrderYearly> findYearlyRevenue(int year);
    Uni<OrderDto.ApiResponseOrderMonthlyTotalRevenue> findMonthlyTotalRevenue(int year, int month);
    Uni<OrderDto.ApiResponseOrderYearlyTotalRevenue> findYearlyTotalRevenue(int year);

    Uni<OrderDto.ApiResponseOrderMonthly> findMonthlyRevenueByMerchant(String merchantId, int year, int month);
    Uni<OrderDto.ApiResponseOrderYearly> findYearlyRevenueByMerchant(String merchantId, int year);
    Uni<OrderDto.ApiResponseOrderMonthlyTotalRevenue> findMonthlyTotalRevenueByMerchant(String merchantId, int year, int month);
    Uni<OrderDto.ApiResponseOrderYearlyTotalRevenue> findYearlyTotalRevenueByMerchant(String merchantId, int year);
}
