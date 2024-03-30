package com.clickpick.dto.post;

import com.clickpick.domain.Hashtag;
import com.clickpick.domain.Post;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class ViewBestPostListRes {

    private Long postId;
    private String nickname;
    private String title;
    private LocalDateTime createAt;
    private Long viewCount;
    private Long likeCount;
    private List<String> hashtags;
    private String postCategory;
    private Long commentCount;
    private String profileUrl;

    private String thumbnail;

    public ViewBestPostListRes(Post post) {
        this.postId = post.getId();
        this.nickname = post.getUser().getNickname();
        this.title = post.getTitle();
        this.createAt = post.getCreateAt();
        this.viewCount = post.getViewCount();
        this.likeCount = (long) post.getPostLikes().size();
        this.hashtags = post.getHashtags().stream().map(Hashtag::getContent).collect(Collectors.toList());
        this.postCategory = post.getPostCategory().toString();
        this.commentCount = (long) post.getComments().size();
        if(post.getUser().getProfileImage() == null){
            this.profileUrl = "http://clickpick.iptime.org:8080/profile/images/default.png";
        }
        else {
            this.profileUrl = post.getUser().getProfileImage().getReturnUrl();
        }
    }

    public void addThumbnail(String url){
        this.thumbnail = url;
    }
}
