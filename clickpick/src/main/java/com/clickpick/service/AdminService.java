package com.clickpick.service;

import com.clickpick.domain.*;
import com.clickpick.dto.admin.BanUserReq;
import com.clickpick.dto.admin.SingUpAdminReq;
import com.clickpick.dto.admin.ViewBanUserListReq;
import com.clickpick.dto.admin.ViewUserListReq;
import com.clickpick.repository.AdminRepository;
import com.clickpick.repository.BanUserRepository;
import com.clickpick.repository.ReportPostRepository;
import com.clickpick.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
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

    @Transactional
    /* 유저 리스트 확인 */
    public ResponseEntity getUserList(int page) {
        PageRequest pageRequest = PageRequest.of(page, 10, Sort.by(Sort.Direction.ASC,"createAt"));
        Page<User> pagingResult = userRepository.findAll(pageRequest);
        Page<ViewUserListReq> map = pagingResult.map(user -> new ViewUserListReq(user));

        return ResponseEntity.status(HttpStatus.OK).body(map);
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


    /* 유저 정지 시키기 */
    @Transactional
    public ResponseEntity banUser(String adminId, BanUserReq banUserReq) {

        //현재 로그인된 유저 가져오기(매니저)
        Optional<Admin> adminResult = adminRepository.findById(adminId);
        Admin admin = adminResult.get();

        //reportPostRepository에서 sql검색으로 인한 값인 = 신고된 유저를 가져온
        Optional<ReportPost> reportedUserID = reportPostRepository.findReportedUserID(banUserReq.getReportedUserId());
        //System.out.println("reportedUserId.get() = " + reportedUserId.get());

        //위에서 일치해서 존재한다면 아래를 실행
        if (reportedUserID.isPresent()) {

            //User 테이블에 위에서 검증한(ReportPost) 유저와 동일하다면 User테이블의 userid를 가져온다.
            Optional<User> findbyId = userRepository.findById(reportedUserID.get().getReportedUser().getId());
            User user = findbyId.get();
            //System.out.println("user11 = " + user);

            //usertable에서 상태를 정지로 변경
            user.changeStatus(UserStatus.BAN); // 유저를 정지시킴  --userRepository.save(user); // 변경 사항을 저장 <-- 안해도됨.

            //정지 처리를 한 후 BanUser 테이블에 생성.
            BanUser banUser = new BanUser(user,admin,banUserReq.getEndDate(),banUserReq.getReason());
            banUserRepository.save(banUser);

            //정지 처리를 한 후 reportPost table에서는 처리완료 상태로 변경하기
            ReportPost reportPost = reportedUserID.get();
            reportPost.changeReportStatus();

            return ResponseEntity.ok("사용자를 정지 시켰습니다.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자를 찾을 수 없습니다. ");
        }
    }


    //ban이 된 유저는 그냥 user에서 report으로 복사 되는 건가? 테이블 관계성에 대해서 모르겠다.
    //이동하게 된다면 다른 곳에서 유저 관련된 모든 기능에서
    // 상태만 변경한다면 report라는 새로운 테이블을 만들 필요가 있나?
    //아 report는 유저끼리 신고 된것이고.
    // 여기서는 그냥 If문으로 정지 된것만 불러오면 되겠다.
    //ban user는 다른 report로 이동되는데.... 이것들도 물어 봐야겠네;;
    /* 정지 유저 리스트 */
//    @Transactional
//    public ResponseEntity banUserList(int page) {
//        PageRequest pageRequest = PageRequest.of(page, 10, Sort.by(Sort.Direction.ASC,"createAt"));
//        Page<User> pagingResult = userRepository.findByStatus(UserStatus.BAN, pageRequest);
//        Page<ViewBanUserListReq> map = pagingResult.map(user -> new ViewBanUserListReq(user));
//
//        return ResponseEntity.status(HttpStatus.OK).body(map);
//    }

    // 왜 안될까? 인증 되었다가 post할려니 인증이 안되었다니....
    // 이유를 전혀 모르겠는데;;;;;;
    //뭐가 문제였는지는 모르겠다..
    //아무튼 정지일때 정상으로 변경하는 것.
    /* 정지 유저 변경 */
//    @Transactional
//    public ResponseEntity updateBanStatus(String userId) {
//        //Optional<User> userResult = userRepository.findById(userId);
//        //그냥 하면 에러남.. @에러 에러나는 듯. 아래와 같이 해줘야한다? 엥 아니네 아까는 안되었는데 지금은 잘만되는데;;
//        Optional<User> userResult = userRepository.findById(userId);
//
//        if (userResult.isPresent()) {
//            User user = userResult.get();
//            user.changeStatus(UserStatus.NORMAL); // 혹은 다른 상태로 변경할 수 있음
//            userRepository.save(user);
//            return ResponseEntity.status(HttpStatus.OK).body("사용자의 정지 상태를 변경했습니다.");
//        } else {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 ID를 가진 사용자를 찾을 수 없습니다.");
//        }
//    }



    /* 정지 유저 단일?? 정지 사유, 정지 게시물 */
}
