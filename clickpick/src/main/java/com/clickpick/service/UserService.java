package com.clickpick.service;

import com.clickpick.config.RedisUtil;
import com.clickpick.domain.*;
import com.clickpick.dto.user.*;
import com.clickpick.repository.*;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final RedisUtil redisUtil;
    private final CommentRepository commentRepository;


    /* 회원 가입 */
    @Transactional
    public ResponseEntity join(SingUpReq singUpReq){
        String id = singUpReq.getId();
        Optional<User> result = userRepository.findById(id);
        if(result.isPresent()){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 가입된 ID 입니다.");
        }
        else{
            String encodedPassword = passwordEncoder.encode(singUpReq.getPassword());
            User user = new User(singUpReq.getId(), encodedPassword,singUpReq.getName(),singUpReq.getNickname(),singUpReq.getPhone());
            userRepository.save(user);
            return ResponseEntity.status(HttpStatus.OK).body("회원으로 가입되었습니다.");
        }

    }

    /* 아이디(이메일) 중복 확인 */
    public ResponseEntity checkId(String id) throws Exception {
        Optional<User> result = userRepository.findById(id);
        if(result.isPresent()){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 가입된 ID 입니다.");
        }
        else{
            mailService.sendMessage(id); //이메일(아이디) 인증 시 사용
            return ResponseEntity.status(HttpStatus.OK).body("인증번호를 해당 이메일로 발송하였습니다.");
        }

    }

    /* 닉네임 중복 확인 */
    public ResponseEntity checkNickname(String nickname){
        Optional<User> result = userRepository.findByNickname(nickname);
        if(result.isPresent()){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("존재하는 별명입니다.");
        }
        else{
            return ResponseEntity.status(HttpStatus.OK).body("사용할 수 있는 별명입니다.");
        }
    }

    /* 전화번호 중복 확인 */
    public ResponseEntity checkPhone(String phone) {
        Optional<User> result = userRepository.findByPhone(phone);
        if(result.isPresent()){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 가입된 전화번호 입니다.");
        }
        else {
            return ResponseEntity.status(HttpStatus.OK).body("사용할 수 있는 전화번호입니다.");
        }
    }

    /*로그인 체크 아이디 일치 && 비밀번호 일치*/
    public ResponseEntity checkLogin(LoginReq loginReq) {
        Optional<User> result = userRepository.findById(loginReq.getId());
        /* 아이디가 존재하는 지 */
        if(result.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("잘못된 아이디 또는 비밀번호 입니다.");
        }
        else{
            User chechUser = result.get();
            boolean matches = passwordEncoder.matches(loginReq.getPassword(), chechUser.getPassword());
            if (matches) {
               return ResponseEntity.status(HttpStatus.OK).body("로그인 되었습니다.");
            }
            else{
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("잘못된 아이디 또는 비밀번호 입니다.");
            }

        }
    }

    /* 아이디 찾기 */
    public ResponseEntity findId(FindIdReq findIdReq){
        Optional<User> result = userRepository.findByPhone(findIdReq.getPhone());
        if (result.isPresent()) {

            User user = result.get();

            if (findIdReq.getName().equals(user.getName())) {
                JsonObject jo = new JsonObject();
                jo.addProperty("id",user.getId());
                return ResponseEntity.status(HttpStatus.OK).body(jo.toString());
            }

        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("존재하지 않는 전화번호 및 이름입니다.");
    }

    /* 비밀번호 찾기 */
    public ResponseEntity findPassword(FindPwReq findPwReq) throws Exception {
        Optional<User> result = userRepository.findById(findPwReq.getId());
        if(result.isPresent()) {
            User user = result.get();
            if(user.getPhone().equals(findPwReq.getPhone()) && user.getName().equals(findPwReq.getName())){
                mailService.sendMessage(user.getId());
                return ResponseEntity.status(HttpStatus.OK).body("해당 이메일(아이디)로 인증번호를 발송하였습니다.");
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("존재하지 않는 이메일(아이디) 입니다.");

    }

    /* 인증코드 확인 */
    @Transactional
    public ResponseEntity checkVerificationCode(VerifyCodeReq verifyCodeReq){
        Optional<String> redisCodeOpt = Optional.ofNullable(redisUtil.getData(verifyCodeReq.getCode()));

        if(redisCodeOpt.isPresent()){
            String redisCode = redisCodeOpt.orElse("");
            
            if(redisCode.equals(verifyCodeReq.getId())) {
                redisUtil.deleteData(verifyCodeReq.getCode());
                return ResponseEntity.status(HttpStatus.OK).body("인증에 성공하였습니다.");
            }

        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("인증번호가 일치하지 않습니다.");


    }

    /* 비밀번호 변경 */
    @Transactional
    public ResponseEntity changeNewPassword(ChangePasswordReq changePasswordReq) {
        Optional<User> result = userRepository.findById(changePasswordReq.getId());
        if(result.isPresent()){
            User user = result.get();
            if(passwordEncoder.matches(changePasswordReq.getPassword(), user.getPassword())){
                return ResponseEntity.status(HttpStatus.CONFLICT).body("기존 비밀번호와 동일합니다.");
            }
            else {
                user.updatePassword(passwordEncoder.encode(changePasswordReq.getPassword()));
                return ResponseEntity.status(HttpStatus.OK).body("비밀번호를 변경하였습니다.");
            }

        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("존재하지 않는 이메일(아이디)입니다.");


    }

    /* 개인정보 확인 */
    public ResponseEntity checkUserInfo(String userId) {
        Optional<User> userResult = userRepository.findById(userId);
        if(userResult.isPresent()){
            User user = userResult.get();
            UserInfoRes userInfoRes = new UserInfoRes(user);
            return ResponseEntity.status(HttpStatus.OK).body(userInfoRes);
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("회원만 사용 가능한 기능입니다.");
    }

    /* 닉네임 변경 */
    @Transactional
    public ResponseEntity updateNewNickname(String userId, String nickname){
        Optional<User> userResult = userRepository.findById(userId);
        if(userResult.isPresent()){
            Optional<User> nicknameResult = userRepository.findByNickname(nickname);
            if(nicknameResult.isPresent()){
                return ResponseEntity.status(HttpStatus.CONFLICT).body("존재하는 닉네임입니다.");
            }
            User user = userResult.get();
            user.updateNickname(nickname);
            return ResponseEntity.status(HttpStatus.OK).body("닉네임이 변경되었습니다.");
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("회원만 사용 가능한 기능입니다.");
    }

    /* 전화번호 변경 */
    @Transactional
    public ResponseEntity updateNewPhoneNumber(String userId, String phone){
        Optional<User> userResult = userRepository.findById(userId);
        if(userResult.isPresent()){
            Optional<User> nicknameResult = userRepository.findByPhone(phone);
            if(nicknameResult.isPresent()){
                return ResponseEntity.status(HttpStatus.CONFLICT).body("존재하는 전화번호입니다.");
            }
            User user = userResult.get();
            user.updatePhone(phone);
            return ResponseEntity.status(HttpStatus.OK).body("전화번호가 변경되었습니다.");
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("회원만 사용 가능한 기능입니다.");

    }

    /* 회원 탈퇴 */
    @Transactional
    public ResponseEntity deleteUser(String userId){
        Optional<User> userResult = userRepository.findById(userId);
        if(userResult.isPresent()){
            User user = userResult.get();
            // 게시글은 연쇄 삭제, 댓글은 대댓글이 있는 경우 삭제된 사용자로 변경?
            Optional<List<Comment>> commentResult = commentRepository.findUserId(user.getId());
            if(commentResult.isPresent()){
                for(Comment comment : commentResult.get()){
                    if(comment.getParent() == null && comment.getComments().size() > 0){
                        Optional<User> deleteUser = userRepository.findById("delete@delete.com");
                        User tempUser = deleteUser.get();
                        comment.leaveUserComment(tempUser); // 대댓글 살리기 위함
                        continue;
                    }
                    commentRepository.delete(comment);
                }
            }
            userRepository.delete(user);


            return ResponseEntity.status(HttpStatus.OK).body("회원탈퇴가 완료되었습니다.");
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("회원만 사용 가능한 기능입니다.");
    }

}
