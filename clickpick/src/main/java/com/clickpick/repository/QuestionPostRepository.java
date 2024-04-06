package com.clickpick.repository;

import com.clickpick.domain.QuestionPost;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface QuestionPostRepository extends JpaRepository<QuestionPost, Long> {

    @Query("select qp from QuestionPost qp where qp.id =:questionId and qp.user.id =:userId")
    Optional<QuestionPost> findQuestionUser(@Param("questionId")Long questionId, @Param("userId")String userId);
}
