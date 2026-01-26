import { Component } from '@angular/core';
import { ThemeService } from 'src/app/theme.service';
import { Router } from '@angular/router';


@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent {
  mode: 'light' | 'dark' = 'light';

  constructor(
    private themeService: ThemeService,
    private router: Router) {
    this.themeService.isDarkTheme$.subscribe(isDark => {
      this.mode = isDark ? 'dark' : 'light'; // Set the 'mode' property based on the theme
    });
  }

  toggleMode() {
    this.themeService.toggleDarkTheme();
  }

  navigateToLogin() {
    this.router.navigate(['/login']);
  }

  navigateToSignup() {
    this.router.navigate(['/signup']);
  }

  navigateToHome() {
    this.router.navigate(['/']);
  }
}
