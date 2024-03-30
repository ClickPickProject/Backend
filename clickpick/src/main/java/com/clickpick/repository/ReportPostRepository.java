package com.clickpick.repository;

import com.clickpick.domain.ReportPost;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;


public interface ReportPostRepository extends JpaRepository<ReportPost, Long> {
    @Query("select rp from ReportPost rp where rp.reportedUser.nickname =:reportedUserNickname and rp.post.id =:postId ")
    Optional<ReportPost> findReportPost(@Param("reportedUserNickname")String reportedUserNickname, @Param("postId") Long postId);

}
