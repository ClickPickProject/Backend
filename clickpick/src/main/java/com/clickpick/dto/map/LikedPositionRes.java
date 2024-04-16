package com.clickpick.dto.map;

import com.clickpick.domain.PositionLike;
import lombok.Data;

@Data
public class LikedPositionRes {
    private double xPosition;
    private double yPosition;
    private String status;
    private String name;
    private String address;
    private String category;
    private String homepage;

    public LikedPositionRes(PositionLike positionLike) {
        this.xPosition = positionLike.getXPosition();
        this.yPosition = positionLike.getYPosition();
        this.status = positionLike.getPositionStatus().toString();
        this.name = positionLike.getName();
        this.address = positionLike.getAddress();
        this.category = positionLike.getCategory();
        this.homepage = positionLike.getHomepage();
    }
}
