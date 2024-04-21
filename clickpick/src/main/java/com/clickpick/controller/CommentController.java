package com.clickpick.controller;

import com.clickpick.dto.comment.CreateCommentReq;
import com.clickpick.dto.comment.CreateReCommentReq;
import com.clickpick.dto.comment.ReportCommentReq;
import com.clickpick.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    /* 댓글 작성 */
    @PostMapping("/api/member/comment")
    public ResponseEntity uploadComment(@RequestBody @Valid CreateCommentReq createCommentReq){ // 위치정보, 해시태그 없으면 null
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        ResponseEntity responseEntity = commentService.createComment(userId, createCommentReq);
        return ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());
    }

    /* 댓글 삭제 */
    @DeleteMapping("/api/member/comment/{commentId}")
    public ResponseEntity erasePost(@PathVariable("commentId")Long commentId){ // 위치정보, 해시태그 없으면 null
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        ResponseEntity responseEntity = commentService.deleteComment(userId, commentId);
        return ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());
    }

    /* 댓글 수정 */
    @PostMapping("/api/member/comment/{commentId}")
    public ResponseEntity updatePost(@PathVariable("commentId")Long commentId, @RequestBody @Valid CreateCommentReq updateCommentReq){ // 위치정보, 해시태그 없으면 null
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        ResponseEntity responseEntity = commentService.renewComment(userId, commentId, updateCommentReq);
        return ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());
    }

    /* 게시글 좋아요 */
    @GetMapping("/api/member/likedcomment/{commentId}")
    public  ResponseEntity likeComment(@PathVariable("commentId")Long commentId){
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        ResponseEntity responseEntity = commentService.likeCount(userId,commentId);
        return ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());
    }

    /* 대댓글 작성 */
    @PostMapping("/api/member/recomment")
    public ResponseEntity uploadReComment(@RequestBody @Valid CreateReCommentReq createReCommentReq){
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        ResponseEntity responseEntity = commentService.renewReComment(userId, createReCommentReq);
        return ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());
    }

    /* 댓글 신고*/
    @PostMapping("/api/member/report/comment")
    public ResponseEntity reportComment(@RequestBody @Valid ReportCommentReq reportCommentReq){
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        ResponseEntity responseEntity = commentService.complainComment(userId, reportCommentReq);
        return ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());
    }
}
