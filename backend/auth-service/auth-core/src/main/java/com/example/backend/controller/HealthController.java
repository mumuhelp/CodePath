package com.example.backend.controller;

import com.example.auth.api.HealthApi;
import com.example.auth.dto.HealthStatusDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HealthController implements HealthApi {

    @Override
    public ResponseEntity<HealthStatusDTO> health() {
        HealthStatusDTO healthStatusDTO = new HealthStatusDTO();
        healthStatusDTO.setStatus("OK");
        healthStatusDTO.setMessage("Backend is running!");
        return ResponseEntity.ok(healthStatusDTO);
    }
}
