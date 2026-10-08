package com.sanedge.gateway.service;

import com.sanedge.gateway.dto.ReviewDto;
import io.smallrye.mutiny.Uni;

public interface ReviewService {
    Uni<ReviewDto.ApiResponsePaginationReview> findAll(int page, int size, String search);
    Uni<ReviewDto.ApiResponsePaginationReview> findByProduct(String productId, int page, int size, String search);
    Uni<ReviewDto.ApiResponsePaginationReview> findByMerchant(String merchantId, int page, int size, String search);
    Uni<ReviewDto.ApiResponsePaginationReview> findByActive(int page, int size, String search);
    Uni<ReviewDto.ApiResponsePaginationReview> findByTrashed(int page, int size, String search);
    Uni<ReviewDto.ApiResponseReview> findById(String id);
    Uni<ReviewDto.ApiResponseReview> create(pb.review.ReviewCommand.CreateReviewRequest body);
    Uni<ReviewDto.ApiResponseReview> update(String id, pb.review.ReviewCommand.UpdateReviewRequest body);
    Uni<ReviewDto.ApiResponseReview> delete(String id);
    Uni<ReviewDto.ApiResponseReview> restore(String id);
    Uni<ReviewDto.SimpleResponse> deletePermanent(String id);
    Uni<ReviewDto.SimpleResponse> restoreAll();
    Uni<ReviewDto.SimpleResponse> deleteAllPermanent();
}
