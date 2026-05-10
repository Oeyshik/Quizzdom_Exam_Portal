import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { MatSnackBar } from '@angular/material/snack-bar';
import { QuizQuestion } from 'src/app/models/quiz.models';
import { AuthService } from 'src/app/services/auth.service';
import { QuizService } from 'src/app/services/quiz.service';
import { QuizAttemptService } from 'src/app/services/quiz-attempt.service';

@Component({
  selector: 'app-quiz-exam',
  templateUrl: './quiz-exam.component.html',
  styleUrls: ['./quiz-exam.component.css'],
})
export class QuizExamComponent implements OnInit, OnDestroy {
  subjectId = '';
  subjectTitle = '';
  questions: QuizQuestion[] = [];
  /** Selected option index per question, or null if unanswered */
  answers: (number | null)[] = [];
  currentIndex = 0;
  loading = true;
  loadError = false;
  submitted = false;
  score = 0;

  private routeSub?: Subscription;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private authService: AuthService,
    private quizService: QuizService,
    private quizAttemptService: QuizAttemptService,
    private snackBar: MatSnackBar
  ) {}

  get currentQuestion(): QuizQuestion | null {
    return this.questions[this.currentIndex] ?? null;
  }

  get progressFraction(): number {
    if (!this.questions.length) {
      return 0;
    }
    return (this.currentIndex + 1) / this.questions.length;
  }

  get isLastQuestion(): boolean {
    return this.currentIndex >= this.questions.length - 1;
  }

  ngOnInit(): void {
    if (!this.authService.isLoggedIn() || !this.authService.isNormal()) {
      this.router.navigate(['/login']);
      return;
    }
    this.routeSub = this.route.paramMap.subscribe((pm) => {
      const id = pm.get('subjectId') || '';
      this.resetExamState();
      this.subjectId = id;
      this.fetchExam(id);
    });
  }

  ngOnDestroy(): void {
    this.routeSub?.unsubscribe();
  }

  private resetExamState(): void {
    this.subjectTitle = '';
    this.questions = [];
    this.answers = [];
    this.currentIndex = 0;
    this.loading = true;
    this.loadError = false;
    this.submitted = false;
    this.score = 0;
  }

  private fetchExam(subjectId: string): void {
    if (!subjectId) {
      this.loading = false;
      this.loadError = true;
      return;
    }
    this.quizService.getQuizData().subscribe({
      next: (data) => {
        const meta = data.subjects?.find((s) => s.id === subjectId);
        this.subjectTitle = meta?.title ?? subjectId;
        const raw = data.questionsBySubject?.[subjectId] ?? [];
        this.questions = raw.filter((q) => this.isValidQuestion(q));
        this.answers = this.questions.map(() => null);
        this.loading = false;
        if (!this.questions.length) {
          this.loadError = true;
        }
      },
      error: () => {
        this.loading = false;
        this.loadError = true;
      },
    });
  }

  private isValidQuestion(q: QuizQuestion): boolean {
    if (!q?.options?.length || q.question == null || q.correctIndex == null) {
      return false;
    }
    return q.correctIndex >= 0 && q.correctIndex < q.options.length;
  }

  prev(): void {
    if (this.currentIndex > 0) {
      this.currentIndex--;
    }
  }

  next(): void {
    if (!this.isLastQuestion) {
      this.currentIndex++;
    }
  }

  submit(): void {
    let correct = 0;
    for (let i = 0; i < this.questions.length; i++) {
      if (this.answers[i] === this.questions[i].correctIndex) {
        correct++;
      }
    }
    this.score = correct;
    this.submitted = true;
    this.persistAttempt();
  }

  private persistAttempt(): void {
    const user = this.authService.getCurrentUser();
    if (!user?.id) {
      this.snackBar.open('Results were not saved (missing user id).', 'Dismiss', { duration: 5000 });
      return;
    }
    const details = this.questions.map((q, i) => ({
      questionId: q.id,
      question: q.question,
      options: q.options,
      selectedIndex: this.answers[i] ?? null,
      correctIndex: q.correctIndex,
    }));
    this.quizAttemptService
      .submit({
        userId: user.id,
        subjectId: this.subjectId,
        subjectTitle: this.subjectTitle,
        score: this.score,
        totalQuestions: this.questions.length,
        details,
      })
      .subscribe({
        error: () =>
          this.snackBar.open(
            'Could not save results to the server. Your score is still shown here.',
            'Dismiss',
            { duration: 6000 }
          ),
      });
  }

  retake(): void {
    this.answers = this.questions.map(() => null);
    this.currentIndex = 0;
    this.submitted = false;
    this.score = 0;
  }

  goSubjects(): void {
    this.router.navigate(['/user/quiz/subjects']);
  }

  goDashboard(): void {
    this.router.navigate(['/user/dashboard']);
  }

  isCorrect(i: number): boolean {
    return this.answers[i] === this.questions[i].correctIndex;
  }
}
