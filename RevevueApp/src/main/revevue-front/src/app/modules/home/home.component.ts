import { Component, inject } from '@angular/core';
import { UserService } from "../../shared/HttpServices";

@Component({
    selector: 'app-home',
    templateUrl: './home.component.html',
    styleUrl: './home.component.scss'
})
export class HomeComponent {
    private userService = inject(UserService)

    isLogged(): boolean {
        return this.userService.isLogin();
    }
}
