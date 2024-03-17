import { Component, inject, signal, ViewEncapsulation } from '@angular/core';
import { Router } from '@angular/router';
import { LoginService } from "../../HttpServices";
import {UserService} from "../../../modules/profil/services/user.service";

@Component({
    selector: 'app-nav-bar',
    templateUrl: './navbar.component.html',
    styleUrl: './navbar.component.scss',
    encapsulation: ViewEncapsulation.None
})
export class NavBarComponent {
    loginService = inject(LoginService)
    userService = inject(UserService)
    private router = inject(Router)
    label = signal('undefined')
    username = signal('undefined')
    login(): void {
        this.router.navigateByUrl('/login').then();
    }

    signup(): void {
        this.router.navigateByUrl('/signup').then();
    }

    logout(): void {
        this.loginService.logout().subscribe(() => {
            this.router.navigateByUrl('/home').then();
        });
    }

    profile(): void {
        this.router.navigateByUrl('/profile').then();
    }

    isUserLogged(): boolean {
        return this.loginService.isLogin()
    }

    search(): void {
        const finalLabel = this.label() === '' ? 'undefined' : this.label()
        this.router.navigateByUrl('/questions/search/' + finalLabel + '/' + this.username(), { skipLocationChange: true }).then();
    }

    updateLabel($event: any): void {
        this.label.set($event.target.value)
    }

    updateUsername($event: any) {
        this.username.set($event.target.value)
    }

    gotoHome() {
        this.router.navigateByUrl('/home').then()
    }
}
