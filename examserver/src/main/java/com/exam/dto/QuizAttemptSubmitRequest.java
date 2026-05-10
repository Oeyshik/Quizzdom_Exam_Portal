package com.exam.dto;

import java.util.List;

public class QuizAttemptSubmitRequest {

    private Long userId;
    private String subjectId;
    private String subjectTitle;
    private int score;
    private int totalQuestions;
    private List<QuizQuestionResultDto> details;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public List<QuizQuestionResultDto> getDetails() {
        return details;
    }

    public void setDetails(List<QuizQuestionResultDto> details) {
        this.details = details;
    }
}
