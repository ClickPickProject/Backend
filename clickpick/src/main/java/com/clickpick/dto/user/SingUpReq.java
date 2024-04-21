package com.clickpick.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SingUpReq {
    @NotBlank
    private String id;
    @NotBlank
    private String password;
    @NotBlank
    private String name;
    @NotBlank
    private String nickname;
    @NotBlank
    private String phone;


}
