package com.clickpick.repository;

import com.clickpick.domain.User;
import com.clickpick.domain.UserStatus;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByNickname(String nickname);

    Optional<User> findByPhone(String phone);

    @Query("select u from User u where u.status =:status")
    Page<User> findStatus(@Param("status") UserStatus status, Pageable pageable);

    // 월별 사용자 카운트 조회 쿼리
    @Query("SELECT CONCAT(FUNCTION('YEAR', u.createAt), '-', FUNCTION('MONTH', u.createAt)) AS monthYear, COUNT(u) AS userCount " +
            "FROM User u " +
            "WHERE FUNCTION('YEAR', u.createAt) = :year " +
            "GROUP BY monthYear " +
            "ORDER BY FUNCTION('MONTH', u.createAt)")
    List<Map<String, Object>> countUsersByMonth(@Param("year") Long year);
}
