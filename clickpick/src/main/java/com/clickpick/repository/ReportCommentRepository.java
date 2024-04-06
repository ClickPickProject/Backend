package com.clickpick.repository;

import com.clickpick.domain.ReportComment;
import com.clickpick.domain.ReportPost;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ReportCommentRepository extends JpaRepository<ReportComment, Long> {

    @Query("select rp from ReportComment rp where rp.reportedUser.id =:reportedUserId")
    Optional<ReportComment> findReportedUserID(@Param("reportedUserId")String reportedUserId);
}
