package com.clickpick.dto.map;

import com.clickpick.domain.Post;

public class MakerRes {

    private double xPosition;
    private double yPosition;
    private Long postId;

    public MakerRes(Post post) {
        this.xPosition = post.getXPosition();
        this.yPosition = post.getYPosition();
        this.postId = post.getId();
    }
}
