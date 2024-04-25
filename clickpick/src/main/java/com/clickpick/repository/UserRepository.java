package com.clickpick.repository;

import com.clickpick.domain.User;
import com.clickpick.domain.UserStatus;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByNickname(String nickname);

    Optional<User> findByPhone(String phone);

    @Query("select u from User u where u.status =:status")
    Page<User> findStatus(@Param("status") UserStatus status, Pageable pageable);


}
