import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import baseUrl from './helper';
import {
  QuizAttemptDetail,
  QuizAttemptSubmitRequest,
  QuizAttemptSummary,
} from '../models/quiz-attempt.models';

@Injectable({
  providedIn: 'root',
})
export class QuizAttemptService {
  constructor(private http: HttpClient) {}

  submit(body: QuizAttemptSubmitRequest): Observable<QuizAttemptSummary> {
    return this.http.post<QuizAttemptSummary>(`${baseUrl}/quiz-attempt/`, body);
  }

  listForUser(userId: number): Observable<QuizAttemptSummary[]> {
    return this.http.get<QuizAttemptSummary[]>(`${baseUrl}/quiz-attempt/user/${userId}`);
  }

  getDetail(attemptId: number, userId: number): Observable<QuizAttemptDetail> {
    return this.http.get<QuizAttemptDetail>(
      `${baseUrl}/quiz-attempt/${attemptId}/user/${userId}`
    );
  }
}
