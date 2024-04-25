package com.clickpick.dto.admin;

import com.clickpick.domain.User;
import com.clickpick.domain.UserStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ViewUserListRes {

    @NotBlank
    private String id;
    @NotBlank
    private String name;
    @NotBlank
    private String nickname;
    @NotBlank
    private String phone;
    @NotBlank
    private LocalDateTime createAt;
    @NotBlank
    private UserStatus userStatus;



    public ViewUserListRes(User user){
        this.id = user.getId();
        this.name = user.getName();
        this.nickname = user.getNickname();
        this.phone = user.getPhone();
        this.createAt = user.getCreateAt();
        this.userStatus = user.getStatus();
    }
}
