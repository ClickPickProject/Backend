package com.clickpick.dto.admin;

import com.clickpick.domain.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ViewReportPostReq {

//    @NotBlank
//    private String id; //컬럼 id

    @NotBlank
    private User reportUser;

    @NotBlank
    private User reportedUser;

    @NotBlank
    private Long postId;

    @NotBlank
    private String reason;

    @NotBlank
    private ReportStatus reportStatus;

    public ViewReportPostReq(ReportPost reportPost) {
        this.reportedUser = reportPost.getReportedUser();
        this.reportUser = reportPost.getReportUser();
        this.postId = reportPost.getPost().getId();
        this.reason = reportPost.getReason();
        this.reportStatus = reportPost.getReportStatus();


    }




}
