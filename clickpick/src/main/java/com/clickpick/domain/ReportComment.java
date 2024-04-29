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
public class ReportComment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_comment_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_user_id",nullable = false)
    private User reportUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_user_id",nullable = false)
    private User reportedUser;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createAt; //신고된 날짜

    @Column(nullable = false)

    @Enumerated(EnumType.STRING)
    @ColumnDefault("'처리전'")
    private ReportStatus reportStatus;  //신고 처리관련 상태

    public void changeReportStatus(){   //service에서 상태변경할 필요없이 바로 변경
        this.reportStatus = ReportStatus.valueOf("처리");
    }

    private String reason;

    public ReportComment(Comment comment, User reportUser, User reportedUser, String reason) {
        this.comment = comment;
        this.reportUser = reportUser;
        this.reportedUser = reportedUser;
        this.reason = reason;
    }

    public void changeCommentNull(){
        this.comment = null;
    }

}
