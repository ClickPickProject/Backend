package com.clickpick.repository;

import com.clickpick.domain.ReportComment;
import com.clickpick.domain.ReportPost;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ReportCommentRepository extends JpaRepository<ReportComment, Long> {


    @Query("select rp from ReportComment rp where rp.reportedUser.id =:reportedUserId")
    Optional<ReportComment> findReportedUserID(@Param("reportedUserId")String reportedUserId);

    @Query("select rc from ReportComment rc where rc.reportedUser.nickname =:reportedUserNickname and rc.comment.id =:commentId ")
    Optional<ReportComment> findReportComment(@Param("reportedUserNickname")String reportedUserNickname, @Param("commentId") Long commentId);

    @Query("SELECT CONCAT(FUNCTION('YEAR', rc.createAt), '-', FUNCTION('MONTH', rc.createAt)) AS monthYear, COUNT(rc) AS userCount " +
            "FROM ReportComment rc " +
            "WHERE FUNCTION('YEAR', rc.createAt) = :year " +
            "GROUP BY monthYear " +
            "ORDER BY FUNCTION('MONTH', rc.createAt)")
    List<Map<String, Object>> countReportCommentByMonth(@Param("year") Long year);

}
