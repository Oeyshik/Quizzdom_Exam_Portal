import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { SignupComponent } from './pages/signup/signup.component';
import { LoginComponent } from './pages/login/login.component';
import { HomeComponent } from './pages/home/home.component';
import { AdminDashboardComponent } from './pages/admin-dashboard/admin-dashboard.component';
import { UserDashboardComponent } from './pages/user-dashboard/user-dashboard.component';
import { ForgotPasswordComponent } from './pages/forgot-password/forgot-password.component';
import { ResetPasswordComponent } from './pages/reset-password/reset-password.component';
import { QuizSubjectSelectionComponent } from './pages/quiz-subject-selection/quiz-subject-selection.component';
import { QuizExamComponent } from './pages/quiz-exam/quiz-exam.component';
import { QuizResultsListComponent } from './pages/quiz-results-list/quiz-results-list.component';
import { QuizResultDetailComponent } from './pages/quiz-result-detail/quiz-result-detail.component';
import { adminGuard, userGuard } from './guards/auth.guard';

const routes: Routes = [
  {
    path: '',
    component: HomeComponent,
    pathMatch: 'full',
  },
  {
    path: 'signup',
    component: SignupComponent,
    pathMatch: 'full',
  },
  {
    path: 'login',
    component: LoginComponent,
    pathMatch: 'full',
  },
  {
    path: 'forgot-password',
    component: ForgotPasswordComponent,
    pathMatch: 'full',
  },
  {
    path: 'reset-password',
    component: ResetPasswordComponent,
    pathMatch: 'full',
  },
  {
    path: 'admin/dashboard',
    component: AdminDashboardComponent,
    canActivate: [adminGuard],
  },
  {
    path: 'user/dashboard',
    component: UserDashboardComponent,
    canActivate: [userGuard],
  },
  {
    path: 'user/quiz/subjects',
    component: QuizSubjectSelectionComponent,
    canActivate: [userGuard],
  },
  {
    path: 'user/quiz/exam/:subjectId',
    component: QuizExamComponent,
    canActivate: [userGuard],
  },
  {
    path: 'user/quiz/results',
    component: QuizResultsListComponent,
    canActivate: [userGuard],
  },
  {
    path: 'user/quiz/results/:attemptId',
    component: QuizResultDetailComponent,
    canActivate: [userGuard],
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
