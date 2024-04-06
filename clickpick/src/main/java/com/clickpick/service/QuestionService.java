package com.clickpick.service;

import com.clickpick.domain.QuestionPost;
import com.clickpick.domain.QuestionStatus;
import com.clickpick.domain.User;
import com.clickpick.dto.question.CreateAnswerReq;
import com.clickpick.dto.question.CreateQuestionReq;
import com.clickpick.dto.question.UpdateAnswerReq;
import com.clickpick.dto.question.UpdateQuestionReq;
import com.clickpick.repository.AdminRepository;
import com.clickpick.repository.QuestionPostRepository;
import com.clickpick.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuestionService {

    private final QuestionPostRepository questionPostRepository;
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;


    /* 질문 작성 */
    @Transactional
    public ResponseEntity createQuestion(String userId, CreateQuestionReq createQuestionReq) {
        Optional<User> userResult = userRepository.findById(userId);
        if(userResult.isPresent()){
            QuestionPost questionPost = new QuestionPost(userResult.get(), createQuestionReq.getTitle(), createQuestionReq.getContent(), null);
            questionPostRepository.save(questionPost);
            return ResponseEntity.status(HttpStatus.OK).body("질문을 등록하였습니다.");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("존재하지 않는 이메일(아이디)입니다.");
    }

    /* 질문 수정 */
    @Transactional
    public ResponseEntity renewQuestion(String userId, Long questionId, UpdateQuestionReq updateQuestionReq) {
        Optional<User> userResult = userRepository.findById(userId);
        if(userResult.isPresent()){
            Optional<QuestionPost> questionResult = questionPostRepository.findQuestionUser(questionId, userId);
            // 답글 달리면 변경 x 추가
            if(questionResult.isPresent()){
                if(questionResult.get().getStatus().equals(QuestionStatus.COMPLETE)){
                    return ResponseEntity.status(HttpStatus.CONFLICT).body("답변이 등록된 질문은 변경할 수 없습니다.");
                }
                QuestionPost questionPost = questionResult.get();
                questionPost.changeQuestion(updateQuestionReq);
                return ResponseEntity.status(HttpStatus.OK).body("질문을 변경하였습니다.");
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("존재하지 않는 질문입니다.");

        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("존재하지 않는 이메일(아이디)입니다.");
    }

    /* 질문 삭제 */
    @Transactional
    public ResponseEntity deleteQuestion(String userId, Long questionId) {
        Optional<User> userResult = userRepository.findById(userId);
        if(userResult.isPresent()){
            Optional<QuestionPost> questionResult = questionPostRepository.findQuestionUser(questionId, userId);
            if(questionResult.isPresent()){
                questionPostRepository.delete(questionResult.get());
                return ResponseEntity.status(HttpStatus.OK).body("질문을 삭제하였습니다.");
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("존재하지 않는 질문입니다.");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("존재하지 않는 이메일(아이디)입니다.");
    }

    /* 답변 작성 */
    @Transactional
    public ResponseEntity createAnswer(String userId, Long questionId, CreateAnswerReq createAnswerReq) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("존재하지 않는 이메일(아이디)입니다.");
    }

    /* 답변 삭제 */
    @Transactional
    public ResponseEntity deleteAnswer(String userId, Long answerId) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("존재하지 않는 이메일(아이디)입니다.");
    }

    /* 답변 수정 */
    @Transactional
    public ResponseEntity renewAnswer(String userId, Long answerId, UpdateAnswerReq updateAnswerReq) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("존재하지 않는 이메일(아이디)입니다.");
    }

    /* 질문 상세 확인 */
    public ResponseEntity selectQuestion(Long questionId) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("존재하지 않는 이메일(아이디)입니다.");
    }

    /* 질문 목록 확인 */
    public ResponseEntity listQuestion(int page) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("존재하지 않는 이메일(아이디)입니다.");
    }

    /* 작성한 질문 목록 확인 */
    public ResponseEntity listMyQuestion(String userId, int page) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("존재하지 않는 이메일(아이디)입니다.");
    }

}
