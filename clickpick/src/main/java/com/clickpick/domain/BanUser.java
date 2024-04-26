package com.clickpick.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDateTime;

@Entity
@Getter
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BanUser {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ban_user_id") //순서
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",nullable = false)  //정지된 유저 아이디
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id",nullable = false)  //관리자
    private Admin admin;

    @CreationTimestamp
    @Column(name = "start_date",nullable = false)  //post 할때 자동으로 들어감.
    private LocalDateTime startDate;

    @Column(name = "end_date",nullable = false)  //날짜 지정
    private LocalDateTime endDate;

    @Column(nullable = false)
    private String reason;  //사유

    public BanUser(User user, Admin admin, LocalDateTime endDate, String reason) {
        this.user = user;
        this.admin = admin;
        this.endDate = endDate;
        this.reason = reason;
    }

    public void plusPeriod(Long days){
        this.endDate = endDate.plusDays(days);
    }

    public void changeReason(String reason){
        this.reason = reason;
    }

}
