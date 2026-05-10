package com.exam.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.exam.model.QuizAttempt;

public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {

    @Query("SELECT COUNT(q) FROM QuizAttempt q WHERE q.user.id = :userId AND q.subjectId = :subjectId")
    long countByUserAndSubject(@Param("userId") Long userId, @Param("subjectId") String subjectId);

    @Query("SELECT q FROM QuizAttempt q WHERE q.user.id = :userId ORDER BY q.completedAt DESC")
    List<QuizAttempt> findByUserOrderByCompletedAtDesc(@Param("userId") Long userId);

    @Query("SELECT q FROM QuizAttempt q WHERE q.id = :id AND q.user.id = :userId")
    Optional<QuizAttempt> findByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);
}
