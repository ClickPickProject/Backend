package com.clickpick.dto.admin;

import com.clickpick.domain.BanUser;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ViewBanUserListRes {

    @NotBlank
    private String id;
    @NotBlank
    private String name;
    @NotBlank
    private String nickname;
    @NotBlank
    private String phone;
    @NotBlank
    private LocalDateTime startDate;
    @NotBlank
    private LocalDateTime endDate;




    public ViewBanUserListRes(BanUser banUser){
        this.id = banUser.getUser().getId();
        this.name = banUser.getUser().getName();
        this.nickname = banUser.getUser().getNickname();
        this.phone = banUser.getUser().getPhone();
        this.startDate = banUser.getStartDate();
        this.endDate = banUser.getEndDate();
    }
}
