package com.clickpick.controller;

import com.clickpick.dto.map.MarkerReq;
import com.clickpick.service.MapService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MapController {

    private final MapService mapService;

    @PostMapping("/api/map/marker")
    private ResponseEntity getMarkers(@RequestBody @Valid MarkerReq makerReq){
        ResponseEntity responseEntity = mapService.findMarkers(makerReq);
        return ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());
    }

}
