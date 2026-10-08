package com.sanedge.gateway.service;

import com.sanedge.gateway.dto.MerchantDto;
import io.smallrye.mutiny.Uni;

public interface MerchantService {
    Uni<MerchantDto.ApiResponsePaginationMerchant> listMerchants(int page, int size, String search);
    Uni<MerchantDto.ApiResponseMerchant> getMerchant(String id);
    Uni<MerchantDto.ApiResponsePaginationMerchantDeleteAt> getActiveMerchants(int page, int size, String search);
    Uni<MerchantDto.ApiResponsePaginationMerchantDeleteAt> getTrashedMerchants(int page, int size, String search);

    Uni<MerchantDto.ApiResponseMerchant> createMerchant(pb.merchant.MerchantCommand.CreateMerchantRequest body);
    Uni<MerchantDto.ApiResponseMerchant> updateMerchant(String id, pb.merchant.MerchantCommand.UpdateMerchantRequest body);
    Uni<MerchantDto.ApiResponseMerchant> updateMerchantStatus(String id, pb.merchant.MerchantCommand.UpdateMerchantStatusRequest body);
    
    Uni<MerchantDto.ApiResponseMerchantDeleteAt> deleteMerchant(String id);
    Uni<MerchantDto.ApiResponseMerchant> restoreMerchant(String id);
    Uni<MerchantDto.SimpleResponse> deleteMerchantPermanent(String id);
    Uni<MerchantDto.SimpleResponse> restoreAllMerchant();
    Uni<MerchantDto.SimpleResponse> deleteAllMerchantPermanent();
}
