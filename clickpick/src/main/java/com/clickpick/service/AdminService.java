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

//    /* --- 유저 단일 확인 */
//    @Transactional
//    public ResponseEntity getUser(String userId) {
//        Optional<User> userResult = userRepository.findById(userId);
//        if(userResult.isPresent()){
//            User user = userResult.get();
//            //비밀번호를 제외하고 나오게 하는데.. 그러면 ViewUserListReq랑 다를게 없는데!?
//            //내일 물어봐야겠따..
//            //유저의 게시물? 댓글?
//            //ViewUserReq viewUserReq = new ViewUserReq(user.getId(),)
////            ViewUserReq viewUserReq = UserMapper.toViewUserReq(user);
//
//            return ResponseEntity.status(HttpStatus.OK).body(user);
//        }
//        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("유저를 찾을 수 없습니다.");
//    }


    /* 게시글 신고된 유저 정지 시키기 + 신고사유와 정지기간 설정 추가해야함.*/
    @Transactional
    public ResponseEntity banUserReportedPost(String adminId, BanUserReq banUserReq) {

        //현재 로그인된 유저 가져오기(매니저)
        Optional<Admin> adminResult = adminRepository.findById(adminId);
        Admin admin = adminResult.get();

        //reportPostRepository에서 sql검색으로 인한 값인 = 신고된 유저를 가져온
        Optional<ReportPost> reportedUserID = reportPostRepository.findById(banUserReq.getReportPostId());
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
                user.changeStatus(UserStatus.valueOf("BAN"));
                banUser.changeReason(banUserReq.getReason());

                postRepository.delete(reportedUserID.get().getPost());
                reportPost.changeReportStatus();
                reportPost.changePost();

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
                reportPost.changePost();



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

        //reportCommentRepository에서 sql검색으로 인한 값인 = 신고된 유저를 가져온
        Optional<ReportComment> reportedUserID = reportCommentRepository.findReportedUserID(banUserReq.getReportedUserId());
        //System.out.println("reportedUserId.get() = " + reportedUserId.get());

        //위에서 일치해서 존재한다면 아래를 실행
        if (reportedUserID.isPresent()) {

            //User 테이블에 위에서 검증한(ReportComment) 유저와 동일하다면 User테이블의 userid를 가져온다.
            Optional<User> findbyId = userRepository.findById(reportedUserID.get().getReportedUser().getId());
            User user = findbyId.get();
            //System.out.println("user11 = " + user);

            //usertable에서 상태를 정지로 변경
            user.changeStatus(UserStatus.BAN); // 유저를 정지시킴  --userRepository.save(user); // 변경 사항을 저장 <-- 안해도됨.

            //정지 처리를 한 후 BanUser 테이블에 생성.
            BanUser banUser = new BanUser(user,admin,LocalDateTime.now().plusDays(banUserReq.getBanDays()),banUserReq.getReason());
            banUserRepository.save(banUser);

            //정지 처리를 한 후 reportComment table에서는 처리완료 상태로 변경하기
            //@Transactional 안적어주어서 '처리'로 변경이 안되었다..
            ReportComment reportComment = reportedUserID.get();
            reportComment.changeReportStatus();

            return ResponseEntity.ok("사용자를 정지 시켰습니다.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자를 찾을 수 없습니다. ");
        }
    }


    //ban이 된 유저는 그냥 user에서 report으로 복사 된다.
    //아 report는 유저끼리 신고 된것이고.
    //여기서는 그냥 If문으로 정지 된것만 불러오면 되겠다.
    /* 정지 유저 리스트 */
    public ResponseEntity banUserList(int page) {
        PageRequest pageRequest = PageRequest.of(page, 10, Sort.by(Sort.Direction.ASC,"startDate"));
        Page<BanUser> banUserResult = banUserRepository.findAll(pageRequest);
        Page<ViewBanUserListRes> map = banUserResult.map(banUser -> new ViewBanUserListRes(banUser));

        return ResponseEntity.status(HttpStatus.OK).body(map);
    }

    // 왜 안될까? 인증 되었다가 post할려니 인증이 안되었다니....
    // 이유를 전혀 모르겠는데;;;;;;
    //뭐가 문제였는지는 모르겠다..
    //아무튼 정지일때 정상으로 변경하는 것.
    /* 정지 유저 변경 */
    @Transactional
    public ResponseEntity updateBanStatus(String userId) {
        //Optional<User> userResult = userRepository.findById(userId);
        //그냥 하면 에러남.. @에러 에러나는 듯. 아래와 같이 해줘야한다? 엥 아니네 아까는 안되었는데 지금은 잘만되는데;;
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
