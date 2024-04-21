package com.clickpick.dto.post;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class UpdatePostReq {

    @NotBlank
    private String title;
    @NotBlank
    private String content;
    @NotBlank
    private String postCategory;

    private String position;
    private double xPosition = 200.0;
    private double yPosition = 200.0;

    private List<String> hashtags;

    private String thumbnailImage;

    private List<String> updateImageNames;
}
