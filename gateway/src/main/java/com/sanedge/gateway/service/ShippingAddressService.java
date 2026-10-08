package com.sanedge.gateway.service;

import com.sanedge.gateway.dto.ShippingAddressDto;
import io.smallrye.mutiny.Uni;

public interface ShippingAddressService {
    Uni<ShippingAddressDto.ApiResponsePaginationAddress> findAll(int page, int size, String search);
    Uni<ShippingAddressDto.ApiResponseAddress> findByOrder(String orderId);
    Uni<ShippingAddressDto.ApiResponsePaginationAddress> findByActive(int page, int size, String search);
    Uni<ShippingAddressDto.ApiResponsePaginationAddress> findByTrashed(int page, int size, String search);
    Uni<ShippingAddressDto.ApiResponseAddress> findById(String id);
    Uni<ShippingAddressDto.ApiResponseAddress> create(pb.shipping_address.ShippingAddressCommand.CreateShippingAddressRequest body);
    Uni<ShippingAddressDto.ApiResponseAddress> update(String id, pb.shipping_address.ShippingAddressCommand.UpdateShippingAddressRequest body);
    Uni<ShippingAddressDto.ApiResponseAddress> delete(String id);
    Uni<ShippingAddressDto.ApiResponseAddress> restore(String id);
    Uni<ShippingAddressDto.SimpleResponse> deletePermanent(String id);
    Uni<ShippingAddressDto.SimpleResponse> restoreAll();
    Uni<ShippingAddressDto.SimpleResponse> deleteAllPermanent();
}
