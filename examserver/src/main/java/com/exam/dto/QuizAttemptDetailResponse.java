package com.exam.dto;

import java.time.Instant;
import java.util.List;

public class QuizAttemptDetailResponse {

    private Long id;
    private String subjectId;
    private String subjectTitle;
    private int attemptNumber;
    private int score;
    private int totalQuestions;
    private Instant completedAt;
    private List<QuizQuestionResultDto> details;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubjectTitle() {
        return subjectTitle;
    }

    public void setSubjectTitle(String subjectTitle) {
        this.subjectTitle = subjectTitle;
    }

    public int getAttemptNumber() {
        return attemptNumber;
    }

    public void setAttemptNumber(int attemptNumber) {
        this.attemptNumber = attemptNumber;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Instant completedAt) {
        this.completedAt = completedAt;
    }

    public List<QuizQuestionResultDto> getDetails() {
        return details;
    }

    public void setDetails(List<QuizQuestionResultDto> details) {
        this.details = details;
    }
}
