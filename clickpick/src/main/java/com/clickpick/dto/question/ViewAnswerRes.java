package com.clickpick.dto.question;

import com.clickpick.domain.QuestionPost;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class ViewAnswerRes {
    private Long answerId;
    private Long questionId;
    private String userId;
    private String nickname;
    private String title;
    private String content;
    private LocalDateTime date;
    private String profileUrl;
    private List<ViewAnswerRes> reAnswer = new ArrayList<>();

    public ViewAnswerRes(QuestionPost questionPost) { // 답변, 추가 답변 (관리자)
        this.answerId = questionPost.getId();
        this.questionId = questionPost.getParent().getId();
        this.userId = questionPost.getAdmin().getId();
        this.nickname = "ADMIN"; // 관리자 닉네임 고정
        this.title = questionPost.getTitle();
        this.content = questionPost.getContent();
        this.date = questionPost.getCreateAt();
        this.profileUrl = "http://clickpick.iptime.org:8080/profile/images/admin.png"; // 관리자 프로필 이미지 고정
    }

    public ViewAnswerRes(QuestionPost questionPost, String userId) { // 추가 답변 (유저)
        this.answerId = questionPost.getId();
        this.questionId = questionPost.getParent().getId();
        this.userId = questionPost.getUser().getId();
        this.nickname = questionPost.getUser().getNickname();
        this.title = questionPost.getTitle();
        this.content = questionPost.getContent();
        this.date = questionPost.getCreateAt();
        this.profileUrl = questionPost.getUser().getProfileImage().getReturnUrl();
    }

    public void addReAnswer(ViewAnswerRes answer){
        this.reAnswer.add(answer);
    }

}
