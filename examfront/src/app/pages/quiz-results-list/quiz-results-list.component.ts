import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { QuizAttemptDetail, QuizAttemptSummary } from 'src/app/models/quiz-attempt.models';
import { AuthService } from 'src/app/services/auth.service';
import { QuizAttemptService } from 'src/app/services/quiz-attempt.service';
import { buildQuizResultHtml, triggerHtmlDownload } from 'src/app/utils/quiz-result-html';

@Component({
  selector: 'app-quiz-results-list',
  templateUrl: './quiz-results-list.component.html',
  styleUrls: ['./quiz-results-list.component.css'],
})
export class QuizResultsListComponent implements OnInit {
  attempts: QuizAttemptSummary[] = [];
  loading = true;
  loadError = false;

  constructor(
    private authService: AuthService,
    private quizAttemptService: QuizAttemptService,
    private router: Router,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    if (!this.authService.isLoggedIn() || !this.authService.isNormal()) {
      this.router.navigate(['/login']);
      return;
    }
    const user = this.authService.getCurrentUser();
    if (!user?.id) {
      this.loading = false;
      this.loadError = true;
      return;
    }
    this.quizAttemptService.listForUser(user.id).subscribe({
      next: (rows) => {
        this.attempts = rows;
        this.loading = false;
      },
      error: () => {
        this.loadError = true;
        this.loading = false;
      },
    });
  }

  goDashboard(): void {
    this.router.navigate(['/user/dashboard']);
  }

  viewAttempt(id: number): void {
    this.router.navigate(['/user/quiz/results', id]);
  }

  downloadAttempt(row: QuizAttemptSummary): void {
    const user = this.authService.getCurrentUser();
    if (!user?.id) {
      return;
    }
    this.quizAttemptService.getDetail(row.id, user.id).subscribe({
      next: (detail) => this.downloadDetail(detail),
      error: () =>
        this.snackBar.open('Could not load this attempt for download.', 'Dismiss', { duration: 4000 }),
    });
  }

  private downloadDetail(detail: QuizAttemptDetail): void {
    const when = this.formatCompletedAt(detail.completedAt);
    const html = buildQuizResultHtml(detail, when);
    const safeSubject = detail.subjectId.replace(/[^a-z0-9-_]/gi, '_');
    const filename = `quiz-${safeSubject}-attempt${detail.attemptNumber}.html`;
    triggerHtmlDownload(filename, html);
  }

  formatCompletedAt(iso: string): string {
    if (!iso) {
      return '';
    }
    try {
      return new Date(iso).toLocaleString();
    } catch {
      return iso;
    }
  }
}
