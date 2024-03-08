package com.clickpick.dto.post;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreatePostReq {

    @NotBlank
    private String title;

    @NotBlank
    private String content;

    private String position;

    private String hashtag;



}
