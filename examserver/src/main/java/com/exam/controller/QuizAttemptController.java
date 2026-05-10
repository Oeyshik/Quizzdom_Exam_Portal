package com.exam.controller;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.exam.dto.QuizAttemptDetailResponse;
import com.exam.dto.QuizAttemptSubmitRequest;
import com.exam.dto.QuizAttemptSummaryResponse;
import com.exam.service.QuizAttemptService;

@RestController
@RequestMapping("/quiz-attempt")
@CrossOrigin("*")
public class QuizAttemptController {

    private static final Logger logger = LogManager.getLogger(QuizAttemptController.class);

    private final QuizAttemptService quizAttemptService;

    public QuizAttemptController(QuizAttemptService quizAttemptService) {
        this.quizAttemptService = quizAttemptService;
    }

    @PostMapping("/")
    public ResponseEntity<?> submit(@RequestBody QuizAttemptSubmitRequest request) {
        try {
            QuizAttemptSummaryResponse saved = quizAttemptService.saveAttempt(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid quiz submit: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Quiz submit failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Could not save quiz attempt"));
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> listForUser(@PathVariable Long userId) {
        try {
            List<QuizAttemptSummaryResponse> list = quizAttemptService.listAttemptsForUser(userId);
            return ResponseEntity.ok(list);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{attemptId}/user/{userId}")
    public ResponseEntity<?> getDetail(@PathVariable Long attemptId, @PathVariable Long userId) {
        try {
            QuizAttemptDetailResponse detail = quizAttemptService.getAttemptForUser(attemptId, userId);
            return ResponseEntity.ok(detail);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }
}
