package com.clickpick.dto.admin;

import com.clickpick.domain.User;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ViewBanUserListReq {

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



    public ViewBanUserListReq(User user){
        this.id = user.getId();
        this.name = user.getName();
        this.nickname = user.getNickname();
        this.phone = user.getPhone();
    }
}