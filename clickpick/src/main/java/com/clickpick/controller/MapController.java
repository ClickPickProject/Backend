package com.clickpick.controller;

import com.clickpick.dto.map.MakerReq;
import com.clickpick.service.MapService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MapController {

    private final MapService mapService;

    @PostMapping("/api/map/maker")
    private ResponseEntity getMakers(@RequestBody @Valid MakerReq makerReq){
        ResponseEntity responseEntity = mapService.findMakers(makerReq);
        return ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());
    }

}
