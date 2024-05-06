package com.clickpick.service;

import com.clickpick.domain.*;
import com.clickpick.dto.admin.*;
import com.clickpick.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {

    private final AdminRepository adminRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ReportPostRepository reportPostRepository;
    private final BanUserRepository banUserRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final ReportCommentRepository reportCommentRepository;


    /* 회원 가입
     *  현재 아이디 중복 가입을 막기 위해 id 체크 하도록 설정됨
     *  Front에서 모든 중복 및 빈칸 체크 후 가입버튼 활성화 한다면 해당 함수에서 중복체크 삭제 요망
     * */
    @Transactional
    public ResponseEntity join(SingUpAdminReq singUpAdminReq){
        String id = singUpAdminReq.getId();
        Optional<Admin> result = adminRepository.findById(id);
        Optional<User> result2 = userRepository.findById(id);
        if(result.isPresent() && result2.isPresent()){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 가입된 ID 입니다.");
        }
        else{
            String encodedPassword = passwordEncoder.encode(singUpAdminReq.getPassword());
            Admin admin = new Admin(singUpAdminReq.getId(), encodedPassword,singUpAdminReq.getName(), singUpAdminReq.getPhone() );
            adminRepository.save(admin);
            return ResponseEntity.status(HttpStatus.OK).body("회원으로 가입되었습니다.");
        }
    }


    /* 유저 리스트 확인 */
    public ResponseEntity getUserList(int page, String status) {
        PageRequest pageRequest = PageRequest.of(page, 10, Sort.by(Sort.Direction.ASC,"createAt"));
        if(status.equals("all")){
            Page<User> pagingResult = userRepository.findAll(pageRequest);
            Page<ViewUserListRes> map = pagingResult.map(user -> new ViewUserListRes(user));
            return ResponseEntity.status(HttpStatus.OK).body(map);
        }
        else{
            if(isEnumValue(status.toUpperCase())){
                return ResponseEntity.status(HttpStatus.CONFLICT).body("검색하고자 하는 유저의 상태가 올바르지 않습니다.");
            }
            Page<User> pagingResult = userRepository.findStatus(UserStatus.valueOf(status.toUpperCase()), pageRequest);
            Page<ViewUserListRes> map = pagingResult.map(user -> new ViewUserListRes(user));

            return ResponseEntity.status(HttpStatus.OK).body(map);
        }

    }

    /* 게시글 신고된 유저 정지 시키기 + 신고사유와 정지기간 설정 추가해야함.*/
    @Transactional
    public ResponseEntity banUserReportedPost(String adminId, BanUserReq banUserReq) {

        //현재 로그인된 유저 가져오기(매니저)
        Optional<Admin> adminResult = adminRepository.findById(adminId);
        Admin admin = adminResult.get();

        //reportPostRepository에서 sql검색으로 인한 값인 = 신고된 유저를 가져온
        Optional<ReportPost> reportedUserID = reportPostRepository.findById(banUserReq.getReportId());
        ReportPost reportPost = reportedUserID.get();
        //위에서 일치해서 존재한다면 아래를 실행
        if (reportedUserID.isPresent()) {

            //User 테이블에 위에서 검증한(ReportPost) 유저와 동일하다면 User테이블의 userid를 가져온다.
            Optional<User> findbyId = userRepository.findById(reportedUserID.get().getReportedUser().getId());
            User user = findbyId.get();
            //System.out.println("user11 = " + user);

            if(user.getStatus() == UserStatus.BAN){
                Optional<BanUser> banUserResult = banUserRepository.findBanUserId(user.getId());
                BanUser banUser = banUserResult.get();
                banUser.changePeriod(banUserReq.getBanDays());
                banUser.changeReason(banUserReq.getReason());

                postRepository.delete(reportedUserID.get().getPost());
                reportPost.changeReportStatus();
                reportPost.changePostNull();

                return ResponseEntity.ok("정지 기간을 연장하였습니다.");
            }
            else{
                user.changeStatus(UserStatus.BAN); // 유저를 정지시킴  --userRepository.save(user); // 변경 사항을 저장 <-- 안해도됨.

                //정지 처리를 한 후 BanUser 테이블에 생성.
                BanUser banUser = new BanUser(user,admin, LocalDateTime.now().plusDays(banUserReq.getBanDays()),banUserReq.getReason());
                banUserRepository.save(banUser);
                user.changeStatus(UserStatus.valueOf("BAN"));
                //정지 처리를 한 후 reportPost table에서는 처리완료 상태로 변경하기
                reportPost.changeReportStatus();
                postRepository.delete(reportPost.getPost());
                reportPost.changePostNull();



                return ResponseEntity.ok("사용자를 정지 시켰습니다.");
            }

        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자를 찾을 수 없습니다.");
        }
    }

    /* 댓글신고된 유저 정지 시키기 + 신고사유와 정지기간 설정 추가해야함.*/
    @Transactional
    public ResponseEntity banUserReportedComment(String adminId, BanUserReq banUserReq) {

        //현재 로그인된 유저 가져오기(매니저)
        Optional<Admin> adminResult = adminRepository.findById(adminId);
        Admin admin = adminResult.get();

        Optional<ReportComment> reportedUserID = reportCommentRepository.findById(banUserReq.getReportId());
        ReportComment reportComment = reportedUserID.get();
        //위에서 일치해서 존재한다면 아래를 실행
        if (reportedUserID.isPresent()) {

            Optional<User> findbyId = userRepository.findById(reportedUserID.get().getReportedUser().getId());
            User user = findbyId.get();

            if(user.getStatus() == UserStatus.BAN){
                Optional<BanUser> banUserResult = banUserRepository.findBanUserId(user.getId());
                BanUser banUser = banUserResult.get();
                banUser.changePeriod(banUserReq.getBanDays());
                banUser.changeReason(banUserReq.getReason());

                Comment comment = reportComment.getComment();
                comment.tempReport();
                reportComment.changeReportStatus();

                return ResponseEntity.ok("정지 기간을 연장하였습니다.");
            }
            else{
                user.changeStatus(UserStatus.BAN); // 유저를 정지시킴  --userRepository.save(user); // 변경 사항을 저장 <-- 안해도됨.

                //정지 처리를 한 후 BanUser 테이블에 생성.
                BanUser banUser = new BanUser(user,admin, LocalDateTime.now().plusDays(banUserReq.getBanDays()),banUserReq.getReason());
                banUserRepository.save(banUser);
                user.changeStatus(UserStatus.valueOf("BAN"));
                //정지 처리를 한 후 reportPost table에서는 처리완료 상태로 변경하기
                if(reportComment.getComment().getComments().size() > 0){
                    reportComment.changeReportStatus();
                    Comment comment = reportComment.getComment();
                    comment.tempReport();
                }
                else{
                    commentRepository.delete(reportComment.getComment());
                    reportComment.changeCommentNull();

                }

                return ResponseEntity.ok("사용자를 정지 시켰습니다.");
            }

        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자를 찾을 수 없습니다.");
        }

    }


    /* 정지 유저 리스트 */
    public ResponseEntity banUserList(int page) {
        PageRequest pageRequest = PageRequest.of(page, 10, Sort.by(Sort.Direction.ASC,"startDate"));
        Page<BanUser> banUserResult = banUserRepository.findAll(pageRequest);
        Page<ViewBanUserListRes> map = banUserResult.map(banUser -> new ViewBanUserListRes(banUser));

        return ResponseEntity.status(HttpStatus.OK).body(map);
    }

    /* 정지 유저 변경 */
    @Transactional
    public ResponseEntity updateBanStatus(String userId) {
        Optional<User> userResult = userRepository.findById(userId);

        if (userResult.isPresent()) {
            User user = userResult.get();
            user.changeStatus(UserStatus.NORMAL); // 혹은 다른 상태로 변경할 수 있음
            userRepository.save(user);
            return ResponseEntity.status(HttpStatus.OK).body("사용자의 정지 상태를 변경했습니다.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 ID를 가진 사용자를 찾을 수 없습니다.");
        }
    }

    public ResponseEntity getReportPostLIst(int page) {
        PageRequest pageRequest = PageRequest.of(page, 10, Sort.by(Sort.Direction.ASC, "createAt"));
        Page<ReportPost> pagingResult = reportPostRepository.findAll(pageRequest);
        Page<ViewReportPostRes> map = pagingResult.map(reportPost -> new ViewReportPostRes(reportPost));

        return ResponseEntity.status(HttpStatus.OK).body(map);

    }

    public ResponseEntity getReportCommentLIst(int page) {
        PageRequest pageRequest = PageRequest.of(page, 10, Sort.by(Sort.Direction.ASC, "createAt"));
        Page<ReportComment> pagingResult = reportCommentRepository.findAll(pageRequest);
        Page<ViewReportCommentRes> map = pagingResult.map(reportComment -> new ViewReportCommentRes(reportComment));

        return ResponseEntity.status(HttpStatus.OK).body(map);
    }

    /* 유저 정지 기간 변경 */
    @Transactional
    public ResponseEntity changePeriod(ChangePeriodReq changePeriodReq) {
        Optional<BanUser> banUserResult = banUserRepository.findBanUserId(changePeriodReq.getUserId());
        if(banUserResult.isPresent()){
            BanUser banUser = banUserResult.get();
            banUser.changePeriod(changePeriodReq.getDays());

            return ResponseEntity.status(HttpStatus.OK).body("정지 기간을 변경하였습니다.");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("정지 유저에 존재하지 않습니다.");

    }



    /* 정지 유저 삭제 */
    @Transactional
    public ResponseEntity dropBanUser(String userId){
        Optional<BanUser> banUserResult = banUserRepository.findBanUserId(userId);
        if(banUserResult.isPresent()){
            User user = banUserResult.get().getUser();
            user.changeStatus(UserStatus.valueOf("NORMAL"));
            banUserRepository.delete(banUserResult.get());

            return ResponseEntity.status(HttpStatus.OK).body("정지 유저에서 삭제하였습니다.");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("정지 유저에 존재하지 않습니다.");
    }

    public ResponseEntity countMember(Long year){
        List<Map<String, Object>> monthlyUserCounts = userRepository.countUsersByMonth(year);

        // HTTP 응답으로 월별 사용자 카운트 결과를 반환
        return ResponseEntity.status(HttpStatus.OK).body(monthlyUserCounts);
    }





    public static boolean isEnumValue(String status) {
        try {
            // Enum.valueOf() 메서드를 사용하여 입력값이 Enum 타입에 속하는지 확인
            UserStatus userStatus = Enum.valueOf(UserStatus.class, status);
            return false; // 속한다면 false 반환
        } catch (IllegalArgumentException e) {
            return true; // 속하지 않는다면 true 반환
        }
    }



}
