package com.clickpick.repository;

import com.clickpick.domain.BanUser;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;


public interface BanUserRepository extends JpaRepository<BanUser, Long> {

    @Query("select bu from BanUser bu where bu.user.id =:userId")
    Optional<BanUser> findBanUserId(@Param("userId") String userId);
}
