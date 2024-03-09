import { Component, inject, signal, ViewEncapsulation } from '@angular/core';
import { Router } from '@angular/router';
import { UserService } from "../../HttpServices";
import {QuestionService} from "../../question.service";

@Component({
    selector: 'app-nav-bar',
    templateUrl: './navbar.component.html',
    styleUrl: './navbar.component.scss',
    encapsulation: ViewEncapsulation.None
})
export class NavBarComponent {
    private router = inject(Router)
    userService = inject(UserService)
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

    isUserLogged(): boolean {
        return this.userService.isLogin()
    }

    search(): void {
        console.log('test')
        this.router.navigateByUrl('/questions/search').then()
        //this.questionService.searchQuestion(this.label()).subscribe(questions => this.router.navigateByUrl('/search-questions'));
    }

    updateLabel($event: any): void {
        this.label.set($event.target.value)
    }
}
