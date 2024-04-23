package com.clickpick.controller;

import com.clickpick.dto.admin.CreateNoticeReq;
import com.clickpick.dto.admin.UpdateNoticeReq;
import com.clickpick.dto.post.UpdatePostReq;
import com.clickpick.service.NoticeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    /* 공지글 작성 */
    @PostMapping("/api/admin/notice")
    public ResponseEntity uploadNotice(@RequestBody @Valid CreateNoticeReq createNoticeReq){
        String adminId = SecurityContextHolder.getContext().getAuthentication().getName();
        ResponseEntity responseEntity = noticeService.createNotice(adminId, createNoticeReq);
        return  ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());
    }

    /* 공지글 삭제 */
    @DeleteMapping("/api/admin/notice/{noticeId}")
    public ResponseEntity eraseNotice(@PathVariable("noticeId") Long noticeId){
        String adminId = SecurityContextHolder.getContext().getAuthentication().getName();
        ResponseEntity responseEntity = noticeService.deleteNotice(noticeId, adminId);
        return ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());

    }

    /* 공지글 수정 */
    @PostMapping("/api/admin/notice/{noticeId}")
    public ResponseEntity updateNotice(@PathVariable("noticeId")Long noticeId, @RequestBody @Valid UpdateNoticeReq updateNoticeReq){
        String adminId = SecurityContextHolder.getContext().getAuthentication().getName();
        ResponseEntity responseEntity = noticeService.renewNotice(noticeId, adminId, updateNoticeReq);
        return ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());
    }

    /* 공지글 리스트 */
    @GetMapping("/api/notice/list")
    public ResponseEntity getNotice(@RequestParam(required = false, defaultValue = "0", value = "page")int page){
        ResponseEntity responseEntity = noticeService.getNotice(page);
        return ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());
    }

    /* 공지글 상세 조회 */
    @GetMapping("/api/notice/{noticeId}")
    public ResponseEntity viewPost(@PathVariable("noticeId")Long noticeId){
        ResponseEntity responseEntity = noticeService.selectNotice(noticeId);
        return ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());
    }


}
