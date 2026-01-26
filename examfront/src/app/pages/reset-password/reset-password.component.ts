import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { UserService } from 'src/app/services/user.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-reset-password',
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.css']
})
export class ResetPasswordComponent implements OnInit {

  resetPasswordData = {
    username: '',
    resetToken: '',
    newPassword: '',
    confirmPassword: ''
  };

  hide = true;
  hideConfirm = true;

  constructor(
    private userService: UserService,
    private snack: MatSnackBar,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    // Get token from query params if available
    this.route.queryParams.subscribe(params => {
      if (params['token']) {
        this.resetPasswordData.resetToken = params['token'];
      }
    });
  }

  formSubmit() {
    if (this.resetPasswordData.username.trim() === '' || this.resetPasswordData.username === null) {
      this.snack.open('Username is required !!', 'Close', {
        duration: 3000,
        verticalPosition: 'top',
        horizontalPosition: 'right',
      });
      return;
    }

    if (this.resetPasswordData.resetToken.trim() === '' || this.resetPasswordData.resetToken === null) {
      this.snack.open('Reset token is required !!', 'Close', {
        duration: 3000,
        verticalPosition: 'top',
        horizontalPosition: 'right',
      });
      return;
    }

    if (this.resetPasswordData.newPassword.trim() === '' || this.resetPasswordData.newPassword === null) {
      this.snack.open('New password is required !!', 'Close', {
        duration: 3000,
        verticalPosition: 'top',
        horizontalPosition: 'right',
      });
      return;
    }

    if (this.resetPasswordData.newPassword !== this.resetPasswordData.confirmPassword) {
      this.snack.open('Passwords do not match !!', 'Close', {
        duration: 3000,
        verticalPosition: 'top',
        horizontalPosition: 'right',
      });
      return;
    }

    // Check password constraints
    const passwordPattern = /^(?=.*\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).{8,}$/;
    if (!passwordPattern.test(this.resetPasswordData.newPassword)) {
      Swal.fire({
        icon: 'error',
        title: 'Password Error',
        html: 'Password must meet the following criteria:<br>' +
          '- At least 8 characters in length.<br>' +
          '- At least one uppercase letter.<br>' +
          '- At least one lowercase letter.<br>' +
          '- At least one number.<br>' +
          '- At least one special character (@#$%^&+=).',
        confirmButtonText: 'Close'
      });
      return;
    }

    this.userService.resetPassword(
      this.resetPasswordData.username,
      this.resetPasswordData.resetToken,
      this.resetPasswordData.newPassword
    ).subscribe({
      next: (response: any) => {
        console.log('Password reset successful:', response);
        Swal.fire('Success', 'Password reset successfully! Please login with your new password.', 'success')
          .then(() => {
            this.router.navigate(['/login']);
          });
      },
      error: (error) => {
        console.error('Reset password error:', error);
        let errorMessage = 'Failed to reset password !!';
        if (error.error && error.error.message) {
          errorMessage = error.error.message;
        }
        this.snack.open(errorMessage, 'Close', {
          duration: 3000,
          verticalPosition: 'top',
          horizontalPosition: 'right',
        });
      }
    });
  }

  goToLogin() {
    this.router.navigate(['/login']);
  }
}
