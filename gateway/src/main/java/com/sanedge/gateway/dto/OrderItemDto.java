package com.sanedge.gateway.dto;

import java.util.List;

public class OrderItemDto {

    public record OrderItemResponse(
            String id,
            String orderId,
            String productId,
            int quantity,
            int price,
            String createdAt,
            String updatedAt) {
        public static OrderItemResponse from(pb.order_item.OrderItemCommon.OrderItemResponse proto) {
            return new OrderItemResponse(
                    proto.getId(),
                    proto.getOrderId(),
                    proto.getProductId(),
                    proto.getQuantity(),
                    proto.getPrice(),
                    proto.getCreatedAt(),
                    proto.getUpdatedAt());
        }

        public static OrderItemResponse from(pb.order_item.OrderItemCommon.OrderItemResponseDeleteAt proto) {
            return new OrderItemResponse(
                    proto.getId(),
                    proto.getOrderId(),
                    proto.getProductId(),
                    proto.getQuantity(),
                    proto.getPrice(),
                    proto.getCreatedAt(),
                    proto.getUpdatedAt());
        }
    }

    public record ApiResponsePaginationOrderItem(
            List<OrderItemResponse> data,
            String status,
            String message) {
        public static ApiResponsePaginationOrderItem from(
                pb.order_item.OrderItemCommon.ApiResponsePaginationOrderItem proto) {
            return new ApiResponsePaginationOrderItem(
                    proto.getDataList().stream().map(OrderItemResponse::from).toList(),
                    proto.getStatus(),
                    proto.getMessage());
        }
    }

    public record ApiResponsePaginationOrderItemDeleteAt(
            List<OrderItemResponse> data,
            String status,
            String message) {
        public static ApiResponsePaginationOrderItemDeleteAt from(
                pb.order_item.OrderItemCommon.ApiResponsePaginationOrderItemDeleteAt proto) {
            return new ApiResponsePaginationOrderItemDeleteAt(
                    proto.getDataList().stream().map(OrderItemResponse::from).toList(),
                    proto.getStatus(),
                    proto.getMessage());
        }
    }

    public record ApiResponsesOrderItem(
            List<OrderItemResponse> data,
            String status,
            String message) {
        public static ApiResponsesOrderItem from(pb.order_item.OrderItemCommon.ApiResponsesOrderItem proto) {
            return new ApiResponsesOrderItem(
                    proto.getDataList().stream().map(OrderItemResponse::from).toList(),
                    proto.getStatus(),
                    proto.getMessage());
        }
    }
}
