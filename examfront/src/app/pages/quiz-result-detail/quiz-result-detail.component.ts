import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { QuizAttemptDetail } from 'src/app/models/quiz-attempt.models';
import { AuthService } from 'src/app/services/auth.service';
import { QuizAttemptService } from 'src/app/services/quiz-attempt.service';
import { buildQuizResultHtml, triggerHtmlDownload } from 'src/app/utils/quiz-result-html';

@Component({
  selector: 'app-quiz-result-detail',
  templateUrl: './quiz-result-detail.component.html',
  styleUrls: ['./quiz-result-detail.component.css'],
})
export class QuizResultDetailComponent implements OnInit {
  detail: QuizAttemptDetail | null = null;
  loading = true;
  loadError = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private authService: AuthService,
    private quizAttemptService: QuizAttemptService,
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
    const attemptId = Number(this.route.snapshot.paramMap.get('attemptId'));
    if (!attemptId) {
      this.loading = false;
      this.loadError = true;
      return;
    }
    this.quizAttemptService.getDetail(attemptId, user.id).subscribe({
      next: (d) => {
        this.detail = d;
        this.loading = false;
      },
      error: () => {
        this.loadError = true;
        this.loading = false;
      },
    });
  }

  isCorrect(i: number): boolean {
    if (!this.detail?.details?.[i]) {
      return false;
    }
    const d = this.detail.details[i];
    return d.selectedIndex === d.correctIndex;
  }

  selectedLabel(i: number): string {
    if (!this.detail?.details?.[i]) {
      return '';
    }
    const d = this.detail.details[i];
    if (d.selectedIndex == null || d.options[d.selectedIndex] == null) {
      return '— (skipped)';
    }
    return d.options[d.selectedIndex];
  }

  correctLabel(i: number): string {
    if (!this.detail?.details?.[i]) {
      return '';
    }
    const d = this.detail.details[i];
    return d.options[d.correctIndex] ?? '';
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

  goList(): void {
    this.router.navigate(['/user/quiz/results']);
  }

  download(): void {
    if (!this.detail) {
      return;
    }
    const when = this.formatCompletedAt(this.detail.completedAt);
    const html = buildQuizResultHtml(this.detail, when);
    const safeSubject = this.detail.subjectId.replace(/[^a-z0-9-_]/gi, '_');
    const filename = `quiz-${safeSubject}-attempt${this.detail.attemptNumber}.html`;
    triggerHtmlDownload(filename, html);
    this.snackBar.open('Download started.', 'Dismiss', { duration: 2500 });
  }
}
