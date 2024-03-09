import {Component, inject, signal, ViewEncapsulation} from '@angular/core';
import { Router } from '@angular/router';
import { UserService } from "../../HttpServices";

@Component({
    selector: 'app-nav-bar',
    templateUrl: './navbar.component.html',
    styleUrl: './navbar.component.scss',
    encapsulation: ViewEncapsulation.None
})
export class NavBarComponent {
    userService = inject(UserService)
    private router = inject(Router)
    label = signal('')

    login(): void {
        this.router.navigateByUrl('/login').then();
    }

    signup(): void {
        this.router.navigateByUrl('/signup').then();
    }

    logout(): void {
        //TODO ajouter une modal de confirmation
        this.userService.logout().subscribe(() => {
            this.router.navigateByUrl('/home');
        });
    }

    profile(): void {
        this.router.navigateByUrl('/profile').then();
    }

    isUserLogged(): boolean {
        return this.userService.isLogin()
    }

    search(): void {
        console.log(this.label())
        this.router.navigateByUrl('/questions/search/' + this.label(), { skipLocationChange: true }).then();
        //this.questionService.searchQuestion(this.label()).subscribe(questions => this.router.navigateByUrl('/search-questions'));
    }

    updateLabel($event: any): void {
        this.label.set($event.target.value)
    }
}
