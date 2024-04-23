package com.clickpick.dto.question;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateQuestionReq {
    @NotBlank
    private String title;
    @NotBlank
    private String content;
    @NotBlank
    private String lock;
}
