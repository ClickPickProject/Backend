package com.clickpick.repository;

import com.clickpick.domain.PositionLike;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PositionLikeRepository extends JpaRepository<PositionLike, Long> {

    @Query("select pl from PositionLike pl where pl.xPosition =:xPosition and pl.yPosition =:yPosition")
    Optional<PositionLike> findPosition(@Param("xPosition")double xPosition, @Param("yPosition")double yPosition);

    @Query("select pl from PositionLike pl where pl.user.id =:userId")
    Optional<List<PositionLike>> findUser(@Param("userId")String userId);

}
