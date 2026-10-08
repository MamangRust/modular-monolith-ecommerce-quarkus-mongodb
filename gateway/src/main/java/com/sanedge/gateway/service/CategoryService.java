package com.sanedge.gateway.service;

import com.sanedge.gateway.dto.CategoryDto;
import io.smallrye.mutiny.Uni;

public interface CategoryService {
    Uni<CategoryDto.ApiResponsePaginationCategory> findAll(int page, int size, String search);
    Uni<CategoryDto.ApiResponsePaginationCategory> findByActive(int page, int size, String search);
    Uni<CategoryDto.ApiResponsePaginationCategory> findByTrashed(int page, int size, String search);
    Uni<CategoryDto.ApiResponseCategory> findById(String id);
    Uni<CategoryDto.ApiResponseMonthlyTotalPrice> findMonthTotalPrice(int year, int month);
    Uni<CategoryDto.ApiResponseYearlyTotalPrice> findYearTotalPrice(int year);
    Uni<CategoryDto.ApiResponseMonthlyTotalPrice> findMonthTotalPriceByMerchant(String merchantId, int year, int month);
    Uni<CategoryDto.ApiResponseYearlyTotalPrice> findYearlyTotalPricesByMerchant(String merchantId, int year);
    Uni<CategoryDto.ApiResponseMonthlyTotalPrice> findMonthlyTotalPricesById(String categoryId, int year, int month);
    Uni<CategoryDto.ApiResponseYearlyTotalPrice> findYearlyTotalPricesById(String categoryId, int year);
    Uni<CategoryDto.ApiResponseMonthlyPrice> findMonthPrice(int year);
    Uni<CategoryDto.ApiResponseYearlyPrice> findYearPrice(int year);
    Uni<CategoryDto.ApiResponseMonthlyPrice> findMonthPriceByMerchant(String merchantId, int year);
    Uni<CategoryDto.ApiResponseYearlyPrice> findYearPriceByMerchant(String merchantId, int year);
    Uni<CategoryDto.ApiResponseMonthlyPrice> findMonthPriceById(String categoryId, int year);
    Uni<CategoryDto.ApiResponseYearlyPrice> findYearPriceById(String categoryId, int year);
    Uni<CategoryDto.ApiResponseCategory> create(CategoryDto.CreateCategoryRequest body);
    Uni<CategoryDto.ApiResponseCategory> update(String id, CategoryDto.UpdateCategoryRequest body);
    Uni<CategoryDto.ApiResponseCategory> trash(String id);
    Uni<CategoryDto.ApiResponseCategory> restore(String id);
    Uni<CategoryDto.SimpleResponse> deleteCategoryPermanent(String id);
    Uni<CategoryDto.SimpleResponse> restoreAllCategories();
    Uni<CategoryDto.SimpleResponse> deleteAllCategoriesPermanent();
}
