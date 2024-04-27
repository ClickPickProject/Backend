package com.clickpick.dto.admin;

import com.clickpick.domain.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ViewReportPostRes {

    @NotNull
    private Long reportPostId; //컬럼 id

    @NotBlank
    private String reportUserId;

    @NotBlank
    private String reportedUserId;

    private Long postId;

    @NotBlank
    private String reason;

    @NotBlank
    private ReportStatus reportStatus;

    public ViewReportPostRes(ReportPost reportPost) {
        this.reportPostId = reportPost.getId();
        this.reportedUserId = reportPost.getReportedUser().getId();
        this.reportUserId = reportPost.getReportUser().getId();
        if(reportPost.getPost() == null){
            this.postId = null;
        }
        else{
            this.postId = reportPost.getPost().getId();
        }
        this.reason = reportPost.getReason();
        this.reportStatus = reportPost.getReportStatus();


    }




}
