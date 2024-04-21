package com.clickpick.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReportCommentReq {


    @NotBlank
    private String reportedUserNickname;
    @NotNull
    private Long commentId;
    @NotBlank
    private String reason;

}
