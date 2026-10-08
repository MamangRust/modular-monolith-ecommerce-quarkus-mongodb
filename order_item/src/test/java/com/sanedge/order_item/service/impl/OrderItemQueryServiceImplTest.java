package com.sanedge.order_item.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.List;
import java.util.function.Supplier;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import com.sanedge.common.domain.response.ApiResponse;
import com.sanedge.common.domain.response.PagedResult;
import com.sanedge.common.observability.TracingMetrics;
import com.sanedge.order_item.domain.requests.FindAllOrderItemRequest;
import com.sanedge.order_item.entity.OrderItem;
import com.sanedge.order_item.repository.OrderItemRepository;

import io.opentelemetry.api.common.Attributes;
import io.smallrye.mutiny.Uni;

@ExtendWith(MockitoExtension.class)
class OrderItemQueryServiceImplTest {

    private static final String ORDER_ITEM_ID = "507f1f77bcf86cd7994390e1";
    private static final String ORDER_ID = "507f1f77bcf86cd799439011";
    private static final String ORDER_ID_404 = "507f1f77bcf86cd799439099";
    private static final String PRODUCT_ID = "507f1f77bcf86cd7994390c1";

    @Mock
    private OrderItemRepository orderItemRepository;
    @Mock
    private TracingMetrics tracingMetrics;

    private OrderItemQueryServiceImpl service;

    @BeforeEach
    void setUp() {
        lenient().doAnswer(invokeSupplier())
                .when(tracingMetrics).traceAndMeasure(anyString(), anyString(), any());
        lenient().doAnswer(invokeSupplier())
                .when(tracingMetrics)
                        .traceAndMeasure(anyString(), anyString(), any(Attributes.class), any());

        service = new OrderItemQueryServiceImpl(orderItemRepository, tracingMetrics);
    }

    private OrderItem mkItem(ObjectId id) {
        OrderItem o = new OrderItem();
        o.id = id;
        o.setOrderId(new ObjectId(ORDER_ID));
        o.setProductId(new ObjectId(PRODUCT_ID));
        o.setPrice(15000);
        o.setQuantity(2);
        o.setCreatedAt(Instant.now());
        o.setUpdatedAt(Instant.now());
        return o;
    }

    @Test
    void findAll_Success() {
        FindAllOrderItemRequest req = new FindAllOrderItemRequest();
        req.setPage(1);
        req.setPageSize(10);
        when(orderItemRepository.findOrderItems(any(FindAllOrderItemRequest.class)))
                .thenReturn(Uni.createFrom().item(new PagedResult<>(List.of(mkItem(new ObjectId(ORDER_ITEM_ID))), 1)));
        ApiResponse<List<com.sanedge.order_item.domain.response.OrderItemResponse>> result = service.findAll(req)
                .await().indefinitely();
        assertThat(result.status()).isEqualTo("success");
        assertThat(result.message()).isEqualTo("Order items retrieved successfully");
    }

    @Test
    void findTrashed_Success() {
        FindAllOrderItemRequest req = new FindAllOrderItemRequest();
        req.setPage(1);
        req.setPageSize(10);
        when(orderItemRepository.findTrashedOrderItems(any(FindAllOrderItemRequest.class)))
                .thenReturn(Uni.createFrom().item(new PagedResult<>(List.of(), 0)));
        var result = service.findTrashed(req).await().indefinitely();
        assertThat(result.status()).isEqualTo("success");
    }

    @Test
    void findByOrder_Empty() {
        when(orderItemRepository.findOrderItemByOrder(anyString()))
                .thenReturn(Uni.createFrom().item(List.of()));
        var result = service.findByOrder(ORDER_ID_404).await().indefinitely();
        assertThat(result.status()).isEqualTo("success");
        assertThat(result.data()).isEmpty();
    }

    @Test
    void findActive_Success() {
        FindAllOrderItemRequest req = new FindAllOrderItemRequest();
        req.setPage(1);
        req.setPageSize(10);
        when(orderItemRepository.findActiveOrderItems(any(FindAllOrderItemRequest.class)))
                .thenReturn(Uni.createFrom().item(new PagedResult<>(List.of(), 0)));
        var result = service.findActive(req).await().indefinitely();
        assertThat(result.status()).isEqualTo("success");
    }

    @Test
    void findByOrder_Success() {
        when(orderItemRepository.findOrderItemByOrder(anyString()))
                .thenReturn(Uni.createFrom().item(List.of(mkItem(new ObjectId(ORDER_ITEM_ID)))));
        var result = service.findByOrder(ORDER_ID).await().indefinitely();
        assertThat(result.status()).isEqualTo("success");
        assertThat(result.data()).hasSize(1);
    }

    /**
     * Finds the Supplier argument in the invocation regardless of whether it was
     * passed positionally in the 3-arg overload (arg index 2) or 4-arg overload
     * (arg index 3), then invokes it and returns the resulting Uni. This lets
     * a single Answer<?> body serve both traceAndMeasure overloads.
     */
    private Answer<Uni<?>> invokeSupplier() {
        return invocation -> {
            Supplier<?> supplier = null;
            for (Object arg : invocation.getArguments()) {
                if (arg instanceof Supplier<?>) {
                    supplier = (Supplier<?>) arg;
                    break;
                }
            }
            return supplier != null ? (Uni<?>) supplier.get() : null;
        };
    }
}
