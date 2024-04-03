package com.clickpick.dto.map;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LikedPositionReq {
    @NotNull
    private double xPosition;
    @NotNull
    private double yPosition;
    @NotBlank
    private String status;
}
