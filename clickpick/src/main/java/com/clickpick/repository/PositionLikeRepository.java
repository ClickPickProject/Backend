package com.clickpick.repository;

import com.clickpick.domain.PositionLike;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PositionLikeRepository extends JpaRepository<PositionLike, Long> {

    // x,y 좌표와 동일한 pl 검색
    @Query("select pl from PositionLike pl where pl.xPosition =:xPosition and pl.yPosition =:yPosition")
    Optional<PositionLike> findPosition(@Param("xPosition")double xPosition, @Param("yPosition")double yPosition);

    // 지도 범위 안의 유저가 동일한 pl 검색
    @Query("select pl from PositionLike pl where pl.user.id =:userId and pl.xPosition >=:west and pl.xPosition <=:east and pl.yPosition >=:south and pl.yPosition <=:north")
    Optional<List<PositionLike>> findUser(@Param("userId")String userId, @Param("south") double south, @Param("west") double west, @Param("north") double north, @Param("east") double east);


}
