package com.clickpick.dto.admin;


import com.clickpick.domain.Notice;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NoticeListRes {

    @NotBlank
    private Long noticeId;
    private String admin;
    @NotBlank
    private String title;
    private LocalDateTime createAt;


    public NoticeListRes(Notice notice) {
        this.noticeId = notice.getId();
        this.admin = "ADMIN";
        this.title = notice.getTitle();
        this.createAt = notice.getCreateAt();
    }
}
