package com.clickpick.controller;

import com.clickpick.dto.post.CreatePostReq;
import com.clickpick.dto.post.ReportPostReq;
import com.clickpick.dto.post.UpdatePostReq;
import com.clickpick.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;


    /* 게시글 작성 */
    @PostMapping("/api/member/post")
    public ResponseEntity uploadPost(@RequestBody @Valid CreatePostReq createPostReq){ // 위치정보, 해시태그 없으면 null
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        ResponseEntity responseEntity = postService.createPost(userId, createPostReq);
        return ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());
    }


    /* 게시글 삭제 */
    @DeleteMapping("/api/member/post/{postId}")
    public ResponseEntity erasePost(@PathVariable("postId")Long postId){ // 위치정보, 해시태그 없으면 null
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        ResponseEntity responseEntity = postService.deletePost(postId, userId);
        return ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());
    }

    /* 게시글 수정 */
    @PostMapping("/api/member/post/{postId}")
    public ResponseEntity updatePost(@PathVariable("postId")Long postId,@RequestBody @Valid UpdatePostReq updatePostReq){
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        ResponseEntity responseEntity = postService.renewPost(postId, userId, updatePostReq);
        return ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());
    }


    /* 게시글 상세 조회 */
    @GetMapping("/api/post/{postId}")
    public ResponseEntity viewPost(@PathVariable("postId")Long postId){
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        ResponseEntity responseEntity = postService.selectPost(userId, postId);
        return ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());
    }

    /* 게시글 좋아요 */
    @GetMapping("/api/member/likedpost/{postId}")
    public  ResponseEntity likePost(@PathVariable("postId")Long postId){
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        ResponseEntity responseEntity = postService.likeCount(userId,postId);
        return ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());
    }

    /* 게시글 리스트 조회 */
    @GetMapping("/api/post/list")
    public ResponseEntity viewPostList(@RequestParam(required = false, defaultValue = "0", value = "page")int page){
        ResponseEntity responseEntity = postService.listPost(page);
        return ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());
    }

    /* 베스트 게시글 리스트 조회 */
    @GetMapping("/api/post/list/best")
    public ResponseEntity viewBestPostList(){
        ResponseEntity responseEntity = postService.bestListPost();
        return ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());
    }

    /* 게시글 작성자의 닉네임 검색 */
    @GetMapping("/api/post/nickname")
    public ResponseEntity searchNickname(@RequestParam(required = false, defaultValue = "0", value = "page")int page, String nickname){
        ResponseEntity responseEntity = postService.findUserNickname(page, nickname);
        return ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());
    }

    /* 게시글 내용 검색 */
    @GetMapping("/api/post/content")
    public ResponseEntity searchContent(@RequestParam(required = false, defaultValue = "0", value = "page")int page, String content){
        ResponseEntity responseEntity = postService.findContent(page, content);
        return ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());
    }
    /* 게시글 제목 검색 */
    @GetMapping("/api/post/title")
    public ResponseEntity searchTitle(@RequestParam(required = false, defaultValue = "0", value = "page")int page, String title){
        ResponseEntity responseEntity = postService.findTitle(page, title);
        return ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());
    }
    /* 게시글 해시태그 검색 */
    @GetMapping("/api/post/hashtag")
    public ResponseEntity searchHashtag(@RequestParam(required = false, defaultValue = "0", value = "page")int page, String hashtag){
        ResponseEntity responseEntity = postService.findHashtag(page, hashtag);
        return ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());
    }

    /* 게시글 카테고리 정렬 */
    @GetMapping("/api/post/category")
    public ResponseEntity searchCategory(@RequestParam(required = false, defaultValue = "0", value = "page")int page, String category){
        ResponseEntity responseEntity = postService.findCategory(page, category);
        return ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());
    }

    /* 게시글 신고 */
    @PostMapping("/api/member/report/post")
    public ResponseEntity reportPost(@RequestBody @Valid ReportPostReq reportPostReq){
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        ResponseEntity responseEntity = postService.complainPost(userId, reportPostReq);
        return ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());
    }


}
