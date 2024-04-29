package com.clickpick.dto.admin;

import com.clickpick.domain.ReportPost;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BanUserReq {

    @NotNull
    private Long reportId;
    @NotBlank
    private String reportedUserId;
    @NotBlank
    private String reason;
    @NotNull
    private Long banDays;

}
