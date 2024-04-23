package com.clickpick.repository;

import com.clickpick.domain.Admin;
import com.clickpick.domain.Notice;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

    @Query("select n from Notice n where n.admin.id =:adminId and n.id =:noticeId")
    Optional<Notice> findAdminNotice(@Param("noticeId")Long noticeId, @Param("adminId")String adminId);
}
