package com.clickpick.dto.admin;

import com.clickpick.domain.ReportComment;
import com.clickpick.domain.ReportStatus;
import com.clickpick.domain.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ViewReportCommentRes {

    @NotNull
    private Long reportCommentId; //컬럼 id

    @NotBlank
    private String reportUserId;

    @NotBlank
    private String reportedUserId;

    @NotBlank
    private Long commentId;

    @NotBlank
    private String reason;

    @NotBlank
    private ReportStatus reportStatus;

    public ViewReportCommentRes(ReportComment reportComment) {
        this.reportCommentId = reportComment.getId();
        this.reportedUserId = reportComment.getReportedUser().getId();
        this.reportUserId = reportComment.getReportUser().getId();
        this.commentId = reportComment.getComment().getId();
        this.reason = reportComment.getReason();
        this.reportStatus = reportComment.getReportStatus();


    }




}
