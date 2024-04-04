package com.clickpick.dto.map;

import com.clickpick.domain.PositionLike;
import lombok.Data;

@Data
public class LikedPositionRes {
    private double xPosition;
    private double yPosition;
    private String status;

    public LikedPositionRes(PositionLike positionLike) {
        this.xPosition = positionLike.getXPosition();
        this.yPosition = positionLike.getYPosition();
        this.status = positionLike.getPositionStatus().toString();
    }
}
