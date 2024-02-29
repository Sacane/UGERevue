import { Component, inject, ViewEncapsulation } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { UserService } from "../../shared/HttpServices";
import { Router } from "@angular/router";

@Component({
    selector: 'app-login',
    templateUrl: './login.component.html',
    styleUrl: './login.component.scss',
    encapsulation: ViewEncapsulation.None
})
export class LoginComponent {
    loginForm = new FormGroup({
        accountName: new FormControl('', [Validators.required]),
        password: new FormControl('', [Validators.required])
    });
    userService = inject(UserService)
    private router = inject(Router)

    login(): void {
        if (this.loginForm.valid) {
            this.userService.login({
                login: this.loginForm.value.accountName as string,
                password: this.loginForm.value.password as string
            }).subscribe(result => {
                this.router.navigateByUrl("/home").then()
            })
        }
    }
}
