package com.clickpick.repository;

import com.clickpick.domain.ReportPost;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;
import java.util.Optional;


public interface ReportPostRepository extends JpaRepository<ReportPost, Long> {


   @Query("select rp from ReportPost rp where rp.reportedUser.id =:reportedUserId")
   Optional<ReportPost> findReportedUserID(@Param("reportedUserId")String reportedUserId);

    @Query("select rp from ReportPost rp where rp.reportedUser.nickname =:reportedUserNickname and rp.post.id =:postId ")
    Optional<ReportPost> findReportPost(@Param("reportedUserNickname")String reportedUserNickname, @Param("postId") Long postId);

    @Query("SELECT CONCAT(FUNCTION('YEAR', rp.createAt), '-', FUNCTION('MONTH', rp.createAt)) AS monthYear, COUNT(rp) AS userCount " +
            "FROM ReportPost rp " +
            "WHERE FUNCTION('YEAR', rp.createAt) = :year " +
            "GROUP BY monthYear " +
            "ORDER BY FUNCTION('MONTH', rp.createAt)")
    List<Map<String, Object>> countReportPostByMonth(@Param("year") Long year);


}
