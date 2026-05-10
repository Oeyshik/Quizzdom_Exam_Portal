import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { QuizSubjectMeta } from 'src/app/models/quiz.models';
import { AuthService } from 'src/app/services/auth.service';
import { QuizService } from 'src/app/services/quiz.service';

@Component({
  selector: 'app-quiz-subject-selection',
  templateUrl: './quiz-subject-selection.component.html',
  styleUrls: ['./quiz-subject-selection.component.css'],
})
export class QuizSubjectSelectionComponent implements OnInit {
  subjects: QuizSubjectMeta[] = [];
  loading = true;
  loadError = false;

  constructor(
    private quizService: QuizService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    if (!this.authService.isLoggedIn() || !this.authService.isNormal()) {
      this.router.navigate(['/login']);
      return;
    }
    this.quizService.getSubjects().subscribe({
      next: (list) => {
        this.subjects = list;
        this.loading = false;
      },
      error: () => {
        this.loadError = true;
        this.loading = false;
      },
    });
  }

  startExam(subjectId: string): void {
    this.router.navigate(['/user/quiz/exam', subjectId]);
  }

  goDashboard(): void {
    this.router.navigate(['/user/dashboard']);
  }
}
