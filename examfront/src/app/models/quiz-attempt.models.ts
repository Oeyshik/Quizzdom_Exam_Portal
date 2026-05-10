export interface QuizQuestionResult {
  questionId: string;
  question: string;
  options: string[];
  selectedIndex: number | null;
  correctIndex: number;
}

export interface QuizAttemptSubmitRequest {
  userId: number;
  subjectId: string;
  subjectTitle: string;
  score: number;
  totalQuestions: number;
  details: QuizQuestionResult[];
}

export interface QuizAttemptSummary {
  id: number;
  subjectId: string;
  subjectTitle: string;
  attemptNumber: number;
  score: number;
  totalQuestions: number;
  completedAt: string;
}

export interface QuizAttemptDetail extends QuizAttemptSummary {
  details: QuizQuestionResult[];
}
