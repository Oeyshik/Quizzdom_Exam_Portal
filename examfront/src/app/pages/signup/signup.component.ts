import { Component, OnInit, OnDestroy } from '@angular/core';
import { FormControl, Validators } from '@angular/forms';
import { UserService } from 'src/app/services/user.service';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { MatSnackBar } from '@angular/material/snack-bar';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.css']
})
export class SignupComponent implements OnInit, OnDestroy {
  private ngUnsubscribe = new Subject<void>();

  constructor(private userService: UserService, private snack: MatSnackBar) {}

  public user = {
    username: '',
    firstName: '',
    lastName: '',
    email: '',
    password: '',
    phone: '',
  };

  hide = true;
  email = new FormControl('', [Validators.required, Validators.email]);

  getErrorMessage() {
    if (this.email.hasError('required')) {
      return 'You must enter a value';
    }

    return this.email.hasError('email') ? 'Not a valid email' : '';
  }

  ngOnInit(): void {}

  ngOnDestroy() {
    this.ngUnsubscribe.next();
    this.ngUnsubscribe.complete();
  }

  formSubmit() {
    console.log(this.user);

    // Check if username is empty or null
    if (this.user.username === '' || this.user.username === null) {
      this.snack.open('User Name is Required !!', 'Close', {
        duration: 3000,
        verticalPosition: 'top',
        horizontalPosition: 'right',
      });
      return;
    }

    // Check if the username is already registered
    this.userService.isUsernameTaken(this.user.username)
      .pipe(takeUntil(this.ngUnsubscribe))
      .subscribe({
        next: (isTaken: boolean) => {
          if (isTaken) {
            this.snack.open('Username is already registered !!', 'Close', {
              duration: 3000,
              verticalPosition: 'top',
              horizontalPosition: 'right',
            });
            return;
          }
          // If username is not taken, proceed with registration
          this.proceedWithRegistration();
        },
        error: (error) => {
          console.error('Error checking username:', error);
          // On error, proceed with registration (let backend handle validation)
          this.proceedWithRegistration();
        }
      });
  }

  private proceedWithRegistration() {

    if (!this.email.valid || this.email.value == null) {
      // Show a snackbar error message for invalid email
      this.snack.open('Invalid Email Address !!', 'Close', {
        duration: 3000,
        verticalPosition: 'top',
        horizontalPosition: 'right',
      });
      return;
    }

    const emailValue = this.email.value;

    // Regular expression to validate email format
    const emailPattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}$/;

    if (!emailPattern.test(emailValue)) {
      // Show a snackbar error message for invalid email format
      this.snack.open('Invalid Email Format !!', 'Close', {
        duration: 3000,
        verticalPosition: 'top',
        horizontalPosition: 'right',
      });
      return;
    }

    if (this.user.phone === '' || this.user.phone === null || !/^\d{10}$/.test(this.user.phone)) {
      // Show a snackbar error message for invalid phone number
      this.snack.open('Invalid Phone Number !! Please enter a valid 10-digit phone number.', 'Close', {
        duration: 3000,
        verticalPosition: 'top',
        horizontalPosition: 'right',
      });
      return;
    }

    if (this.user.password === '' || this.user.password === null) {
      // Show a snackbar error message for an empty password
      this.snack.open('Password is Required !!', 'Close', {
        duration: 3000,
        verticalPosition: 'top',
        horizontalPosition: 'right',
      });
      return;
    }

    // Check password constraints
    const passwordPattern = /^(?=.*\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).{8,}$/;

    if (!passwordPattern.test(this.user.password)) {
      // Show a SweetAlert2 error message for password constraints
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




    // addUser: userService
    this.userService
      .addUser(this.user)
      .pipe(takeUntil(this.ngUnsubscribe))
      .subscribe({
        next: (data:any) => {
          // success
          console.log(data);
          Swal.fire('Success', 'User Registration is Successfull !! <br> User Id: ' + data.id, 'success');
        },
        error: (error) => {
          // error
          console.log(error);
          let errorMessage = 'Something Went Wrong !!';

          // Handle different error response formats
          if (error.error) {
            if (error.error.message) {
              errorMessage = error.error.message;
            } else if (error.error.error) {
              errorMessage = error.error.error;
            } else if (typeof error.error === 'string') {
              errorMessage = error.error;
            }
          } else if (error.message) {
            errorMessage = error.message;
          }

          // Check if it's a duplicate user error
          if (error.status === 409 || errorMessage.toLowerCase().includes('already present') ||
              errorMessage.toLowerCase().includes('already registered')) {
            errorMessage = 'Username is already registered. Please choose a different username.';
          }

          Swal.fire('Error', errorMessage, 'error');
        }
      });
  }
}
