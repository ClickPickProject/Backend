package com.clickpick.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReportPostReq {

    @NotBlank
    private String reportedUserNickname;
    @NotNull
    private Long postId;
    @NotBlank
    private String reason;
}
