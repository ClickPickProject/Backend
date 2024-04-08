package com.clickpick.dto.question;

import com.clickpick.domain.QuestionPost;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class ViewQuestionRes {
    private Long questionId;
    private String userId;
    private String nickname;
    private String title;
    private String content;
    private String status;
    private LocalDateTime date;
    private List<ViewAnswerRes> answer = new ArrayList<>();

    public ViewQuestionRes(QuestionPost questionPost) {
        this.questionId = questionPost.getId();
        this.nickname = questionPost.getUser().getNickname();
        this.userId = questionPost.getUser().getId();
        this.title = questionPost.getTitle();
        this.content = questionPost.getContent();
        this.status = questionPost.getStatus().toString();
        this.date = questionPost.getCreateAt();
    }

    public void addAnswer(ViewAnswerRes viewAnswerRes){
        answer.add(viewAnswerRes);
    }
}
