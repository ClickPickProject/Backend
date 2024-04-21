package com.clickpick.dto.admin;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SingUpAdminReq {
    @NotBlank
    private String id;
    @NotBlank
    private String password;
    @NotBlank
    private String name;
    @NotBlank
    private String phone;


}
