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

    public ViewAnswerRes(QuestionPost questionPost) {
        this.answerId = questionPost.getId();
        this.questionId = questionPost.getParent().getId();
        this.adminId = questionPost.getAdmin().getId();
        this.nickname = "ADMIN";
        this.title = questionPost.getTitle();
        this.content = questionPost.getContent();
        this.date = questionPost.getCreateAt();
    }
}
