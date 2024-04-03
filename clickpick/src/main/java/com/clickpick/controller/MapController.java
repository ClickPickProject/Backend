package com.clickpick.controller;

import com.clickpick.dto.map.MarkerReq;
import com.clickpick.service.MapService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class MapController {

    private final MapService mapService;

    /* 지도 영역 게시글 마커 조회*/
    @PostMapping("/api/map/marker")
    private ResponseEntity getMarkers(@RequestBody @Valid MarkerReq makerReq){
        ResponseEntity responseEntity = mapService.findMarkers(makerReq);
        return ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());
    }

    /* 동일 좌표 게시글 조회 */
    @GetMapping("/api/map/post/{xPosition}/{yPosition}")
    public ResponseEntity viewPostList(@RequestParam(required = false, defaultValue = "0", value = "page")int page, @PathVariable("xPosition") double xPosition, @PathVariable("yPosition") double yPosition){
        ResponseEntity responseEntity = mapService.findPosts(page, xPosition, yPosition);
        return ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());
    }

}
