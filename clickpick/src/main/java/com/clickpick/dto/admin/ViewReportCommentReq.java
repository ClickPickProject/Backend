package com.clickpick.dto.admin;

import com.clickpick.domain.ReportComment;
import com.clickpick.domain.ReportPost;
import com.clickpick.domain.ReportStatus;
import com.clickpick.domain.User;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ViewReportCommentReq {

//    @NotBlank
//    private String id; //컬럼 id

    @NotBlank
    private User reportUser;

    @NotBlank
    private User reportedUser;

    @NotBlank
    private Long commentId;

    @NotBlank
    private String reason;

    @NotBlank
    private ReportStatus reportStatus;

    public ViewReportCommentReq(ReportComment reportComment) {
        this.reportedUser = reportComment.getReportedUser();
        this.reportUser = reportComment.getReportUser();
        this.commentId = reportComment.getComment().getId();
        this.reason = reportComment.getReason();
        this.reportStatus = reportComment.getReportStatus();


    }




}
