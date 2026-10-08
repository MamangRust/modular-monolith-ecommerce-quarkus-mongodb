package com.sanedge.gateway.service;

import com.sanedge.gateway.dto.SliderDto;
import io.smallrye.mutiny.Uni;

public interface SliderService {
    Uni<SliderDto.ApiResponsePaginationSlider> findAll(int page, int size, String search);
    Uni<SliderDto.ApiResponsePaginationSlider> findByActive(int page, int size, String search);
    Uni<SliderDto.ApiResponsePaginationSlider> findByTrashed(int page, int size, String search);
    Uni<SliderDto.ApiResponseSlider> findById(String id);
    Uni<SliderDto.ApiResponseSlider> create(pb.slider.SliderCommand.CreateSliderRequest body);
    Uni<SliderDto.ApiResponseSlider> update(String id, pb.slider.SliderCommand.UpdateSliderRequest body);
    Uni<SliderDto.ApiResponseSlider> delete(String id);
    Uni<SliderDto.ApiResponseSlider> restore(String id);
    Uni<SliderDto.SimpleResponse> deletePermanent(String id);
    Uni<SliderDto.SimpleResponse> restoreAll();
    Uni<SliderDto.SimpleResponse> deleteAllPermanent();
}
