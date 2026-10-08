package com.sanedge.gateway.service;

import com.sanedge.gateway.dto.MerchantPolicyDto;
import io.smallrye.mutiny.Uni;

public interface MerchantPolicyService {
    Uni<MerchantPolicyDto.ApiResponsePaginationPolicy> findAll(int page, int size, String search);
    Uni<MerchantPolicyDto.ApiResponsePaginationPolicy> findByActive(int page, int size, String search);
    Uni<MerchantPolicyDto.ApiResponsePaginationPolicy> findByTrashed(int page, int size, String search);
    Uni<MerchantPolicyDto.ApiResponsePolicy> findById(String id);
    Uni<MerchantPolicyDto.ApiResponsePolicy> create(pb.merchant_policy.MerchantPolicyCommand.CreateMerchantPoliciesRequest body);
    Uni<MerchantPolicyDto.ApiResponsePolicy> update(String id, pb.merchant_policy.MerchantPolicyCommand.UpdateMerchantPoliciesRequest body);
    Uni<MerchantPolicyDto.ApiResponsePolicy> delete(String id);
    Uni<MerchantPolicyDto.ApiResponsePolicy> restore(String id);
    Uni<MerchantPolicyDto.SimpleResponse> deletePermanent(String id);
    Uni<MerchantPolicyDto.SimpleResponse> restoreAll();
    Uni<MerchantPolicyDto.SimpleResponse> deleteAllPermanent();
}
