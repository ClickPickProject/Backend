package com.clickpick.dto.user;

import com.clickpick.domain.User;
import lombok.Data;

import java.time.LocalDateTime;
@Data
public class UserInfoRes {

    private String id;
    private String name;
    private String nickname;
    private String phone;
    private LocalDateTime createAt;

    public UserInfoRes(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.nickname = user.getNickname();
        this.phone = user.getPhone();
        this.createAt = user.getCreateAt();
    }
}
