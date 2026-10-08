package com.sanedge.gateway.service;

import com.sanedge.gateway.dto.BannerDto;
import io.smallrye.mutiny.Uni;

public interface BannerService {
    Uni<BannerDto.ApiResponsePaginationBanner> findAll(int page, int size, String search);
    Uni<BannerDto.ApiResponsePaginationBanner> findByActive(int page, int size, String search);
    Uni<BannerDto.ApiResponsePaginationBanner> findByTrashed(int page, int size, String search);
    Uni<BannerDto.ApiResponseBanner> findById(String id);
    Uni<BannerDto.ApiResponseBanner> createBanner(pb.banner.BannerCommand.CreateBannerRequest req);
    Uni<BannerDto.ApiResponseBanner> updateBanner(String id, pb.banner.BannerCommand.UpdateBannerRequest req);
    Uni<BannerDto.ApiResponseBanner> deleteBanner(String id);
    Uni<BannerDto.ApiResponseBanner> restoreBanner(String id);
    Uni<BannerDto.SimpleResponse> deleteBannerPermanent(String id);
    Uni<BannerDto.SimpleResponse> restoreAllBanners();
    Uni<BannerDto.SimpleResponse> deleteAllBannersPermanent();
}
