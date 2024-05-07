package com.clickpick.dto.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CancelReportRes {

    @NotNull
    private Long id;
    @NotBlank
    private String type;

}
