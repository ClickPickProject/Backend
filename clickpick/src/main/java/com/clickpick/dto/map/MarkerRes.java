package com.clickpick.dto.map;

import com.clickpick.domain.Post;
import lombok.Data;

@Data
public class MarkerRes {

    private double xPosition;
    private double yPosition;
    private String position;
    private Long postId;

    public MarkerRes(Post post) {
        this.xPosition = post.getXPosition();
        this.yPosition = post.getYPosition();
        this.postId = post.getId();
        this.position = post.getPosition();
    }
}
