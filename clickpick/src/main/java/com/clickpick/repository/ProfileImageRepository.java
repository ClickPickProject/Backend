package com.clickpick.repository;

import com.clickpick.domain.ProfileImage;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ProfileImageRepository extends JpaRepository<ProfileImage, Long> {

    @Query("select pi from ProfileImage pi where pi.user.id =:userId")
    Optional<ProfileImage> findUserId(@Param("userId")String userId);
}
