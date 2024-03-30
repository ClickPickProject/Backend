package com.clickpick.dto.comment;

import com.clickpick.domain.Comment;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ViewRecommentRes {
    @NotNull
    private Long commentId;
    @NotBlank
    private String nickname;
    @NotBlank
    private String content;
    @NotBlank
    private LocalDateTime createAt;
    @NotNull
    private Long likeCount;
    private boolean likeCommentCheck;
    private String profileUrl;
    private Long parentId;

    public ViewRecommentRes(Comment comment, boolean likeCommentCheck) {
        this.commentId = comment.getId();
        this.nickname = comment.getUser().getNickname();
        this.content = comment.getContent();
        this.createAt = comment.getCreateAt();
        this.likeCount = (long) comment.getCommentLikes().size();
        this.likeCommentCheck = likeCommentCheck;
        this.parentId = comment.getParent().getId();
        if(comment.getUser().getProfileImage() == null){
            this.profileUrl = "http://clickpick.iptime.org:8080/profile/images/default.png";
        }
        else {
            this.profileUrl = comment.getUser().getProfileImage().getReturnUrl();
        }
    }
}
