package com.clickpick.controller;

import com.clickpick.dto.map.LikedPositionReq;
import com.clickpick.dto.map.MarkerReq;
import com.clickpick.service.MapService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class MapController {

    private final MapService mapService;

    /* 지도 영역 게시글 마커 조회*/
    @PostMapping("/api/map/marker")
    private ResponseEntity getMarkers(@RequestBody @Valid MarkerReq markerReq){
        ResponseEntity responseEntity = mapService.findMarkers(markerReq);
        return ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());
    }

    /* 동일 좌표 게시글 조회 */
    @GetMapping("/api/map/post/{xPosition}/{yPosition}")
    public ResponseEntity viewPostList(@RequestParam(required = false, defaultValue = "0", value = "page")int page, @PathVariable("xPosition") double xPosition, @PathVariable("yPosition") double yPosition){
        ResponseEntity responseEntity = mapService.findPosts(page, xPosition, yPosition);
        return ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());
    }

    /* 장소 즐겨찾기 */
    @PostMapping("/api/member/map/bookmark")
    public ResponseEntity bookmark(@RequestBody @Valid LikedPositionReq likedPositionReq){
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        ResponseEntity responseEntity = mapService.bookmarkPosition(likedPositionReq, userId);
        return ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());
    }

    /* 해당 영역(지도) 즐겨찾기 목록 조회 */
    @PostMapping("/api/member/map/bookmark/list")
    public ResponseEntity viewBookmark(@RequestBody @Valid MarkerReq markerReq){
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        ResponseEntity responseEntity = mapService.viewBookmarkPosition(userId, markerReq);
        return ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());
    }

    /* 전체 즐겨찾기 목록 조회 */
    @GetMapping("/api/member/map/bookmark/list")
    public ResponseEntity viewBookmark(){
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        ResponseEntity responseEntity = mapService.viewALlBookmarkPosition(userId);
        return ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());
    }

}
