package com.sanedge.gateway.service.impl;

import com.sanedge.gateway.dto.OrderItemDto;
import com.sanedge.gateway.service.OrderItemService;
import com.sanedge.gateway.telemetry.TelemetryHelper;
import io.quarkus.grpc.GrpcClient;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

@ApplicationScoped
public class OrderItemServiceImpl implements OrderItemService {

    private static final Logger LOG = Logger.getLogger(OrderItemServiceImpl.class);

    @Inject
    TelemetryHelper telemetryHelper;

    @GrpcClient("order_item")
    pb.order_item.MutinyOrderItemQueryServiceGrpc.MutinyOrderItemQueryServiceStub orderItemQueryService;

    @Override
    public Uni<OrderItemDto.ApiResponsePaginationOrderItem> findAll(int page, int size, String search) {
        return telemetryHelper.traceAndMetric("orderItem.findAll", () -> orderItemQueryService.findAll(
                pb.order_item.OrderItemQuery.FindAllOrderItemRequest.newBuilder()
                        .setPage(page)
                        .setPageSize(size)
                        .setSearch(search == null ? "" : search)
                        .build())
                .map(OrderItemDto.ApiResponsePaginationOrderItem::from)
                .onFailure().invoke(t -> LOG.error("Failed to find all order items: " + t.getMessage(), t)));
    }

    @Override
    public Uni<OrderItemDto.ApiResponsePaginationOrderItemDeleteAt> findByActive(int page, int size, String search) {
        return telemetryHelper.traceAndMetric("orderItem.findByActive", () -> orderItemQueryService.findByActive(
                pb.order_item.OrderItemQuery.FindAllOrderItemRequest.newBuilder()
                        .setPage(page)
                        .setPageSize(size)
                        .setSearch(search == null ? "" : search)
                        .build())
                .map(OrderItemDto.ApiResponsePaginationOrderItemDeleteAt::from)
                .onFailure().invoke(t -> LOG.error("Failed to find active order items: " + t.getMessage(), t)));
    }

    @Override
    public Uni<OrderItemDto.ApiResponsePaginationOrderItemDeleteAt> findByTrashed(int page, int size, String search) {
        return telemetryHelper.traceAndMetric("orderItem.findByTrashed", () -> orderItemQueryService.findByTrashed(
                pb.order_item.OrderItemQuery.FindAllOrderItemRequest.newBuilder()
                        .setPage(page)
                        .setPageSize(size)
                        .setSearch(search == null ? "" : search)
                        .build())
                .map(OrderItemDto.ApiResponsePaginationOrderItemDeleteAt::from)
                .onFailure().invoke(t -> LOG.error("Failed to find trashed order items: " + t.getMessage(), t)));
    }

    @Override
    public Uni<OrderItemDto.ApiResponsesOrderItem> findOrderItemByOrder(String orderId) {
        return telemetryHelper.traceAndMetric("orderItem.findOrderItemByOrder", () -> orderItemQueryService.findOrderItemByOrder(
                pb.order_item.OrderItemCommon.FindByIdOrderItemRequest.newBuilder()
                        .setId(orderId)
                        .build())
                .map(OrderItemDto.ApiResponsesOrderItem::from)
                .onFailure().invoke(t -> LOG.error("Failed to find order items by order " + orderId + ": " + t.getMessage(), t)));
    }
}
