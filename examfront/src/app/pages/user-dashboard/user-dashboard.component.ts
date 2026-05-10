import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { AuthService, LoginResponse } from 'src/app/services/auth.service';

@Component({
  selector: 'app-user-dashboard',
  templateUrl: './user-dashboard.component.html',
  styleUrls: ['./user-dashboard.component.css']
})
export class UserDashboardComponent implements OnInit {
  user: LoginResponse | null = null;

  get userInitials(): string {
    if (!this.user) {
      return '?';
    }
    const a = (this.user.firstName?.charAt(0) || '').toUpperCase();
    const b = (this.user.lastName?.charAt(0) || '').toUpperCase();
    const pair = `${a}${b}`;
    if (pair) {
      return pair;
    }
    return (this.user.username?.charAt(0) || '?').toUpperCase();
  }

  get rolesDisplay(): string {
    if (!this.user?.roles?.length) {
      return '—';
    }
    return this.user.roles
      .map((r) =>
        r
          .replace(/^ROLE_/i, '')
          .replace(/_/g, ' ')
          .toLowerCase()
          .replace(/\b\w/g, (c) => c.toUpperCase())
      )
      .join(', ');
  }

  constructor(
    private authService: AuthService,
    private router: Router,
    private snackBar: MatSnackBar
  ) {}

  actionComingSoon(feature: string): void {
    this.snackBar.open(`${feature} will be available in a future update.`, 'Dismiss', {
      duration: 4000,
    });
  }

  ngOnInit(): void {
    this.user = this.authService.getCurrentUser();
    if (!this.user || !this.authService.isNormal()) {
      this.router.navigate(['/login']);
    }
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
