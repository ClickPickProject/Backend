package com.clickpick.repository;

import com.clickpick.domain.CommentLike;
import com.clickpick.domain.PostLike;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {

    @Query("select cl from CommentLike cl where cl.user.id =:userId and cl.comment.id =:commentId") //유저아이디와 댓글 아이디가 같으면 좋아요 한 것
    Optional<CommentLike> checkLikeComment(@Param("commentId")Long commentId, @Param("userId")String userId);

    long countByCommentId(Long commentId);
}
