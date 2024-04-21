package com.clickpick.controller;

import com.clickpick.dto.user.*;
import com.clickpick.service.PostService;
import com.clickpick.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final PostService postService;

    /* 유저 회원 가입*/
    @PostMapping("/api/signup/user")
    public ResponseEntity singUpUser(@RequestBody @Valid SingUpReq singUpReq){
        ResponseEntity responseEntity = userService.join(singUpReq);
        return ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());
    }

    /* 아이디 중복 체크*/
    @GetMapping("/api/check/userid/{user_id}")
    public ResponseEntity duplicateId(@PathVariable("user_id")String userId) throws Exception {
        ResponseEntity responseEntity = userService.checkId(userId);
        return ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());

    }

    /* 닉네임 중복 체크 */
    @GetMapping("/api/check/nickname/{nickname}")
    public ResponseEntity duplicateNickname(@PathVariable("nickname")String nickname){
        ResponseEntity responseEntity = userService.checkNickname(nickname);
        return ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());
    }

    /* 전화번호 중복 체크 */
    @GetMapping("/api/check/phone/{phone}")
    public ResponseEntity duplicatePhone(@PathVariable("phone")String phone){
        ResponseEntity responseEntity = userService.checkPhone(phone);
        return ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());
    }

    /* 유저 로그인 */
    @PostMapping("/api/login")
    public ResponseEntity login(@RequestBody @Valid LoginReq loginReq){
        ResponseEntity responseEntity = userService.checkLogin(loginReq);
        return ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());
    }

    /* 아이디 찾기 */

   @PostMapping("/api/login/id")
   public ResponseEntity findUserId(@RequestBody @Valid FindIdReq findIdReq){
       ResponseEntity responseEntity = userService.findId(findIdReq);
       return ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());
   }

    /* 비밀번호 찾기 */
    @PostMapping("/api/login/password")
    public ResponseEntity findUserPassword(@RequestBody @Valid FindPwReq findPwReq) throws Exception {
        ResponseEntity responseEntity = userService.findPassword(findPwReq);
        return ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());
    }

    /* 인증코드 확인 */
    @PostMapping("/api/verification")
    public ResponseEntity verifyCode(@RequestBody @Valid VerifyCodeReq verifyCodeReq){
        ResponseEntity responseEntity = userService.checkVerificationCode(verifyCodeReq);
        return ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());

    }

    /* 비밀번호 변경 */
    @PostMapping("/api/login/new-password")
    public ResponseEntity changePassword(@RequestBody @Valid ChangePasswordReq changePasswordReq){
        ResponseEntity responseEntity = userService.changeNewPassword(changePasswordReq);
        return ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());
    }

    /* 개인정보확인 */
    @GetMapping("/api/member/userinfo")
    public ResponseEntity viewUserInfo(){
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        ResponseEntity responseEntity = userService.checkUserInfo(userId);
        return ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());
    }

    /* 닉네임 변경 */
    @GetMapping("/api/member/new-nickname/{nickname}")
    public ResponseEntity changeNickname(@PathVariable("nickname") String nickname){
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        ResponseEntity responseEntity = userService.updateNewNickname(userId, nickname);
        return ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());
    }

    /* 전화번호 변경 */
    @GetMapping("/api/member/new-phone-number/{phone-number}")
    public ResponseEntity changePhoneNumber(@PathVariable("phone-number") String phoneNumber){
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        ResponseEntity responseEntity = userService.updateNewPhoneNumber(userId, phoneNumber);
        return ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());
    }

    /* 회원 탈퇴 */
    @DeleteMapping("/api/member")
    public ResponseEntity leaveUser(){
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        ResponseEntity responseEntity = userService.deleteUser(userId);
        return ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());
    }

    /**/

    /* 작성한 게시글 리스트 조회 */
    @GetMapping("/api/member/post/list")
    public ResponseEntity viewMyPostList(@RequestParam(required = false, defaultValue = "0", value = "page")int page){
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        ResponseEntity responseEntity = postService.myPostList(page,userId);
        return ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());
    }

    /* 작성한 댓글 리스트 조회 */
    @GetMapping("/api/member/comment/list")
    public ResponseEntity viewMyCommentList(@RequestParam(required = false,defaultValue = "0", value = "page")int page){
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        ResponseEntity responseEntity = postService.myCommentList(page,userId);
        return ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());
    }

    /* 좋아요 한 게시글 확인 */
    @GetMapping("/api/member/liked/post/list")
    public ResponseEntity viewMyLikePostList(@RequestParam(required = false,defaultValue = "0", value = "page")int page){
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        ResponseEntity responseEntity = postService.myLikePostList(page,userId);
        return ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());
    }

    /* 좋아요 한 댓글 확인 */
    @GetMapping("/api/member/liked/comment/list")
    public ResponseEntity viewMyLikeCommentList(@RequestParam(required = false,defaultValue = "0", value = "page")int page){
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        ResponseEntity responseEntity = postService.myLikeCommentList(page,userId);
        return ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());
    }

}
