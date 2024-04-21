package com.clickpick.repository;

import com.clickpick.domain.PostImage;
import com.clickpick.domain.PostImageStatus;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PostImageRepository extends JpaRepository<PostImage, Long> {

    @Query("select pi from PostImage pi where pi.user.id =:userId and pi.fileName=:fileName")
    Optional<PostImage> findPostImage(@Param("userId")String userId, @Param("fileName")String fileName);

    Optional<PostImage> findByFileName(String fileName);

    @Query("select pi from PostImage pi where pi.post.id =:postId and pi.postImageStatus =:status")
    Optional<PostImage> findThumbnail(@Param("postId") Long postId, @Param("status") PostImageStatus status);
}
