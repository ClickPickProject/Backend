package com.clickpick.controller;

import com.clickpick.dto.question.CreateAnswerReq;
import com.clickpick.dto.question.CreateQuestionReq;
import com.clickpick.dto.question.UpdateAnswerReq;
import com.clickpick.dto.question.UpdateQuestionReq;
import com.clickpick.service.QuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    /* 질문 작성 */
    @PostMapping("/api/member/question")
    public ResponseEntity uploadQuestion(@RequestBody @Valid CreateQuestionReq createQuestionReq){
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        ResponseEntity responseEntity = questionService.createQuestion(userId, createQuestionReq);
        return ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());
    }
    /* 질문 변경 */
    @PostMapping("/api/member/question/{questionId}")
    public ResponseEntity updateQuestion(@PathVariable("questionId")Long questionId, @RequestBody @Valid UpdateQuestionReq updateQuestionReq){
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        ResponseEntity responseEntity = questionService.renewQuestion(userId, questionId, updateQuestionReq);
        return ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());
    }
    /* 질문 삭제 */
    @DeleteMapping("/api/member/question/{questionId}")
    public ResponseEntity eraseQuestion(@PathVariable("questionId")Long questionId){
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        ResponseEntity responseEntity = questionService.deleteQuestion(userId, questionId);
        return ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());
    }

    /* 답변 작성 */
    @PostMapping("/api/admin/{questionId}/answer")
    public ResponseEntity uploadAnswer(@PathVariable("questionId")Long questionId, @RequestBody @Valid CreateAnswerReq createAnswerReq){
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        ResponseEntity responseEntity = questionService.createAnswer(userId,questionId, createAnswerReq);
        return ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());
    }

    /* 답변 수정 */
    @PostMapping("/api/admin/answer/{answerId}")
    public ResponseEntity updateAnswer(@PathVariable("answerId")Long answerId, @RequestBody @Valid UpdateAnswerReq updateAnswerReq){
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        ResponseEntity responseEntity = questionService.renewAnswer(userId, answerId, updateAnswerReq);
        return ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());
    }

    /* 답변 삭제 */
    @DeleteMapping("/api/admin/answer/{answerId}")
    public ResponseEntity eraseAnswer(@PathVariable("answerId")Long answerId){
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        ResponseEntity responseEntity = questionService.deleteAnswer(userId, answerId);
        return ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());
    }

    /* 질문 상세 확인 */
    @GetMapping("/api/question/{questionId}")
    public ResponseEntity viewQuestion(@PathVariable("questionId")Long questionId){
        ResponseEntity responseEntity = questionService.selectQuestion(questionId);
        return ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());
    }

    /* 질문 목록 확인 */
    @GetMapping("/api/question/list")
    public ResponseEntity viewQuestionList(@RequestParam(required = false, defaultValue = "0", value = "page")int page){
        ResponseEntity responseEntity = questionService.listQuestion(page);
        return ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());
    }

    /* 작성한 질문 목록 확인 */
    @GetMapping("api/member/question/list")
    public ResponseEntity viewMyQuestionList(@RequestParam(required = false, defaultValue = "0", value = "page")int page){
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        ResponseEntity responseEntity = questionService.listMyQuestion(userId, page);
        return ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());
    }


}
