package com.clickpick.domain;

import com.clickpick.dto.question.UpdateAnswerReq;
import com.clickpick.dto.question.UpdateQuestionReq;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QuestionPost {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id")
    private Admin admin;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 50000)
    private String content;

    @Column(nullable = false)
    @CreationTimestamp
    private LocalDateTime createAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private QuestionPost parent;

    @OneToMany(mappedBy = "parent", orphanRemoval = true)
    private List<QuestionPost> answers = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @ColumnDefault("'AWAITING'")
    private QuestionStatus status;

    @Enumerated(EnumType.STRING)
    @ColumnDefault("'UNLOCK'")
    private QuestionLock lockStatus; // 질문 공개, 비공개

    public QuestionPost(User user, String title, String content, QuestionPost parent, String lock) { //질문 잓어
        this.user = user;
        this.title = title;
        this.content = content;
        this.parent = parent;
        this.lockStatus = QuestionLock.valueOf(lock);
    }

    public QuestionPost(User user, String title, String content, QuestionPost parent, QuestionStatus questionStatus){ // 추가 답변(사용자용)
        this.user = user;
        this.title = title;
        this.content = content;
        this.parent = parent;
        this.status = questionStatus;
    }

    public QuestionPost(Admin admin, String title, String content, QuestionPost parent, QuestionStatus questionStatus) { // 추가 답변(관리자용)
        this.admin = admin;
        this.title = title;
        this.content = content;
        this.parent = parent;
        this.status = questionStatus;
    }

    public void changeQuestion(UpdateQuestionReq updateQuestionReq){
        this.title = updateQuestionReq.getTitle();
        this.content = updateQuestionReq.getContent();
        this.lockStatus = QuestionLock.valueOf(updateQuestionReq.getLock());
    }

    public void changeQuestion(UpdateAnswerReq updateAnswerReq){
        this.title = updateAnswerReq.getTitle();
        this.content = updateAnswerReq.getContent();
    }

    public void changeComplete(){
        this.status = QuestionStatus.valueOf("COMPLETE");
    }

    public void changeAwaiting(){
        this.status = QuestionStatus.valueOf("AWAITING");
    }


}
