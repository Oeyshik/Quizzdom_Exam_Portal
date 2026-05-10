import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map, shareReplay } from 'rxjs';
import { QuizDataFile, QuizQuestion, QuizSubjectMeta } from '../models/quiz.models';

@Injectable({
  providedIn: 'root',
})
export class QuizService {
  private readonly dataUrl = 'assets/data/quiz-questions.json';
  private cache$: Observable<QuizDataFile> | null = null;

  constructor(private http: HttpClient) {}

  getQuizData(): Observable<QuizDataFile> {
    if (!this.cache$) {
      this.cache$ = this.http.get<QuizDataFile>(this.dataUrl).pipe(shareReplay(1));
    }
    return this.cache$;
  }

  getSubjects(): Observable<QuizSubjectMeta[]> {
    return this.getQuizData().pipe(map((data) => data.subjects ?? []));
  }

  getQuestionsForSubject(subjectId: string): Observable<QuizQuestion[]> {
    return this.getQuizData().pipe(
      map((data) => data.questionsBySubject?.[subjectId] ?? [])
    );
  }

  getSubjectMeta(subjectId: string): Observable<QuizSubjectMeta | undefined> {
    return this.getSubjects().pipe(
      map((subjects) => subjects.find((s) => s.id === subjectId))
    );
  }
}
