package com.clickpick.repository;

import com.clickpick.domain.Comment;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("select c from Comment c where c.user.id =:userId and c.id =:commentId") // 띄어쓰기 주의 =:바로다음에 와야함
    Optional<Comment> findUserComment(@Param("commentId")Long commentId, @Param("userId")String userId); // 유저가 작성한 게시글 조회

    Optional<List<Comment>> findByPostId(Long postId);

    @Query("select c from Comment c where c.user.id =:userId")
    Optional<List<Comment>> findUserId(@Param("userId")String userId);
}
