package com.clickpick.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.*;

import java.time.LocalDateTime;

@Entity
@Getter
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReportPost {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_post_id")
    private Long id;    //컬럼 id

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_user_id",nullable = false)
    private User reportUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_user_id",nullable = false)
    private User reportedUser;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Post post;
  
    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createAt;   //신고된 날짜

    @Enumerated(EnumType.STRING)
    @ColumnDefault("'처리전'")
    private ReportStatus reportStatus;  //신고 처리관련 상태

    private String reason;

    public ReportPost(User reportUser, User reportedUser, Post post, String reason) {
        this.reportUser = reportUser;
        this.reportedUser = reportedUser;
        this.post = post;
        this.reason = reason;
    }

    public void changeReportStatus(){   //service에서 상태변경할 필요없이 바로 변경
        this.reportStatus = ReportStatus.valueOf("처리");
    }

    public void changePost(){
        this.post = null;
    }
}