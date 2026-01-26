import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { UserService } from 'src/app/services/user.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-forgot-password',
  templateUrl: './forgot-password.component.html',
  styleUrls: ['./forgot-password.component.css']
})
export class ForgotPasswordComponent {

  forgotPasswordData = {
    username: '',
    email: ''
  };

  resetToken: string | null = null;
  showResetForm = false;

  constructor(
    private userService: UserService,
    private snack: MatSnackBar,
    public router: Router
  ) {}

  formSubmit() {
    if (this.forgotPasswordData.username.trim() === '' || this.forgotPasswordData.username === null) {
      this.snack.open('Username is required !!', 'Close', {
        duration: 3000,
        verticalPosition: 'top',
        horizontalPosition: 'right',
      });
      return;
    }

    this.userService.forgotPassword(this.forgotPasswordData.username, this.forgotPasswordData.email)
      .subscribe({
        next: (response: any) => {
          console.log('Password reset token:', response);
          
          // Store token for reset password page
          if (response.resetToken) {
            this.resetToken = response.resetToken;
            this.showResetForm = true;
            
            Swal.fire({
              icon: 'success',
              title: 'Reset Token Generated',
              html: `Token: <strong>${response.resetToken}</strong><br><br>Please copy this token and use it to reset your password.`,
              confirmButtonText: 'Continue to Reset'
            });
          } else {
            Swal.fire('Success', 'Password reset instructions sent!', 'success');
            this.router.navigate(['/login']);
          }
        },
        error: (error) => {
          console.error('Forgot password error:', error);
          let errorMessage = 'Failed to process request !!';
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
