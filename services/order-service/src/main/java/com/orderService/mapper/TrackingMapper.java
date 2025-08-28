package com.orderService.mapper;

import com.orderService.dto.response.TrackingHistoryDTO;
import com.orderService.dto.response.TrackingResponseDTO;
import com.orderService.entity.OrderTrackingEntity;
import com.orderService.entity.OrderTrackingHistoryEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TrackingMapper {

    TrackingResponseDTO toResponse(OrderTrackingEntity entity);

    TrackingHistoryDTO toHistoryDTO(OrderTrackingHistoryEntity entity);
}

