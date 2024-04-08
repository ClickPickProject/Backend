package com.clickpick.service;

import com.clickpick.domain.Admin;
import com.clickpick.domain.QuestionPost;
import com.clickpick.domain.QuestionStatus;
import com.clickpick.domain.User;
import com.clickpick.dto.comment.ViewCommentRes;
import com.clickpick.dto.question.*;
import com.clickpick.repository.AdminRepository;
import com.clickpick.repository.QuestionPostRepository;
import com.clickpick.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
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
                return ResponseEntity.status(HttpStatus.OK).body("질문을 수정하였습니다.");
            }
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("사용자가 수정할 수 없는 질문입니다.");

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
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("사용자가 삭제할 수 없는 질문입니다.");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("존재하지 않는 이메일(아이디)입니다.");
    }

    /* 답변 작성 */
    @Transactional
    public ResponseEntity createAnswer(String userId, Long questionId, CreateAnswerReq createAnswerReq) {
        Optional<Admin> adminResult = adminRepository.findById(userId);
        if(adminResult.isPresent()){
            Optional<QuestionPost> questionResult = questionPostRepository.findById(questionId);
            if(questionResult.isPresent()){
                QuestionPost questionPost = new QuestionPost(adminResult.get(), createAnswerReq.getTitle(), createAnswerReq.getContent(), questionResult.get(), "ANSWER");
                questionPostRepository.save(questionPost);
                questionResult.get().changeComplete();
                return ResponseEntity.status(HttpStatus.OK).body("답변을 작성하였습니다.");
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("질문이 존재하지 않습니다.");
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("관리자만 사용가능한 기능입니다.");
    }

    /* 답변 삭제 */
    @Transactional
    public ResponseEntity deleteAnswer(String userId, Long answerId) {
        Optional<Admin> adminResult = adminRepository.findById(userId);
        if(adminResult.isPresent()){
            Optional<QuestionPost> questionResult = questionPostRepository.findAnswerAdmin(answerId, userId);
            if(questionResult.isPresent()){
                questionPostRepository.delete(questionResult.get());
                Optional<QuestionPost> parentQuestion = questionPostRepository.findById(questionResult.get().getParent().getId());
                parentQuestion.get().changeAwaiting();
                return ResponseEntity.status(HttpStatus.OK).body("답변을 삭제하였습니다.");
            }
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("삭제할 수 없는 답변입니다.");
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("관리자만 사용가능한 기능입니다.");
    }

    /* 답변 수정 */
    @Transactional
    public ResponseEntity renewAnswer(String userId, Long answerId, UpdateAnswerReq updateAnswerReq) {
        Optional<Admin> adminResult = adminRepository.findById(userId);
        if(adminResult.isPresent()){
            Optional<QuestionPost> questionResult = questionPostRepository.findAnswerAdmin(answerId, userId);
            if(questionResult.isPresent()){
                QuestionPost questionPost = questionResult.get();
                questionPost.changeQuestion(updateAnswerReq);
                return ResponseEntity.status(HttpStatus.OK).body("답변을 수정하였습니다.");
            }
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("수정할 수 없는 답변입니다.");

        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("관리자만 사용가능한 기능입니다.");
    }

    /* 질문 상세 확인 */
    public ResponseEntity selectQuestion(Long questionId) {
        Optional<QuestionPost> questionResult = questionPostRepository.findById(questionId);
        if(questionResult.isPresent() && questionResult.get().getParent() == null){ // 질문인 경우만, 답변이면 안됌
            ViewQuestionRes viewQuestionRes = new ViewQuestionRes(questionResult.get());
            Optional<List<QuestionPost>> answerResult = questionPostRepository.findAnswerQuestion(questionId);
            if(answerResult.isPresent()){
                for(QuestionPost questionPost : answerResult.get()){
                    ViewAnswerRes answer = new ViewAnswerRes(questionPost);
                    viewQuestionRes.addAnswer(answer);
                }
            }
            return ResponseEntity.status(HttpStatus.OK).body(viewQuestionRes);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("존재하지 않는 질문입니다.");

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
