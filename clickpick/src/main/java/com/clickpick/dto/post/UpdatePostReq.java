package com.clickpick.dto.post;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class UpdatePostReq {

    @NotBlank
    private String title;
    @NotBlank
    private String content;
    @NotBlank
    private String postCategory;

    private String position;

    private List<String> hashtags;

    private String thumbnailImage;

    private List<String> updateImageNames;
}
