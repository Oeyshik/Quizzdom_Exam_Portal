package com.exam.service;

import java.util.List;

import com.exam.dto.QuizAttemptDetailResponse;
import com.exam.dto.QuizAttemptSubmitRequest;
import com.exam.dto.QuizAttemptSummaryResponse;

public interface QuizAttemptService {

    QuizAttemptSummaryResponse saveAttempt(QuizAttemptSubmitRequest request);

    List<QuizAttemptSummaryResponse> listAttemptsForUser(Long userId);

    QuizAttemptDetailResponse getAttemptForUser(Long attemptId, Long userId);
}
