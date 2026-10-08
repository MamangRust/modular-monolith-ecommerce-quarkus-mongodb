package com.sanedge.gateway.service;

import com.sanedge.gateway.dto.CartDto;
import io.smallrye.mutiny.Uni;

public interface CartService {
    Uni<CartDto.ApiResponsePaginationCart> findAll(String userId, int page, int size, String search);
    Uni<CartDto.ApiResponseCart> create(pb.cart.CartCommand.CreateCartRequest body);
    Uni<CartDto.SimpleResponse> delete(String cartId, String userId);
    Uni<CartDto.SimpleResponse> deleteAll(String userId, java.util.List<String> cartIds);
}
