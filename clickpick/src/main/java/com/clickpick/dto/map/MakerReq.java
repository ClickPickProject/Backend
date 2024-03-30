package com.clickpick.dto.map;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MakerReq {
    @NotNull
    private double south;
    @NotNull
    private double west;
    @NotNull
    private double north;
    @NotNull
    private double east;
}
