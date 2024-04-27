package com.clickpick.controller;

import com.clickpick.domain.User;
import com.clickpick.dto.admin.BanUserReq;
import com.clickpick.dto.admin.ChangePeriodReq;
import com.clickpick.dto.admin.SingUpAdminReq;
import com.clickpick.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    /* 매니저 회원 가입*/
    @PostMapping("/api/signup/admin")
    public ResponseEntity singUpAdmin(@RequestBody @Valid SingUpAdminReq singUpAdminReq){
        ResponseEntity responseEntity = adminService.join(singUpAdminReq);
        return ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());
    }

    /* 유저 리스트 확인 */
    @GetMapping("/api/admin/userlist")
    public ResponseEntity getUserList(@RequestParam(required = false, defaultValue = "0", value = "page")int page, @RequestParam(required = false, defaultValue = "all")String status){
        ResponseEntity responseEntity = adminService.getUserList(page,status);
        return ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());
    }

//    /* 유저 단일 확인 */
//    @GetMapping("/api/admin/manager/user/{userId}")
//    public ResponseEntity getUser(@PathVariable("userId") String userId) {
//        // 특정 유저를 가져오는 로직을 구현
//        ResponseEntity user = adminService.getUser(userId);
//        if (user == null) {
//            ResponseEntity responseEntity = ResponseEntity.notFound().build();
//            return ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());
//        }
//        ResponseEntity responseEntity = ResponseEntity.ok(user);
//        return ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());
//    }

    /* 신고 게시글리스트 */
    @GetMapping("/api/admin/reportpostlist")
    public ResponseEntity reportPostLIst(@RequestParam(required = false, defaultValue = "0", value = "page")int page){
        ResponseEntity responseEntity = adminService.getReportPostLIst(page);
        return ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());
    }

    /* 신고 댓글리스트 */
    @GetMapping("/api/admin/reportcommentlist")
    public ResponseEntity reportCommentLIst(@RequestParam(required = false, defaultValue = "0", value = "page")int page){
        ResponseEntity responseEntity = adminService.getReportCommentLIst(page);
        return ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());
    }



    /* 댓글 신고된 유저 정지 시킴 */
    @PostMapping("/api/admin/commentban")
    public ResponseEntity banUserReportedComment(@RequestBody @Valid BanUserReq banUserReq) {
        // 유저를 정지시키는 로직을 구현
        String adminId = SecurityContextHolder.getContext().getAuthentication().getName();
        ResponseEntity responseEntity = adminService.banUserReportedComment(adminId, banUserReq);
        return ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());
    }

    /* 게시물 신고된 유저 정지 시킴 */
    @PostMapping("/api/admin/postban")
    public ResponseEntity banUserReportedPost(@RequestBody @Valid BanUserReq banUserReq) {
        // 유저를 정지시키는 로직을 구현
        String adminId = SecurityContextHolder.getContext().getAuthentication().getName();
        ResponseEntity responseEntity = adminService.banUserReportedPost(adminId, banUserReq);
        return ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());
    }

    /* 정지 유저 리스트 */
    @GetMapping("/api/admin/banuserlist")
    public ResponseEntity getBanUsers(@RequestParam(required = false, defaultValue = "0", value = "page")int page) {
        // 정지된 유저 리스트를 가져오는 로직을 구현
        ResponseEntity responseEntity = adminService.banUserList(page);
        return ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());
    }

    /* 정지 유저 변경 -- 정지된 유저 리스트에서 정지된 상태를 정상 상태로 돌린다.즉, ban된 유저에서만 사용한다?  */
    @PostMapping("/api/admin/ban/{userid}")
    public ResponseEntity updateBanStatus(@PathVariable("userid") String userId) {
        // 정지된 유저의 상태를 변경하는 로직을 구현
        ResponseEntity responseEntity = adminService.updateBanStatus(userId);
        return ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());
    }

    /* 유저 정지 기간 변경 */
    @PostMapping("/api/admin/ban/period")
    public ResponseEntity changeBanUserPeriod(@RequestBody @Valid ChangePeriodReq changePeriodReq){
        ResponseEntity responseEntity = adminService.changePeriod(changePeriodReq);
        return ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());
    }

    /* 정지 유저 삭제 */
    @DeleteMapping("/api/admin/ban/{userId}")
    public ResponseEntity deleteBanUser(@PathVariable("userId") String userId){
        ResponseEntity responseEntity = adminService.dropBanUser(userId);
        return ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());
    }



//    /* (banUser table에서) 정지 유저 삭제 */
//    @DeleteMapping("/api/admin/manager/ban/{user_id}")
//    public ResponseEntity deleteBanUser(@PathVariable("user_id") Long userId) {
//        // 정지된 유저를 삭제하는 로직을 구현
//        adminService.deleteBannedUser(userId);
//        return ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());
//    }

}
