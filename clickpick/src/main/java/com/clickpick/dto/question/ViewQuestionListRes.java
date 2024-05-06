package com.clickpick.dto.question;

import com.clickpick.domain.QuestionPost;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ViewQuestionListRes {
    private Long questionId;
    private String userId;
    private String nickname;
    private String title;
    private LocalDateTime createAt;
    private String status;
    private String profileUrl;
    private String lockStatus;

    public ViewQuestionListRes(QuestionPost questionPost) {
        this.questionId = questionPost.getId();
        this.userId = questionPost.getUser().getId();
        this.nickname = questionPost.getUser().getNickname();
        this.title = questionPost.getTitle();
        this.createAt = questionPost.getCreateAt();
        this.status = questionPost.getStatus().toString();
        this.lockStatus = questionPost.getLockStatus().toString();
        if(questionPost.getUser().getProfileImage() == null){
            this.profileUrl = "http://clickpick.iptime.org:8080/profile/images/default.png";
        }
        else {
            this.profileUrl = questionPost.getUser().getProfileImage().getReturnUrl();
        }
    }
}
