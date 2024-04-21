package com.clickpick.repository;

import com.clickpick.domain.User;
import com.clickpick.domain.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByNickname(String nickname);

    Optional<User> findByPhone(String phone);

    Page<User> findByStatus(UserStatus status, Pageable pageable);
}
