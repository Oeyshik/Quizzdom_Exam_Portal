package com.exam.service.impl;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.exam.dto.QuizAttemptDetailResponse;
import com.exam.dto.QuizAttemptSubmitRequest;
import com.exam.dto.QuizAttemptSummaryResponse;
import com.exam.dto.QuizQuestionResultDto;
import com.exam.model.QuizAttempt;
import com.exam.model.User;
import com.exam.repo.QuizAttemptRepository;
import com.exam.repo.UserRepository;
import com.exam.service.QuizAttemptService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class QuizAttemptServiceImpl implements QuizAttemptService {

    private static final Logger logger = LogManager.getLogger(QuizAttemptServiceImpl.class);

    private final QuizAttemptRepository quizAttemptRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    public QuizAttemptServiceImpl(
            QuizAttemptRepository quizAttemptRepository,
            UserRepository userRepository,
            ObjectMapper objectMapper) {
        this.quizAttemptRepository = quizAttemptRepository;
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public QuizAttemptSummaryResponse saveAttempt(QuizAttemptSubmitRequest request) {
        if (request.getUserId() == null) {
            throw new IllegalArgumentException("userId is required");
        }
        if (request.getSubjectId() == null || request.getSubjectId().isBlank()) {
            throw new IllegalArgumentException("subjectId is required");
        }
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + request.getUserId()));

        long previous = quizAttemptRepository.countByUserAndSubject(request.getUserId(), request.getSubjectId());
        int attemptNumber = (int) previous + 1;

        String detailJson;
        try {
            detailJson = objectMapper.writeValueAsString(
                    request.getDetails() != null ? request.getDetails() : List.of());
        } catch (Exception e) {
            logger.error("Failed to serialize quiz details", e);
            throw new IllegalStateException("Could not store quiz details");
        }

        QuizAttempt entity = new QuizAttempt();
        entity.setUser(user);
        entity.setSubjectId(request.getSubjectId().trim());
        entity.setSubjectTitle(
                request.getSubjectTitle() != null ? request.getSubjectTitle() : request.getSubjectId());
        entity.setAttemptNumber(attemptNumber);
        entity.setScore(request.getScore());
        entity.setTotalQuestions(request.getTotalQuestions());
        entity.setCompletedAt(Instant.now());
        entity.setDetailJson(detailJson);

        QuizAttempt saved = quizAttemptRepository.save(entity);
        logger.info("Saved quiz attempt id={} userId={} subject={} attemptNo={}",
                saved.getId(), user.getId(), saved.getSubjectId(), attemptNumber);

        return toSummary(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuizAttemptSummaryResponse> listAttemptsForUser(Long userId) {
        if (userId == null || !userRepository.existsById(userId)) {
            throw new IllegalArgumentException("User not found: " + userId);
        }
        return quizAttemptRepository.findByUserOrderByCompletedAtDesc(userId).stream()
                .map(this::toSummary)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public QuizAttemptDetailResponse getAttemptForUser(Long attemptId, Long userId) {
        QuizAttempt attempt = quizAttemptRepository.findByIdAndUserId(attemptId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Attempt not found or access denied"));

        QuizAttemptDetailResponse dto = new QuizAttemptDetailResponse();
        dto.setId(attempt.getId());
        dto.setSubjectId(attempt.getSubjectId());
        dto.setSubjectTitle(attempt.getSubjectTitle());
        dto.setAttemptNumber(attempt.getAttemptNumber());
        dto.setScore(attempt.getScore());
        dto.setTotalQuestions(attempt.getTotalQuestions());
        dto.setCompletedAt(attempt.getCompletedAt());

        try {
            List<QuizQuestionResultDto> details = objectMapper.readValue(
                    attempt.getDetailJson(),
                    new TypeReference<List<QuizQuestionResultDto>>() {
                    });
            dto.setDetails(details);
        } catch (Exception e) {
            logger.error("Failed to parse stored quiz details for attempt {}", attemptId, e);
            dto.setDetails(List.of());
        }
        return dto;
    }

    private QuizAttemptSummaryResponse toSummary(QuizAttempt a) {
        QuizAttemptSummaryResponse s = new QuizAttemptSummaryResponse();
        s.setId(a.getId());
        s.setSubjectId(a.getSubjectId());
        s.setSubjectTitle(a.getSubjectTitle());
        s.setAttemptNumber(a.getAttemptNumber());
        s.setScore(a.getScore());
        s.setTotalQuestions(a.getTotalQuestions());
        s.setCompletedAt(a.getCompletedAt());
        return s;
    }
}
