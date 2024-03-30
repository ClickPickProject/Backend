package com.clickpick.repository;

import com.clickpick.domain.Post;
import com.clickpick.domain.PostCategory;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("select p from Post p where p.user.id =:userId and p.id =:postId") // 띄어쓰기 주의 =:바로다음에 와야함ㄷ
    Optional<Post> findUserPost(@Param("postId")Long postId, @Param("userId")String userId); // 유저가 작성한 게시글 조회

    // 작성한 게시글 리스트 검색 쿼리
    @Query("select p from Post p where p.user.id =:userId")
    Page<Post> findUserId(@Param("userID")String userId, Pageable pageable);

    // 좋아요 한 게시글 리스트 검색 쿼리
    @Query("select p from Post p join p.postLikes pl where pl.user.id =:userId")
    Page<Post> findLikePost(@Param("userId")String userId, Pageable pageable);

    // 작성한 댓글의 게시글 리스트 검색 쿼리
    @Query("select distinct p from Post p join fetch p.comments c where c.user.id =:userId") //fetch 시 별칭 쓰면 안된다더라...
    Page<Post> findCommentUserId(@Param("userId")String userId, Pageable pageable);

    // 좋아요 한 댓글의 게시글 리스트 검색 쿼리
    @Query("select distinct p from Post p join fetch p.comments c join c.commentLikes cl where cl.user.id =:userId") //fetch 시 별칭 쓰면 안된다더라...
    Page<Post> findLikeComment(@Param("userId")String userId, Pageable pageable);

    // 좋아요 top3 검색 쿼리
    @Query(value = "SELECT * FROM post ORDER BY like_count DESC LIMIT 3", nativeQuery = true)
    List<Post> findTop3LikePost();

    // 내용 검색 쿼리
    @Query("select p from Post p where p.content like %:searchString%")
    Page<Post> findContent(@Param("searchString")String searchString, Pageable pageable);

    // 게시글 작성 닉네임 검색 쿼리
    @Query("select p from Post p where p.user.nickname =:searchNickname")
    Page<Post> findNickname(@Param("searchNickname")String searchNickname, Pageable pageable);

    // 제목 검색 쿼리
    @Query("select p from Post p where p.title like %:searchTitle%")
    Page<Post> findTitle(@Param("searchTitle")String searchTitle, Pageable pageable);

    @Query("select p from Post p join p.hashtags ht where ht.content =:searchHashtag")
    Page<Post> findHashtag(@Param("searchHashtag")String searchHashtag, Pageable pageable);

    @Query("select p from Post p where p.postCategory =:searchCategory")
    Page<Post> findCategory(@Param("searchCategory") PostCategory searchCategory, Pageable pageable);


}
