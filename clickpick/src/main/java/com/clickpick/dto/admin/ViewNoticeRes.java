package com.clickpick.dto.admin;

import com.clickpick.domain.Notice;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ViewNoticeRes {

    @NotBlank
    private Long noticeId;
    private String admin;
    @NotBlank
    private String title;
    @NotBlank
    private String content;
    private LocalDateTime createAt;

    public ViewNoticeRes(Notice notice){
        this.noticeId = notice.getId();
        this.admin = "ADMIN";
        this.title = notice.getTitle();
        this.content = notice.getContent();
        this.createAt = notice.getCreateAt();
    }
}
