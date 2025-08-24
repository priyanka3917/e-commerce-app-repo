package com.orderService.feign;

import com.orderService.config.FeignConfig;
import com.orderService.dto.response.GetOrUpdateUserByIdResponseDTO;
import com.orderService.utils.GenericResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "${feign.user.service.name}", configuration = FeignConfig.class)
public interface UserServiceClient {

    @GetMapping("/{id}")
    public ResponseEntity<GenericResponse<GetOrUpdateUserByIdResponseDTO>> getUserById(@PathVariable UUID id);
}
