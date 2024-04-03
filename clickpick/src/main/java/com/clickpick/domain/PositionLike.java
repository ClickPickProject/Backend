package com.clickpick.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PositionLike {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "position_like_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

    @Column(nullable = false)
    private double xPosition;
    @Column(nullable = false)
    private double yPosition;

    @Enumerated(EnumType.STRING)
    @ColumnDefault("'LIKE'")
    private PositionStatus positionStatus;

    public PositionLike(User user, double xPosition, double yPosition, String positionStatus) {
        this.user = user;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.positionStatus = PositionStatus.valueOf(positionStatus);
    }
}

