package com.clickpick.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PositionLike {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "position_like_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @Column(nullable = false)
    private double xPosition;
    @Column(nullable = false)
    private double yPosition;

    @Enumerated(EnumType.STRING)
    @ColumnDefault("'LIKE'")
    private PositionStatus positionStatus;

    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String address;
    private String category;
    private String homepage;

    public PositionLike(User user, double xPosition, double yPosition, String positionStatus, String name, String address, String category, String homepage) {
        this.user = user;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.positionStatus = PositionStatus.valueOf(positionStatus);
        this.name = name;
        this.address = address;
        this.category = category;
        this.homepage = homepage;
    }
}

