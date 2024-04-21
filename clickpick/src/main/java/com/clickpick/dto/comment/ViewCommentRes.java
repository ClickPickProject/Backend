package com.clickpick.dto.comment;

import com.clickpick.domain.Comment;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class ViewCommentRes {
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
    private String commentStatus;
    private String profileUrl;
    private List<ViewRecommentRes> recommentList = new ArrayList<>();


    public ViewCommentRes(Comment comment, boolean likeCommentCheck) {
        this.commentId = comment.getId();
        this.nickname = comment.getUser().getNickname();
        this.content = comment.getContent();
        this.createAt = comment.getCreateAt();
        this.likeCount = (long) comment.getCommentLikes().size();
        this.likeCommentCheck = likeCommentCheck;
        this.commentStatus = String.valueOf(comment.getStatus());
        if(comment.getUser(). getProfileImage() == null){
            this.profileUrl = "http://clickpick.iptime.org:8080/profile/images/default.png";
        }
        else {
            this.profileUrl = comment.getUser().getProfileImage().getReturnUrl();
        }
    }

    public void addRecomment(ViewRecommentRes viewRecommentRes){
        recommentList.add(viewRecommentRes);

    }
}
