package com.clickpick.repository;

import com.clickpick.domain.QuestionPost;
import com.clickpick.domain.QuestionStatus;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface QuestionPostRepository extends JpaRepository<QuestionPost, Long> {

    @Query("select qp from QuestionPost qp where qp.id =:questionId and qp.user.id =:userId")
    Optional<QuestionPost> findQuestionUser(@Param("questionId")Long questionId, @Param("userId")String userId);

    @Query("select qp from QuestionPost qp where qp.user.id =:userId")
    Page<QuestionPost> findUser(@Param("userId")String userId, Pageable pageable);

    @Query("select qp from QuestionPost qp where qp.id =:questionId and qp.admin.id =:adminId")
    Optional<QuestionPost> findAnswerAdmin(@Param("questionId")Long questionId, @Param("adminId")String adminId);

    //답변이 달린 질문 검색 쿼리
    @Query("select qp from QuestionPost qp where qp.parent.id =:questionId")
    Optional<List<QuestionPost>> findAnswerQuestion(@Param("questionId")Long questionId);

    //질문 검색 쿼리
    @Query("select qp from QuestionPost qp where qp.parent.id IS NULL")
    Page<QuestionPost> findQuestion(Pageable pageable);

    // 상태에 따른 검색 쿼리
    @Query("select qp from QuestionPost qp where qp.status =:status")
    Page<QuestionPost> findQuestionStatus(@Param("status")QuestionStatus status, Pageable pageable);

}
