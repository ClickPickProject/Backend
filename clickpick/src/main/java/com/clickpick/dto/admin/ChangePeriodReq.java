package com.clickpick.dto.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChangePeriodReq {
    @NotBlank
    private String userId;
    @NotNull
    private Long days;
}
