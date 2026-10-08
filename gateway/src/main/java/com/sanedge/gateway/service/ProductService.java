package com.sanedge.gateway.service;

import com.sanedge.gateway.dto.ProductDto;
import io.smallrye.mutiny.Uni;

public interface ProductService {
    Uni<ProductDto.ApiResponsePaginationProduct> findAll(int page, int size, String search);
    Uni<ProductDto.ApiResponsePaginationProduct> findByActive(int page, int size, String search);
    Uni<ProductDto.ApiResponsePaginationProduct> findByTrashed(int page, int size, String search);
    Uni<ProductDto.ApiResponseProduct> findById(String id);
    Uni<ProductDto.ApiResponseProduct> create(pb.product.ProductCommand.CreateProductRequest body);
    Uni<ProductDto.ApiResponseProduct> update(String id, pb.product.ProductCommand.UpdateProductRequest body);
    Uni<ProductDto.ApiResponseProduct> delete(String id);
    Uni<ProductDto.ApiResponseProduct> restore(String id);
    Uni<ProductDto.SimpleResponse> deletePermanent(String id);
    Uni<ProductDto.SimpleResponse> restoreAll();
    Uni<ProductDto.SimpleResponse> deleteAllPermanent();
}
