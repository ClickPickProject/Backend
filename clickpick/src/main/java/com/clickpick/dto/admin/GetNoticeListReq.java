package com.clickpick.dto.admin;


import com.clickpick.domain.Admin;
import com.clickpick.domain.Notice;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GetNoticeListReq {

    @NotBlank
    private Long id;
    private String admin;
    @NotBlank
    private String title;
    @NotBlank
    private String content;
    private LocalDateTime createAt;


    public GetNoticeListReq(Notice notice) {
        this.id = notice.getId();
        this.admin  =notice.getAdmin().getId();
        this.title = notice.getTitle();
        this.content = notice.getContent();
        this.createAt = notice.getCreateAt();
    }
}