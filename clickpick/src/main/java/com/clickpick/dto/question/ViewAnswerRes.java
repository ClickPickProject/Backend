package com.clickpick.dto.question;

import com.clickpick.domain.QuestionPost;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ViewAnswerRes {
    private Long answerId;
    private Long questionId;
    private String adminId;
    private String nickname;
    private String title;
    private String content;
    private LocalDateTime date;
    private String profileUrl;

    public ViewAnswerRes(QuestionPost questionPost) {
        this.answerId = questionPost.getId();
        this.questionId = questionPost.getParent().getId();
        this.adminId = questionPost.getAdmin().getId();
        this.nickname = "ADMIN"; // 관리자 닉네임 고정
        this.title = questionPost.getTitle();
        this.content = questionPost.getContent();
        this.date = questionPost.getCreateAt();
        this.profileUrl = "http://clickpick.iptime.org:8080/profile/images/admin.png"; // 관리자 프로필 이미지 고정
    }
}
