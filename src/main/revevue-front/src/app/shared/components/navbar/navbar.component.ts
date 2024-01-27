import { Component, ViewEncapsulation } from '@angular/core';
import { Router } from '@angular/router';

@Component({
    selector: 'app-nav-bar',
    templateUrl: './navbar.component.html',
    styleUrl: './navbar.component.scss',
    encapsulation: ViewEncapsulation.None
})
export class NavBarComponent {
    constructor(private router: Router) { }

    login(): void {
        this.router.navigateByUrl('/login');
    }

    signup(): void {
        this.router.navigateByUrl('/signup');
    }
}
