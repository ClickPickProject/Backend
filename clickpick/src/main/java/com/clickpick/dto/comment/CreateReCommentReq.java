package com.clickpick.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateReCommentReq {

    @NotNull
    private Long parentCommentId;

    @NotNull
    private Long postId;

    @NotBlank
    private String content;

}
