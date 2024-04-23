package com.clickpick.dto.admin;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateNoticeReq {

    @NotBlank
    private String title;
    @NotBlank
    private String content;

}
