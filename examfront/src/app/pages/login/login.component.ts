import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { AuthService } from 'src/app/services/auth.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  hide = true;
  loginData = {
    username: '',
    password: ''
  };

  constructor(
    private authService: AuthService,
    private snack: MatSnackBar,
    private router: Router
  ) {}

  ngOnInit(): void {
    // Redirect if already logged in
    if (this.authService.isLoggedIn()) {
      this.redirectBasedOnRole();
    }
  }

  formSubmit() {
    if (this.loginData.username.trim() === '' || this.loginData.username === null) {
      this.snack.open('Username is required !!', 'Close', {
        duration: 3000,
        verticalPosition: 'top',
        horizontalPosition: 'right',
      });
      return;
    }

    if (this.loginData.password.trim() === '' || this.loginData.password === null) {
      this.snack.open('Password is required !!', 'Close', {
        duration: 3000,
        verticalPosition: 'top',
        horizontalPosition: 'right',
      });
      return;
    }

    // Request to server to generate token
    this.authService.login(this.loginData).subscribe({
      next: (response: any) => {
        console.log('Login successful:', response);
        
        // Show success message
        Swal.fire('Success', 'Login Successful!', 'success');
        
        // Redirect based on role
        this.redirectBasedOnRole();
      },
      error: (error) => {
        console.error('Login error:', error);
        let errorMessage = 'Invalid Credentials !!';
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

  private redirectBasedOnRole(): void {
    const user = this.authService.getCurrentUser();
    if (user) {
      if (user.roles.includes('ADMIN')) {
        this.router.navigate(['/admin/dashboard']);
      } else if (user.roles.includes('NORMAL')) {
        this.router.navigate(['/user/dashboard']);
      } else {
        // Default to user dashboard
        this.router.navigate(['/user/dashboard']);
      }
    }
  }
}
